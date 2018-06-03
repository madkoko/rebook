package it.polito.mad.koko.kokolab3.profile.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;

@SuppressLint("ValidFragment")
class TabComments extends Fragment {
    private final String uid;
    private Query query;
    private FirebaseListAdapter adapter;

    public TabComments(String uid) {
        this.uid=uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_books, container, false);

        ListView comentsListView = view.findViewById(R.id.books_listview);

        //query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("uid").equalTo(uid);

        /*FirebaseListOptions<> options = new FirebaseListOptions.Builder<Book>()
                .setLayout(R.layout.books_adapter_layout)
                .setQuery(query, .class)
                .build();
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

            }
        };
        comentsListView.setAdapter(adapter);*/

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
