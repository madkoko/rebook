package it.polito.mad.koko.kokolab3.profile.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;

@SuppressLint("ValidFragment")
public class TabProfile extends Fragment {


    private static final String TAG = "TabProfileFragment";
    private ProfileTabAdapter profileTabAdapter;
    private Query query;
    private ListView profilesList;
    private String uid;

    public TabProfile(String uid) {
        this.uid=uid;
    }

    /**
     * Profile profile data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.tab_profile, container, false);
        profilesList = view.findViewById(R.id.profiles_listview);

        query = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(uid);
        FirebaseListOptions<Profile> options = new FirebaseListOptions.Builder<Profile>()
                .setLayout(R.layout.adapter_tab_profile)
                .setQuery(query, Profile.class)
                .build();

        profileTabAdapter = new ProfileTabAdapter(getContext(), options);
        profilesList.setAdapter(profileTabAdapter);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();
        profileTabAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        profileTabAdapter.stopListening();
    }

}

/*
float distanceX = event.getX();
                    if (distanceX > 400.00)
                        getActivity().onBackPressed();
 */