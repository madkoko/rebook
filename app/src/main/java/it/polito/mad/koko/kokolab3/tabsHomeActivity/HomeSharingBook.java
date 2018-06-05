package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;
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

                LinearLayout linearLayoutRated = v.findViewById(R.id.linear_layout_rated_sharing);
                ViewSwitcher viewSwitcherRated =  v.findViewById(R.id.rated_switch_sharing);
                TextView senderName = v.findViewById(R.id.req_requester_sharing);
                if (model.getSenderId().equals(ProfileManager.getCurrentUserID()))
                    senderName.setText("Request by me");
                else {
                    senderName.setText("Request by " + model.getSenderId());
                    senderName.setOnClickListener(vprofile-> {
                        Intent showProfile = new Intent(getActivity(), ShowProfile.class);
                        showProfile.putExtra("UserID", model.getSenderId());
                        ProfileManager.retrieveInformationUser(model.getSenderId());
                        startActivity(showProfile);
                    });
                }

                TextView textViewCompletSession = v.findViewById(R.id.rated_text_sharing);

                titleBook.setText(model.getBookName());
                Picasso.get().load(model.getBookImage()).into(imageBook);

                if (model.getStatus().equals("onBorrow")) {
                    buttonReturn.setVisibility(View.VISIBLE);
                    buttonReturn.setOnClickListener(v1 -> RequestManager.Companion.returnRequest(getRef(position).getKey()));
                } else if (model.getStatus().equals("returned")) {
                    linearLayoutRated.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    viewSwitcherRated.showNext();
                    buttonReturn.setVisibility(View.VISIBLE);
                    buttonReturn.setText(R.string.currency);
                    ratingBar.setVisibility(View.VISIBLE);
                    feedbackEditText.setVisibility(View.VISIBLE);
                    buttonReturn.setOnClickListener(v2 -> {
                        Log.d(TAG, String.valueOf(ratingBar.getRating()));
                        ProfileManager.addRating(model.getReceiverId(), String.valueOf(ratingBar.getRating()),feedbackEditText.getText().toString());
                        RequestManager.Companion.putReceiverRate(getRef(position).getKey(), String.valueOf((int) ratingBar.getRating()));
                        textViewCompletSession.setText(R.string.rated_added);
                        linearLayoutRated.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
                        viewSwitcherRated.showPrevious();
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
