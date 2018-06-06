package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;

public class HomeListBook extends Fragment {


    private static final String TAG = "topListBookFragment";
    private FirebaseListAdapter<Book> adapter;
    private ListView listView;
    private HomeBookAdapter adapterReyclerMostViewed;
    private HomeBookAdapter adapterReyclerRecentlyAdded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.top_book_fragment, container, false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        RecyclerView mRecyclerViewMostViewed = getActivity().findViewById(R.id.list_home_recyclerViewBottom);
        RecyclerView mRecyclerViewRecentlyAdded =getActivity().findViewById(R.id.list_home_recyclerViewTop);

        Query queryRecyclerRecentlyAdded = FirebaseDatabase.getInstance()
                .getReference()
                .child("books")
                .limitToLast(10);

        Query queryRecyclerMostViewed = FirebaseDatabase.getInstance()
                .getReference()
                .child("books")
                .orderByChild("visualizations")
                .limitToLast(5);

        FirebaseRecyclerOptions<Book> optionsRecyclerMostViewed = new FirebaseRecyclerOptions.Builder<Book>()
                .setQuery(queryRecyclerMostViewed, Book.class)
                .build();
        FirebaseRecyclerOptions<Book> optionsRecyclerRecentlyAdded = new FirebaseRecyclerOptions.Builder<Book>()
                .setQuery(queryRecyclerRecentlyAdded, Book.class)
                .build();
        adapterReyclerMostViewed = new HomeBookAdapter(optionsRecyclerMostViewed, 0,getActivity());
        adapterReyclerRecentlyAdded = new HomeBookAdapter(optionsRecyclerRecentlyAdded, 0,getActivity());

        // use a linear layout manager
        LinearLayoutManager mLayoutManagerMostViewed = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        mLayoutManagerMostViewed.setStackFromEnd(true);

        mRecyclerViewMostViewed.setAdapter(adapterReyclerMostViewed);
        mRecyclerViewMostViewed.setLayoutManager(mLayoutManagerMostViewed);

        LinearLayoutManager mLayoutManagerRecentlyAdded = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        mLayoutManagerRecentlyAdded.setStackFromEnd(true);

        mRecyclerViewRecentlyAdded.setAdapter(adapterReyclerRecentlyAdded);
        mRecyclerViewRecentlyAdded.setLayoutManager(mLayoutManagerRecentlyAdded);


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
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterReyclerMostViewed.startListening();
        adapterReyclerRecentlyAdded.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterReyclerMostViewed.stopListening();
        adapterReyclerRecentlyAdded.stopListening();
    }

}
