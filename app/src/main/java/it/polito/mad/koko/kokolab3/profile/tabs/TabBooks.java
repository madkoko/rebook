package it.polito.mad.koko.kokolab3.profile.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.BookManager;

@SuppressLint("ValidFragment")
public class TabBooks extends Fragment {

    private String uid;
    private Query query;
    private BookTabAdapter bookTabAdapter;

    public TabBooks(String uid) {
        this.uid=uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_books, container, false);

        ListView bookListView = view.findViewById(R.id.books_listview);

        query = FirebaseDatabase.getInstance().getReference().child("books").orderByChild("uid").equalTo(uid);
        FirebaseListOptions<Book> options = new FirebaseListOptions.Builder<Book>()
                .setLayout(R.layout.my_books_adapter_layout)
                .setQuery(query, Book.class)
                .build();

        bookTabAdapter = new BookTabAdapter(options, getContext());

        bookListView.setAdapter(bookTabAdapter);

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        bookTabAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        bookTabAdapter.stopListening();
    }
}
