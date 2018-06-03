package it.polito.mad.koko.kokolab3.profile.tabs;

import android.content.Context;
import android.support.constraint.Guideline;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;

public class ProfileTabAdapter extends FirebaseListAdapter<Profile> {

    private final Context context;
    private TextView valueEmail;
    private TextView valueLocation;
    private TextView valueBio;
    private EditText editBio;
    private ViewSwitcher switcherBio;
    private ImageView ratingView;
    private TextView ratingText;
    private Guideline guideLine;


    public ProfileTabAdapter(Context context, FirebaseListOptions<Profile> options) {
        super(options);
        this.context = context;

    }

    @Override
    protected void populateView(View v, Profile model, int position) {


        ratingView = v.findViewById(R.id.ratingView);
        ratingText = v.findViewById(R.id.ratingText);

        valueEmail = v.findViewById(R.id.value_email);
        valueEmail.setText(model.getEmail());

        //valueLocation = v.findViewById(R.id.value_location);
        //valueLocation.setText(model.getLocation());
        //valueLocation.setOnClickListener(vPlace -> PlaceApi());

        valueBio = v.findViewById(R.id.value_bio);
        valueBio.setText(model.getBio());

        if (model.getCompletedExchanges() != null && !model.getCompletedExchanges().equals("") && model.getTotalStars() != null && !model.getTotalStars().equals("")) {
            ratingText.setText(String.valueOf(Double.valueOf(Integer.valueOf(model.getTotalStars()) / Integer.valueOf(model.getCompletedExchanges())).intValue()));
        }

    }
}
