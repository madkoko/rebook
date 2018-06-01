package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.twitter.sdk.android.core.models.Search;

import java.util.Iterator;
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.util.JsonUtil;

public class SearchBooks extends AppCompatActivity {

    private static final String TAG = "SearchBooks";

    private int SEARCH_BOOKS=2;

    private EditText title;
    private EditText author;
    private EditText publisher;
    private EditText editionYear;
    private EditText conditions;

    /**
     * ArrayList with all the books that match the keywords
     */
    private Map<String,Book> searchedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        title=findViewById(R.id.search_book_title);
        author=findViewById(R.id.search_book_author);
        publisher=findViewById(R.id.search_book_publisher);
        editionYear=findViewById(R.id.search_book_edition_year);
        conditions=findViewById(R.id.search_book_conditions);

        Button searchButton=findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().equals("")&&author.getText().toString().equals("")&&publisher.getText().toString().equals("")&&editionYear.getText().toString().equals("")&&conditions.getText().toString().equals(""))
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
    private void searchBooks(){

        searchedBooks=BookManager.getAllBooks();

        Iterator<Book> booksIterator=searchedBooks.values().iterator();
        while(booksIterator.hasNext()){
            if(!bookMatchesKeywords(booksIterator.next()))
                booksIterator.remove();
        }

        // Starts the activity "ShowBooks" with his request code
        // and putting the filtered books array list in the intent
        Intent showSearchBooks = new Intent(getApplicationContext(), ShowBooks.class);
        showSearchBooks.putExtra("request_code", SEARCH_BOOKS);

        showSearchBooks.putExtra("searchedBooks", JsonUtil.serialize(searchedBooks));
        startActivity(showSearchBooks);

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

        if(book.getUid().equalsIgnoreCase(currentUserID)) {
            return false;
        }
        else if (!title.equals("")) {
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

}
