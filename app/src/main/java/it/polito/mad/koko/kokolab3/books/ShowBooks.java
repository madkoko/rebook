package it.polito.mad.koko.kokolab3.books;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class ShowBooks extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private static final String TAG = "ShowBooks";

    private int USER_BOOKS = 0, SEARCH_BOOKS = 2, requestCode;

    private FirebaseListAdapter<Book> booksAdapter;

    /**
     * Books retrieved from a Firebase query or.
     * It can contain the user's books or a simple
     * query result.
     */
    private ArrayList<Book> book_list;

    private ProfileManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        pm = ProfileManager.getInstance();

        requestCode = getIntent().getIntExtra("request_code", -1);

        ListView bookListView = findViewById(R.id.books_listview);
        book_list = new ArrayList<>();

        // In case this activity is called by "SearchBooks"
        if (requestCode == SEARCH_BOOKS) {

            book_list = (ArrayList<Book>) getIntent().getExtras().get("searchedBooks");

            if (book_list != null && book_list.size() != 0) {

                bookListView.setAdapter(new BaseAdapter() {

                    @Override
                    public int getCount() {
                        return book_list.size();
                    }

                    @Override
                    public Object getItem(int i) {
                        return book_list.get(i);
                    }

                    @Override
                    public long getItemId(int i) {
                        return 0;
                    }

                    @Override
                    public View getView(final int i, View view, ViewGroup viewGroup) {
                        if (view == null)
                            view = getLayoutInflater().inflate(R.layout.search_books_adapter_layout, viewGroup, false);

                        createBooksView(view, book_list.get(i), null);

                        return view;
                    }
                });
            }

        }

        // In case this activity is called by "My Books"
        else if (requestCode == USER_BOOKS) {
            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Query userBooksQuery = FirebaseDatabase.getInstance().getReference().child("books").orderByChild("uid").equalTo(currentUserID);

            //FirebaseListOptions<Book> to retrieve books from firebase
            //query is reference
            FirebaseListOptions<Book> booksListOptions = new FirebaseListOptions.Builder<Book>()
                    .setLayout(R.layout.my_books_adapter_layout)
                    .setQuery(userBooksQuery, Book.class)
                    .build();

            //FirebaseListAdapter to create ListAdapter Ui from firebaseUi
            booksAdapter = new FirebaseListAdapter<Book>(booksListOptions) {

                @Override
                protected void populateView(View view, Book model, int position) {

                    Log.d(TAG, booksAdapter.getRef(position).getKey());

                    createBooksView(view, model, position);

                    // Insert the current Book (model) into an array to use it in "showMap"
                    // IN MY_BOOKS "MAP" BUTTON IS NOT VISIBLE ANYMORE
                    book_list.add(model);

                }
            };
            bookListView.setAdapter(booksAdapter);
        }
        ;

    }

    /**
     * Method to create the view in both cases user's or searched books
     *
     * @param view  view to be created
     * @param model book to populate the views
     */
    private void createBooksView(View view, Book model, Integer position) {

        if (requestCode == SEARCH_BOOKS) {
            TextView searchBookTitle = (TextView) view.findViewById(R.id.search_book_title);
            ImageView searchBookPhoto = (ImageView) view.findViewById(R.id.search_book_photo);
            String sharable = model.getSharable();
            searchBookTitle.setText(model.getTitle());
            Picasso.get().load(model.getImage()).fit().centerCrop().into(searchBookPhoto);

            TextView sharingUser = (TextView) view.findViewById(R.id.search_book_sharing_user);
            String sharedBy = "Shared by: " + model.getBookOwner().getName() + "\n---------------------" +
                    "\n" + model.getBookOwner().getLocation();
            sharingUser.setText(sharedBy);

            // Checks if the book is available for sharing
            // If it's available, the title is displayed green;
            // otherwise, the title is displayed red
            if (sharable.equalsIgnoreCase("yes"))
                searchBookTitle.setTextColor(Color.GREEN);

            else
                searchBookTitle.setTextColor(Color.RED);
            // start the activity "Show Book" passing the current book in the Intent
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent showBook = new Intent(getApplicationContext(), ShowBook.class);

                    showBook.putExtra("book", model);
                    //showBook.putExtra("bookPhoto",bookVals.get("image"));
                    startActivity(showBook);
                }
            });

            // Map button click listener
            findViewById(R.id.books_map_button).setOnClickListener(v -> showMap());

        } else if (requestCode == USER_BOOKS) {
            TextView myBookTitle = (TextView) view.findViewById(R.id.my_book_title);
            ImageView myBookPhoto = (ImageView) view.findViewById(R.id.my_book_photo);
            String sharable = model.getSharable();
            myBookTitle.setText(model.getTitle());
            Picasso.get().load(model.getImage()).fit().centerCrop().into(myBookPhoto);

            Button deleteBookButton = (Button) view.findViewById(R.id.delete_my_book);

            // set the listener to the delete button with an alert dialog
            deleteBookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ShowBooks.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(ShowBooks.this);
                    }
                    builder.setTitle("Delete Book")
                            .setMessage("Are you sure you want to delete this book?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    BookManager.removeBook(String.valueOf(booksAdapter.getRef(position).getKey()));
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent editBook = new Intent(getApplicationContext(), EditBook.class);

                    editBook.putExtra("updatingBook", model);
                    editBook.putExtra("bookKey", booksAdapter.getRef(position).getKey());
                    //showBook.putExtra("bookPhoto",bookVals.get("image"));
                    startActivity(editBook);
                }
            });

            findViewById(R.id.books_map_button).setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (requestCode == USER_BOOKS)
            booksAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestCode == USER_BOOKS)
            booksAdapter.stopListening();
    }

    /**
     * Displaying books' position on a map
     */
    private void showMap() {
        Intent mapsIntent = new Intent(getApplicationContext(), BooksMapActivity.class);

        // Retrieving all users IDs
        // ArrayList<String> userId = new ArrayList<String>();

        // Map with key: username - value: user position passed to BooksMapActivity
        // to show user in google map
        HashMap<String, String> sharingUsersPositions = new HashMap<>();

        for (Book book : book_list) {
            if (book.getUid() != null &&
                    !book.getUid().isEmpty() &&
                    book.getUid() != "") {

                String userPosition = book.getBookOwner().getPosition();
                String userName = book.getBookOwner().getName();

                /*if (userPosition != null)
                    userId.add(book.getUid());*/
                sharingUsersPositions.put(userName, userPosition);
            }
        }
        mapsIntent.putExtra("sharingUsersPositions", sharingUsersPositions);

        // Launching the Maps with the right markers
        startActivity(mapsIntent);
    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}
