package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.messaging.MessageManager;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;
import it.polito.mad.koko.kokolab3.util.JsonUtil;

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

    private Map<String, Book> bookMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        requestCode = getIntent().getIntExtra("request_code", -1);

        ListView bookListView = findViewById(R.id.books_listview);
        book_list = new ArrayList<>();

        // In case this activity is called by "SearchBooks"
        if (requestCode == SEARCH_BOOKS) {

            bookMap = JsonUtil.deserializeBooks(getIntent().getStringExtra("searchedBooks"));

            if (bookMap != null && bookMap.size() == 0) {
                Toast.makeText(getApplicationContext(), "No books found", Toast.LENGTH_LONG).show();
                finish();
            } else {


                bookListView.setAdapter(new BaseAdapter() {

                    @Override
                    public int getCount() {
                        return bookMap.size();
                    }

                    @Override
                    public Object getItem(int i) {
                        return bookMap.keySet().toArray()[i];
                    }

                    @Override
                    public long getItemId(int i) {
                        return 0;
                    }

                    @Override
                    public View getView(final int i, View view, ViewGroup viewGroup) {
                        if (view == null)
                            view = getLayoutInflater().inflate(R.layout.books_adapter_layout, viewGroup, false);

                        createBooksView(view, bookMap.get(bookMap.keySet().toArray()[i]), (String) bookMap.keySet().toArray()[i]);

                        TextView sharingUser = (TextView) view.findViewById(R.id.sharing_user);
                        String uid = bookMap.get(bookMap.keySet().toArray()[i]).getUid();
                        String sharedBy = "Shared by: " + ProfileManager.getProfile(uid).getName();
                        sharingUser.setText(sharedBy);
                        sharingUser.setOnClickListener(v -> {
                            Intent showProfile = new Intent(getApplicationContext(), ShowProfile.class);
                            showProfile.putExtra("UserID", uid);
                            ProfileManager.retriveInformationUser(uid);
                            startActivity(showProfile);
                        });
                        return view;
                    }
                });
            }
        }

        // In case this activity is called by "My Books"
        else if (requestCode == USER_BOOKS) {
            String currentUserID = ProfileManager.getCurrentUserID();
            Query userBooksQuery = FirebaseDatabase.getInstance().getReference().child("books").orderByChild("uid").equalTo(currentUserID);

            // FirebaseListOptions<Book> to retrieve books from firebase
            // query is reference
            FirebaseListOptions<Book> booksListOptions = new FirebaseListOptions.Builder<Book>()
                    .setLayout(R.layout.books_adapter_layout)
                    .setQuery(userBooksQuery, Book.class)
                    .build();

            // FirebaseListAdapter to create ListAdapter Ui from firebaseUi
            booksAdapter = new FirebaseListAdapter<Book>(booksListOptions) {

                @Override
                protected void populateView(View view, Book model, int position) {

                    Log.d(TAG, booksAdapter.getRef(position).getKey());

                    createBooksView(view, model, null);

                    // Insert the current Book (model) into an array to use it in "showMap"
                    book_list.add(model);

                }
            };
            bookListView.setAdapter(booksAdapter);
        }
        ;

        // Map button click listener
        findViewById(R.id.books_map_button).setOnClickListener(v -> showMap());
    }

    /**
     * Method to create the view in both cases user's or searched books
     *
     * @param view  view to be created
     * @param model book to populate the views
     */
    private void createBooksView(View view, Book model, String bookId) {
        TextView title = (TextView) view.findViewById(R.id.book_title);
        ImageView photo = (ImageView) view.findViewById(R.id.book_photo);
        title.setText(model.getTitle());
        Picasso.get().load(model.getImage()).fit().centerCrop().into(photo);


        // start the activity "Show Book" passing the current book in the Intent
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = getChatInfo(model);
                Boolean chatFlag = false;

                //MessageManager.createChat(i, model.getTitle(), chatFlag);
                MessageManager.checkExistingChat(i.getExtras().getString("senderId"), i.getExtras().getString("receiverId"));
                // * Ho messo in MessageManager.chatID il chatID nel caso in cui sia stato trovato, se no è null.

                Intent showBook = new Intent(getApplicationContext(), ShowBook.class);
                showBook.putExtra("book", model);

                if (bookId != null)
                    showBook.putExtra("bookId", bookId);

                //showBook.putExtra("bookPhoto",bookVals.get("image"));
                startActivity(showBook);

            }
        });
    }

    /*  Method to retrieve Sender & Receiver info to Start / Resume their chat  */
    private Intent getChatInfo(Book book) {

        Intent i = new Intent();

        // Sender Info
        String senderId = FirebaseAuth.getInstance().getUid();
        Profile senderProfile = ProfileManager.getProfile(senderId);
        String senderUsername = senderProfile.getName();
        String senderImage = senderProfile.getImage();
        String senderToken = senderProfile.getTokenMessage();

        // Receiver Info
        String receiverId = book.getUid();
        Profile receiverProfile = ProfileManager.getProfile(receiverId);
        String receiverImage = receiverProfile.getImage();
        String receiverUsername = receiverProfile.getName();
        String receiverToken = receiverProfile.getTokenMessage();

        // 2. Put Sender & Receiver info into Intent
        i.putExtra("senderId", senderId);
        i.putExtra("senderUsername", senderUsername);
        i.putExtra("senderImage", senderImage);
        i.putExtra("senderToken", senderToken);
        i.putExtra("receiverId", receiverId);
        i.putExtra("receiverUsername", receiverUsername);
        i.putExtra("receiverImage", receiverImage);
        i.putExtra("receiverToken", receiverToken);

        return i;

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
        ArrayList<String> userId = new ArrayList<String>();
        for (Book book : book_list) {
            if (book.getUid() != null &&
                    !book.getUid().isEmpty() &&
                    book.getUid() != "") {

                Profile profile = ProfileManager.getProfile(book.getUid());
                String userPosition = profile.getPosition();

                if (userPosition != null)
                    userId.add(book.getUid());
            }
        }
        mapsIntent.putExtra("key", userId);

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
