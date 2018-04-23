package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;

public class ShowBooks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

    }

    @Override
    protected void onStart() {
        super.onStart();

        final HashMap<String,Book> books=(HashMap<String, Book>) BookManager.getBooks();
        final List<String> bookTitles = new ArrayList<>();
        final List<String> bookPhotos=new ArrayList<>();
        ListView bookListView = findViewById(R.id.books_listview);

        // set the list view to show all the books

        if(books!=null) {
            for (Object ob : books.values().toArray()) {
                Map<String, String> book = (HashMap<String, String>) ob;
                String title;
                String bookPhoto;
                title = book.get("title");
                bookPhoto=book.get("image");
                bookTitles.add(title);
                bookPhotos.add(bookPhoto);
            }

            bookListView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return books.size();
                }

                @Override
                public Object getItem(int i) {
                    return bookTitles.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    if (view == null)
                        view = getLayoutInflater().inflate(R.layout.books_adapter_layout, viewGroup, false);

                    TextView title = (TextView) view.findViewById(R.id.book_title);
                    ImageView photo=(ImageView) view.findViewById(R.id.book_photo);
                    title.setText(bookTitles.get(i));
                    Picasso.get().load(bookPhotos.get(i)).fit().centerCrop().into(photo);

                    final int position = i;

                    // start the activity "Show Book" passing the current book in the Intent

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent showBook = new Intent(getApplicationContext(), ShowBook.class);

                            Book book;
                            for (Object ob : books.values().toArray()) {
                                Map<String, String> bookVals = (HashMap<String, String>) ob;

                                String isbn, title, author, publisher, editionYear, bookConditions, uid;
                                title = bookVals.get("title");

                                if (title.equalsIgnoreCase(bookTitles.get(position))) {

                                    isbn = bookVals.get("isbn");
                                    author = bookVals.get("author");
                                    publisher = bookVals.get("publisher");
                                    editionYear = bookVals.get("editionYear");
                                    bookConditions = bookVals.get("bookConditions");
                                    uid = bookVals.get("uid");
                                    book = new Book(isbn, title, author, publisher, editionYear, bookConditions, uid);

                                    showBook.putExtra("book", book);
                                    showBook.putExtra("bookPhoto",bookVals.get("image"));
                                    startActivity(showBook);
                                }

                            }

                        }
                    });
                    return view;
                }
            });
        }
    }

}
