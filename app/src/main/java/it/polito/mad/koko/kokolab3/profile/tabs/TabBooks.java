package it.polito.mad.koko.kokolab3.profile.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.BookManager;

public class TabBooks extends Fragment {
    private ArrayList<Book> myBooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_books, container, false);


        /**
         * TODO: modify the view with Firebase List Adapter (getUserBooks() doesn't work anymore
         */
        //myBooks = BookManager.getUserBooks();
        ListView bookListView = view.findViewById(R.id.books_listview);

        // set the list view to show all the books

        if (myBooks != null) {
            BookTabAdapter bookTabAdapter = new BookTabAdapter(myBooks, getContext());
            bookListView.setAdapter(bookTabAdapter);
        }
        return view;
    }


}
