package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.request.Request;
import it.polito.mad.koko.kokolab3.request.RequestManager;

public class HomeSharingBook extends Fragment {

    private static final String TAG = "HomeSharingBook";
    private FirebaseListAdapter<Request> adapter;
    private RatingBar ratingBar;
    private EditText feedbackEditText;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ListView listView = getActivity().findViewById(R.id.list_home_chats);

        Query query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("requests")
                .orderByChild("senderId")
                .equalTo(currentUid);

        FirebaseListOptions<Request> options = new FirebaseListOptions.Builder<Request>()
                .setLayout(R.layout.sharing_adapter_layout)
                .setQuery(query, Request.class)
                .build();

        adapter = new FirebaseListAdapter<Request>(options) {

            @Override
            protected void populateView(View v, Request model, int position) {
                ImageView imageBook = v.findViewById(R.id.book_sharing_home);
                TextView titleBook = v.findViewById(R.id.req_book_title_sharing_home);
                Button buttonReturn = v.findViewById(R.id.return_button);
                ratingBar = v.findViewById(R.id.rating_bar_sharing_home);
                ratingBar.setVisibility(View.INVISIBLE);

                feedbackEditText= v.findViewById(R.id.feedback_edit_text_sharing_home);
                feedbackEditText.setVisibility(View.INVISIBLE);

                titleBook.setText(model.getBookName());
                Picasso.get().load(model.getBookImage()).into(imageBook);

                if (model.getStatus().equals("onBorrow")) {
                    buttonReturn.setVisibility(View.VISIBLE);
                    buttonReturn.setOnClickListener(v1 -> {
                        RequestManager.Companion.retunRequest(getRef(position).getKey());
                    });
                } else if (model.getStatus().equals("returned")) {
                    buttonReturn.setVisibility(View.VISIBLE);
                    buttonReturn.setText(R.string.currency);
                    ratingBar.setVisibility(View.VISIBLE);
                    feedbackEditText.setVisibility(View.VISIBLE);
                    buttonReturn.setOnClickListener(v2 -> {
                        Log.d(TAG, String.valueOf(ratingBar.getRating()));
                        ProfileManager.addRating(model.getReceiverId(), String.valueOf(ratingBar.getRating()),feedbackEditText.getText().toString());
                        RequestManager.Companion.putReceiverRate(getRef(position).getKey(), String.valueOf((int) ratingBar.getRating()));
                        if (model.getRatingSender() != null && !model.getRatingSender().isEmpty() && model.getRatingSender().compareTo("") != 0) {
                            RequestManager.Companion.ratedTransition(getRef(position).getKey());
                        }
                    });
                } else
                    buttonReturn.setVisibility(View.INVISIBLE);
                if(!model.getRatingReceiver().equals("")) {
                    ratingBar.setVisibility(View.INVISIBLE);
                    feedbackEditText.setVisibility(View.INVISIBLE);
                    buttonReturn.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onDataChanged() {
                if(adapter.getCount()==0) {
                    listView.setEmptyView(getActivity().findViewById(R.id.no_borrowed_found));
                    Toast.makeText(getActivity().getApplicationContext(), "No books borrowed found.", Toast.LENGTH_SHORT).show();
                }
            }

        };

        listView.setAdapter(adapter);

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
