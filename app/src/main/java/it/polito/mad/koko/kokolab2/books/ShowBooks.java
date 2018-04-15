package it.polito.mad.koko.kokolab2.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import it.polito.mad.koko.kokolab2.R;

public class ShowBooks extends AppCompatActivity {

    //private ArrayList<Book> books;
    //private String[] booksTitle;
    private ListView bookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //books=BookManager.getBooks();
        //booksTitle=BookManager.getBooksTitle();

        BookManager.printBooks();

        // int bookNum=BookManager.getBooksNumber();

        final Map<String,Book> books=BookManager.getBooks();

        final Book[] bookList=books.values().toArray(new Book[books.size()]);

        /*for(int i=0;i<BookManager.getBooks().length;i++){
            Log.d("debug",BookManager.getBooks()[i].toString());
            books[i]=BookManager.getBooks()[i];
        }*/

        bookListView =findViewById(R.id.books_listview);
        bookListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return books.size();
            }

            @Override
            public Object getItem(int i) {
                return bookList[i];
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view==null)
                    view=getLayoutInflater().inflate(R.layout.books_adapter_layout,viewGroup, false);

                TextView title=(TextView) view.findViewById(R.id.book_title);
                Button show=(Button)view.findViewById(R.id.show_book_button);

                title.setText(bookList[i].getTitle());

                final int position=i;

                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent showBook=new Intent(getApplicationContext(),ShowBook.class);
                        showBook.putExtra("book",bookList[position]);
                        startActivity(showBook);

                    }
                });
                return view;
            }
        });
    }

}
