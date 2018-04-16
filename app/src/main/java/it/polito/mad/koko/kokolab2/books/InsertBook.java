package it.polito.mad.koko.kokolab2.books;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.polito.mad.koko.kokolab2.R;

public class InsertBook extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);

        Button addButton=findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBook();
            }
        });
    }

    public void createBook(){

        EditText bookIsbn=findViewById(R.id.edit_book_ISBN);
        EditText bookTitle=findViewById(R.id.edit_book_title);
        EditText bookAuthor=findViewById(R.id.edit_book_author);
        EditText bookPublisher=findViewById(R.id.edit_book_publisher);
        EditText bookEditionYear=findViewById(R.id.edit_book_edition_year);
        EditText bookConditions=findViewById(R.id.edit_book_conditions);

        String isbn=bookIsbn.getText().toString();
        String title=bookTitle.getText().toString();
        String author=bookAuthor.getText().toString();
        String publisher=bookPublisher.getText().toString();
        String editionYear=bookEditionYear.getText().toString();
        String conditions=bookConditions.getText().toString();

        Book book=new Book(isbn,title,author,publisher,editionYear,conditions);

        BookManager.insertBook(book);

        finish();

    }
}
