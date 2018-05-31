package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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

    private FirebaseListAdapter<Request> adapter;

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

                titleBook.setText(model.getBookName());
                Picasso.get().load(model.getBookImage()).into(imageBook);

                if (model.getStatus().equals("onBorrow")) {
                    buttonReturn.setVisibility(View.VISIBLE);
                    buttonReturn.setOnClickListener(v1 -> {
                        RequestManager.Companion.retunRequest(getRef(position).getKey());
                    });
                } else if (model.getStatus().equals("return")) {
                    buttonReturn.setVisibility(View.VISIBLE);
                    buttonReturn.setText(R.string.currency);
                    RatingBar ratingBar = v.findViewById(R.id.rating_bar_sharing_home);
                    buttonReturn.setOnClickListener(v2 -> {
                        ProfileManager.getInstance().setRating(model.getReceiverId(), ratingBar.getNumStars());
                        RequestManager.Companion.ratedTransition(getRef(position).getKey());
                    });
                } else
                    buttonReturn.setVisibility(View.INVISIBLE);
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
