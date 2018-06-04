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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.util.JsonUtil;

public class SearchBooks extends AppCompatActivity {

    private static final String TAG = "SearchBooks";

    private int SEARCH_BOOKS = 2;

    private EditText title;
    private EditText author;
    private EditText publisher;
    private EditText editionYear;
    private EditText conditions;

    /**
     * ArrayList with all the books that match the keywords
     */
    private Map<String, Book> searchedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

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
                    Toast.makeText(getApplicationContext(), "Please insert a keyword", Toast.LENGTH_LONG).show();
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
        // Retrieving all books
        searchedBooks = BookManager.getAllBooks();
        Iterator<Book> booksIterator = searchedBooks.values().iterator();
        while (booksIterator.hasNext()) {
            if (!bookMatchesKeywords(booksIterator.next()))
                booksIterator.remove();
        }

        // If there is at least one book
        if (searchedBooks.size() != 0) {
            // Casting the book map into a list in order to order it by value
            LinkedList<Map.Entry<String, Book>> searchedBooksList = new LinkedList<>(searchedBooks.entrySet());

            // Ordering books by means of a custom comparator
            Collections.sort(searchedBooksList, (o1, o2) -> {

                String pos1 = o1.getValue().getBookOwner().getPosition();
                String pos2 = o2.getValue().getBookOwner().getPosition();

                String latBook1 = pos1.substring(pos1.indexOf("(") + 1, pos1.indexOf(","));
                String lngBook1 = pos1.substring(pos1.indexOf(",") + 1, pos1.indexOf(")"));
                double latitudeBook1 = Double.parseDouble(latBook1);
                double longitudeBook1 = Double.parseDouble(lngBook1);

                String latBook2 = pos2.substring(pos2.indexOf("(") + 1, pos2.indexOf(","));
                String lngBook2 = pos2.substring(pos2.indexOf(",") + 1, pos2.indexOf(")"));
                double latitudeBook2 = Double.parseDouble(latBook2);
                double longitudeBook2 = Double.parseDouble(lngBook2);

                String currentUserPosition = ProfileManager.getProfile().getPosition();
                String currentUserLocation = ProfileManager.getProfile().getLocation();

                Log.d(TAG, "Current user location: " + currentUserLocation + "\n ");

                double latitudeCurrentUser = Double.parseDouble(currentUserPosition.substring(currentUserPosition.indexOf("(") + 1, currentUserPosition.indexOf(",")));
                double longitudeCurrentUser = Double.parseDouble(currentUserPosition.substring(currentUserPosition.indexOf(",") + 1, currentUserPosition.indexOf(")")));

                //Log.d(TAG, "Latitude Book1: " + latitudeBook1 + "\nLatitude Book2: " + latitudeBook2);
                //Log.d(TAG, "Longitude Book1: " + longitudeBook1 + "\nLongitude Book2: " + longitudeBook2);

                //Log.d(TAG, "Latitude of current user: " + latitudeCurrentUser + "\nLongitude of current user: " + longitudeCurrentUser);

                Log.d(TAG, "Book1: " + o1.getValue().getTitle() + " - location: " + o1.getValue().getBookOwner().getLocation());
                Log.d(TAG, "Book2: " + o2.getValue().getTitle() + " - location: " + o2.getValue().getBookOwner().getLocation());

                double distanceFromBook1 = distance(latitudeBook1, longitudeBook1, latitudeCurrentUser, longitudeCurrentUser);
                double distanceFromBook2 = distance(latitudeBook2, longitudeBook2, latitudeCurrentUser, longitudeCurrentUser);

                Log.d(TAG, "Distance from Book1: " + distanceFromBook1);
                Log.d(TAG, "Distance from Book2: " + distanceFromBook2 + "\n ");

                return Double.compare(distanceFromBook1, distanceFromBook2);

            });

            // Sorted list to map, in order to serialize it into an Intent
            searchedBooks.clear();
            for (Iterator<Map.Entry<String, Book>> it = searchedBooksList.iterator(); it.hasNext();) {
                Map.Entry<String, Book> entry = (Map.Entry<String, Book>) it.next();
                searchedBooks.put(entry.getKey(), entry.getValue());
            }

            // Starts the activity "ShowBooks" with his request code
            // and putting the filtered books array list in the intent
            Intent showSearchBooks = new Intent(getApplicationContext(), ShowBooks.class);
            showSearchBooks.putExtra("request_code", SEARCH_BOOKS);
            showSearchBooks.putExtra("searchedBooks", JsonUtil.serialize(searchedBooks));
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

        String currentUserID = ProfileManager.getCurrentUserID();
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
