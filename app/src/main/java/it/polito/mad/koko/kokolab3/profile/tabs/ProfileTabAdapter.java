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


    public ProfileTabAdapter(Context context, FirebaseListOptions<Profile> options){
        super(options);
        this.context=context;

    }

    @Override
    protected void populateView(View v, Profile model, int position) {


        ratingView = v.findViewById(R.id.ratingView);
        ratingText = v.findViewById(R.id.ratingText);

        valueEmail = v.findViewById(R.id.value_email);
        valueEmail.setText(model.getEmail());

        valueLocation = v.findViewById(R.id.value_location);
        valueLocation.setText(model.getLocation());
        //valueLocation.setOnClickListener(vPlace -> PlaceApi());

        valueBio = v.findViewById(R.id.value_bio);
        valueBio.setText(model.getBio());

        //WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //Display display=  windowManager.getDefaultDisplay();

        //guideLine = v.findViewById(R.id.guideline);
        //guideLine.setGuidelineBegin(display.getWidth());


        /*
        switcherBio.setOnKeyListener((View v1, int keyCode, KeyEvent event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if (valueBio.getText() != null && valueBio.getText().toString() != "") {
                            valueBio.setText(valueBio.getText().toString());
                            switcherBio.showPrevious();
                        }
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
        */

        //valueBio.setOnClickListener(vBio -> switcherBio.showNext());

    }
}
