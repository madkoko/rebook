package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;

public class HomeListBook extends Fragment {


    private static final String TAG = "topListBookFragment";
    private FirebaseListAdapter<Book> adapter;
    private ListView listView;
    private HomeBookAdapter adapterReycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.top_book_fragment, container, false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        RecyclerView mRecyclerViewTop = getActivity().findViewById(R.id.list_home_recyclerViewTop);
        RecyclerView mRecyclerViewBottom =getActivity().findViewById(R.id.list_home_recyclerViewBottom);
        Query queryRecycler = FirebaseDatabase.getInstance()
                .getReference()
                .child("books");

        /*queryRecycler.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Book> uidBooks= new ArrayList<>();
                for(DataSnapshot postDataSnapshot: dataSnapshot.getChildren() ){
                    String uid = postDataSnapshot.child("uid").getValue(String.class);
                    //Log.d(TAG, "auth"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if(uid.compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid())!=0) {
                        uidBooks.add(postDataSnapshot.getValue(Book.class));
                        Log.d(TAG, postDataSnapshot.child("uid").getValue(String.class));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<Book> optionsRecycler = new FirebaseRecyclerOptions.Builder<Book>()
                .setQuery(queryRecycler, Book.class)
                .build();
        adapterReycler = new HomeBookAdapter(optionsRecycler, 0,getActivity());

        // use a linear layout manager
        LinearLayoutManager mLayoutManagerTop = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerViewTop.setAdapter(adapterReycler);
        mRecyclerViewTop.setLayoutManager(mLayoutManagerTop);

        LinearLayoutManager mLayoutManagerBottom = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        mLayoutManagerBottom.setStackFromEnd(true);
        mRecyclerViewBottom.setAdapter(adapterReycler);
        mRecyclerViewBottom.setLayoutManager(mLayoutManagerBottom);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterReycler.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterReycler.stopListening();
    }

}
