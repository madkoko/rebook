package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
//import com.twitter.sdk.android.core.models.Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class SearchBooks extends AppCompatActivity {

    private static final String TAG = "SearchBooks";

    private int SEARCH_BOOKS = 2;

    private EditText title;
    private EditText author;
    private EditText publisher;
    private EditText editionYear;
    private EditText conditions;

    private ProfileManager pm;

    /**
     * ArrayList with all the books that match the keywords
     */
    private ArrayList<Book> searchedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        pm=ProfileManager.getInstance();

        pm.retrieveCurrentUser();

        title = findViewById(R.id.search_book_title);
        author = findViewById(R.id.search_book_author);
        publisher = findViewById(R.id.search_book_publisher);
        editionYear = findViewById(R.id.search_book_edition_year);
        conditions = findViewById(R.id.search_book_conditions);

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals("") && author.getText().toString().equals("") && publisher.getText().toString().equals("") && editionYear.getText().toString().equals("") && conditions.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Pleast insert a keyword", Toast.LENGTH_LONG).show();
                else {
                    searchBooks();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Retrieve all the books from Firebase
        // everytime the activity is restarted
        BookManager.populateAllBooks();

    }

    /**
     * Method that filters all the books with the keywords
     */
    private void searchBooks() {

        searchedBooks = BookManager.getAllBooks();

        Iterator<Book> booksIterator = searchedBooks.iterator();
        while (booksIterator.hasNext()) {
            if (!bookMatchesKeywords(booksIterator.next()))
                booksIterator.remove();
        }

        if (searchedBooks.size() != 0) {

            Collections.sort(searchedBooks, new Comparator<Book>() {
                @Override
                public int compare(Book o1, Book o2) {

                    String pos1 = o1.getBookOwner().getPosition();
                    String pos2 = o2.getBookOwner().getPosition();

                    String latBook1 = pos1.substring(pos1.indexOf("(") + 1, pos1.indexOf(","));
                    String lngBook1 = pos1.substring(pos1.indexOf(",") + 1, pos1.indexOf(")"));
                    double latitudeBook1 = Double.parseDouble(latBook1);
                    double longitudeBook1 = Double.parseDouble(lngBook1);

                    String latBook2 = pos2.substring(pos2.indexOf("(") + 1, pos2.indexOf(","));
                    String lngBook2 = pos2.substring(pos2.indexOf(",") + 1, pos2.indexOf(")"));
                    double latitudeBook2 = Double.parseDouble(latBook2);
                    double longitudeBook2 = Double.parseDouble(lngBook2);

                    String currentUserPosition=pm.getCurrentUser().getPosition();
                    String currentUserLocation=pm.getCurrentUser().getLocation();

                    Log.d(TAG,"Current user location: "+currentUserLocation+"\n ");

                    double latitudeCurrentUser = Double.parseDouble(currentUserPosition.substring(currentUserPosition.indexOf("(") + 1, currentUserPosition.indexOf(",")));
                    double longitudeCurrentUser = Double.parseDouble(currentUserPosition.substring(currentUserPosition.indexOf(",") + 1, currentUserPosition.indexOf(")")));

                    //Log.d(TAG, "Latitude Book1: " + latitudeBook1 + "\nLatitude Book2: " + latitudeBook2);
                    //Log.d(TAG, "Longitude Book1: " + longitudeBook1 + "\nLongitude Book2: " + longitudeBook2);

                    //Log.d(TAG, "Latitude of current user: " + latitudeCurrentUser + "\nLongitude of current user: " + longitudeCurrentUser);

                    Log.d(TAG,"Book1: "+o1.getTitle()+" - location: "+o1.getBookOwner().getLocation());
                    Log.d(TAG,"Book2: "+o2.getTitle()+" - location: "+o2.getBookOwner().getLocation());

                    double distanceFromBook1=distance(latitudeBook1, longitudeBook1, latitudeCurrentUser, longitudeCurrentUser);
                    double distanceFromBook2=distance(latitudeBook2, longitudeBook2, latitudeCurrentUser, longitudeCurrentUser);

                    Log.d(TAG,"Distance from Book1: " + distanceFromBook1);
                    Log.d(TAG,"Distance from Book2: " + distanceFromBook2+"\n ");

                    return Double.compare(distanceFromBook1, distanceFromBook2);

                }
            });

            // Starts the activity "ShowBooks" with his request code
            // and putting the filtered books array list in the intent
            Intent showSearchBooks = new Intent(getApplicationContext(), ShowBooks.class);
            showSearchBooks.putExtra("request_code", SEARCH_BOOKS);
            showSearchBooks.putExtra("searchedBooks", searchedBooks);
            startActivity(showSearchBooks);
        } else {
            Toast.makeText(getApplicationContext(), "No books found", Toast.LENGTH_LONG).show();
            recreate();
        }

    }

    /**
     * method to filter the books with the search keywords
     *
     * @param book book to which apply the search keywords
     * @return true if it matches one of the keywords, false otherwise
     */

    private boolean bookMatchesKeywords(Book book) {

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String title = this.title.getText().toString();
        String author = this.author.getText().toString();
        String publisher = this.publisher.getText().toString();
        String editionYear = this.editionYear.getText().toString();
        String conditions = this.conditions.getText().toString();

        if (book.getUid().equalsIgnoreCase(currentUserID)) {
            return false;
        } else if (!title.equals("")) {
            if (!BookManager.containsCaseInsensitive(book.getTitle(), title))
                return false;
        } else if (!author.equals("")) {
            if (!BookManager.containsCaseInsensitive(book.getAuthor(), author))
                return false;
        } else if (!publisher.equals("")) {
            if (!BookManager.containsCaseInsensitive(book.getPublisher(), publisher))
                return false;
        } else if (!editionYear.equals("")) {
            if (!BookManager.containsCaseInsensitive(book.getEditionYear(), editionYear))
                return false;
        } else if (!conditions.equals("")) {
            if (!BookManager.containsCaseInsensitive(book.getBookConditions(), conditions))
                return false;
        }

        return true;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist * 1.609344;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
