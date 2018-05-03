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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class ShowBooks extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private static final String TAG = "ShowBooks";

    private int USER_BOOKS = 0, SEARCH_BOOKS = 2, requestCode;

    /**
     * Books retrieved from a Firebase query.
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

        if (requestCode == USER_BOOKS)
            book_list = BookManager.getUserBooks();
        else if (requestCode == SEARCH_BOOKS) {
            book_list = BookManager.getSearchBooks();
        }

        // Map button click listener
        findViewById(R.id.books_map_button).setOnClickListener(v -> showMap());
    }

    /**
     * Displaying books' position on a map
     */
    private void showMap() {
        Intent mapsIntent = new Intent(getApplicationContext(), BooksMapActivity.class);

        // Retrieving all users IDs
        ProfileManager profileManager = ProfileManager.getInstance();
        ArrayList<String> userId = new ArrayList<String>();
        for(Book book: book_list) {
            if( book.getUid() != null &&
                !book.getUid().isEmpty() &&
                book.getUid() != "" &&
                profileManager.getProfile(book.getUid()).getPosition() != null) {
                    userId.add(book.getUid());
            }
        }
        mapsIntent.putExtra("key", userId);

        // Launching the Maps with the right markers
        startActivity(mapsIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ListView bookListView = findViewById(R.id.books_listview);

        // set the list view to show all the books

        if (book_list != null) {

            Log.d(TAG, "book_list onStart ShowBooks" + book_list.toString());

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
                        view = getLayoutInflater().inflate(R.layout.books_adapter_layout, viewGroup, false);

                    TextView title = (TextView) view.findViewById(R.id.book_title);
                    ImageView photo = (ImageView) view.findViewById(R.id.book_photo);
                    title.setText(book_list.get(i).getTitle());
                    Picasso.get().load(book_list.get(i).getImage()).fit().centerCrop().into(photo);

                    if (requestCode == SEARCH_BOOKS) {

                        TextView sharingUser = (TextView) view.findViewById(R.id.sharing_user);
                        String uid = book_list.get(i).getUid();
                        sharingUser.setText("Shared by: " + pm.getProfile(uid).getName());

                    }

                    // start the activity "Show Book" passing the current book in the Intent

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent showBook = new Intent(getApplicationContext(), ShowBook.class);

                            showBook.putExtra("book", book_list.get(i));
                            //showBook.putExtra("bookPhoto",bookVals.get("image"));
                            startActivity(showBook);


                        }
                    });
                    return view;
                }
            });
        }


    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
