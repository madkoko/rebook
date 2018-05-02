package it.polito.mad.koko.kokolab3.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.auth.Authenticator;

public class TabProfile extends Fragment {


    /**
     * Profile profile data.
     */
    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_location;
    private TextView tv_bio;
    private Profile profile;
    private ProfileManager profileManager;
    private Authenticator authenticator;
    private boolean menuVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.tab_profile, container, false);

        profileManager=ProfileManager.getInstance();

        // Restoring UI fields containing user info
        tv_email=view.findViewById(R.id.user_email);
        tv_location=view.findViewById(R.id.user_location);
        tv_bio=view.findViewById(R.id.user_bio);


        Log.d("ui_",FirebaseAuth.getInstance().getUid());
        profile = profileManager.getProfile(FirebaseAuth.getInstance().getUid());
        tv_email.setText(profile.getEmail());
        tv_location.setText(profile.getLocation());
        tv_bio.setText(profile.getBio());

        return view;
    }
}