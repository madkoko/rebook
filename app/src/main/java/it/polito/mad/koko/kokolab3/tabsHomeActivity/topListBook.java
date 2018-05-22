package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;

public class topListBook extends Fragment{


    private FirebaseListAdapter<Book> adapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.top_book_fragment, container, false);
        listView = rootView.findViewById(R.id.list_home);
        return rootView;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ListView mRecyclerView = getActivity().findViewById(R.id.list_home);


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("books");
        FirebaseListOptions<Book> options =
                new FirebaseListOptions.Builder<Book>()
                        .setLayout(R.layout.top_book_fragment)
                        .setQuery(query, Book.class)
                        .build();

        adapter = new FirebaseListAdapter<Book>(options) {
            @Override
            protected void populateView(View v, Book model, int position) {
                ImageView imageView = v.findViewById(R.id.book_cover);
                Picasso.get().load(model.getImage()).into(imageView);
            }
        };
        // use a linear layout manager
        //LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(adapter);



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
