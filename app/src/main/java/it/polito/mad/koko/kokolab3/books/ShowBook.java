package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;

public class ShowBook extends AppCompatActivity {

    private static final String TAG = "ShowBook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book);

        TextView isbn,title,author,publisher,editionYear,conditions;
        ImageView bookImage;

        isbn=findViewById(R.id.show_book_isbn);
        title=findViewById(R.id.show_book_title);
        author=findViewById(R.id.show_book_author);
        publisher=findViewById(R.id.show_book_publisher);
        editionYear=findViewById(R.id.show_book_edition_year);
        conditions=findViewById(R.id.show_book_conditions);
        bookImage=findViewById(R.id.show_book_photo);

        Intent i=getIntent();
        Book book;
        if(i.getExtras().get("book")!=null) {
            book = (Book) i.getExtras().get("book");

            if(book.getISBN()!=null)isbn.setText(book.getISBN());
            if(book.getTitle()!=null)title.setText(book.getTitle());
            if(book.getAuthor()!=null)author.setText(book.getAuthor());
            if(book.getPublisher()!=null)publisher.setText(book.getPublisher());
            if(book.getEditionYear()!=null)editionYear.setText(book.getEditionYear());
            if(book.getBookConditions()!=null)conditions.setText(book.getBookConditions());
            Picasso.get().load(i.getExtras().get("bookPhoto").toString()).into(bookImage);
        }

    }
}
