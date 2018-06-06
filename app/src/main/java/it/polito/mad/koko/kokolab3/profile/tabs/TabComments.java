package it.polito.mad.koko.kokolab3.profile.tabs;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.profile.Profile;

@SuppressLint("ValidFragment")
public class TabComments extends Fragment {
    private final String TAG = "TabComments";
    private final String uid;
    private Query query;
    private FirebaseListAdapter adapter;

    public TabComments(String uid) {
        this.uid = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_books, container, false);

        ListView comentsListView = view.findViewById(R.id.books_listview);

        query = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("feedback");

        FirebaseListOptions<Object> options = new FirebaseListOptions.Builder<>()
                .setLayout(R.layout.comment_adapter)
                .setQuery(query, Object.class)
                .build();
        adapter = new FirebaseListAdapter<Object>(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

                Log.d(TAG, model.toString());
                TextView feedback = (TextView) v.findViewById(R.id.comment_view);
                String feedbackRating = model.toString();
                String feedbackText = "'" + model.toString() + "'";
                feedback.setText(feedbackText);
                feedback.setTypeface(feedback.getTypeface(), Typeface.BOLD_ITALIC);
            }
        };
        comentsListView.setAdapter(adapter);

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
