package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//import com.twitter.sdk.android.core.models.Search;

import it.polito.mad.koko.kokolab3.R;

public class SearchBooks extends AppCompatActivity {

    private EditText title;
    private EditText author;
    private EditText publisher;
    private EditText editionYear;
    private EditText conditions;

    private int SEARCH_BOOKS=2;

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
                searchBooks();
            }
        });

    }

    public void searchBooks(){

        //BookManager.removeSearchBooksEventListener();

        BookManager.setSearchKeywords(
            title.getText().toString(),
            author.getText().toString(),
            publisher.getText().toString(),
            editionYear.getText().toString(),
            conditions.getText().toString())
        ;

        Intent showSearchBooks = new Intent(getApplicationContext(), ShowBooks.class);
        showSearchBooks.putExtra("request_code", SEARCH_BOOKS);
        startActivity(showSearchBooks);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //BookManager.populateSearchBooks();
    }
}
