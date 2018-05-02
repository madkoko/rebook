package it.polito.mad.koko.kokolab3.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.BookManager;

public class TabBooks extends Fragment {
    private ProfileManager pm;
    private ArrayList<Book> myBooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_show_books, container, false);

        pm = ProfileManager.getInstance();
        myBooks = BookManager.getUserBooks();

        ListView bookListView = view.findViewById(R.id.books_listview);

        // set the list view to show all the books

        if (myBooks != null) {
            ProfileBookAdapter profileBookAdapter = new ProfileBookAdapter(myBooks, getContext());
            bookListView.setAdapter(profileBookAdapter);
        }
        return view;
    }


}
