package it.polito.mad.koko.kokolab3.profile.Tab_Profile_Show;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.polito.mad.koko.kokolab3.R;

public class TabProfile extends Fragment {


    /**
     * Profile profile data.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.tab_profile, container, false);
        ListView profilesList = view.findViewById(R.id.profiles_listview);
        ProfileTabAdapter profileTabAdapter = new ProfileTabAdapter(getContext());
        profilesList.setAdapter(profileTabAdapter);
        return view;
    }
}