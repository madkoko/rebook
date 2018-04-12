package it.polito.mad.koko.kokolab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class InsertBook extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

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

        /*Map<String, String> bookData = new HashMap<String, String>();

        bookData.put("ISBN",bookIsbn.getText().toString());
        bookData.put("Title", bookTitle.getText().toString());
        bookData.put("Author", bookAuthor.getText().toString());
        bookData.put("Publisher", bookPublisher.getText().toString());
        bookData.put("EditionYear",bookEditionYear.getText().toString());
        bookData.put("Conditions", bookConditions.getText().toString());*/

        /*DEBUG
        Log.d("debug", database.toString());
        Log.d("debug", ref.toString());
        Log.d("debug", booksRef.toString());
        Log.d("debug", bookData.toString());*/

        //booksRef.push().setValue(bookData);

        DatabaseReference booksRef = ref.child("books");

        Book book=new Book(isbn,title,author,publisher,editionYear,conditions);

        booksRef.push().setValue(book);

        finish();

    }
}
