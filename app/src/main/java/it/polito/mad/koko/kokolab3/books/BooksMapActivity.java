package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class BooksMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private static final String TAG = "BooksMapActivity";
    private ProfileManager profileManager;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_books_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /** Called when the map is ready. */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // ******************* Google *********************
        mMap = googleMap;

        // ******************* Davide **********************
        profileManager = ProfileManager.getInstance();
        Intent in = getIntent();
        //ArrayList of key
        ArrayList<String> list = (ArrayList<String>) in.getSerializableExtra("key");


        // ********************** Io ************************

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                if (profileManager.getProfile(list.get(i)) != null) {
                    Profile user = profileManager.getProfile(list.get(i));
                    String pos = user.getPosition();
                    String nameUser = user.getName();

                    //Transform String into a LetLng object
                    String lat = pos.substring(pos.indexOf("(") + 1, pos.indexOf(","));
                    String lng = pos.substring(pos.indexOf(",") + 1, pos.indexOf(")"));
                    double latitude = Double.parseDouble(lat);
                    double longitude = Double.parseDouble(lng);
                    LatLng position = new LatLng(latitude, longitude);


                    // Add some markers to the map, and add a data title (name of users) to each marker.
                    //Add only one time for point, if we have two equals point it marks only one time
                    googleMap.addMarker(new MarkerOptions()
                            .title(nameUser)
                            .position(position));

                    // Set a listener for marker click.
                    mMap.setOnMarkerClickListener(this);
                }
            }
        }
    }

        /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(Marker marker) {
        String g=marker.getTitle();
        // Quando avremo più utenti si aprirà un utente
        return false;
    }
}