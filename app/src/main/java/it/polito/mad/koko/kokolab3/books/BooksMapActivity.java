package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

    /**
     * Whether the map zooming should be animated or not.
     */
    private static final boolean ANIMATION = false;

    /**
     * Map zoom padding.
     */
    private static final int PADDING = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_books_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Called when the map is ready.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map
        mMap = googleMap;

        // Retrieving user IDs
        Intent mapsIntent = getIntent();
        HashMap<String, String> sharingUsersPositions = (HashMap<String, String>) mapsIntent.getSerializableExtra("sharingUsersPositions");

        // Collecting markers in order to zoom automatically
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (sharingUsersPositions != null) {

            for(String userName:sharingUsersPositions.keySet()) {

                String pos = sharingUsersPositions.get(userName);

                //Transform String into a LetLng object
                String lat = pos.substring(pos.indexOf("(") + 1, pos.indexOf(","));
                String lng = pos.substring(pos.indexOf(",") + 1, pos.indexOf(")"));
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lng);
                LatLng position = new LatLng(latitude, longitude);

                // Add some markers to the map, and add a data title (name of users) to each marker.
                //Add only one time for point, if we have two equals point it marks only one time
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .title(userName)
                        .position(position));

                // Adding the marker to the set of markers in order to zoom automatically
                builder.include(marker.getPosition());

                // Set a listener for marker click.
                mMap.setOnMarkerClickListener(this);
            }

        }

        // Building zoom bounds
        LatLngBounds bounds = builder.build();

        // Obtaining a movement description object by using the factory: CameraUpdateFactory
        int padding = PADDING; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        // Map automatic zoom
        if (ANIMATION)
            googleMap.animateCamera(cu);
        else
            googleMap.moveCamera(cu);
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        String g = marker.getTitle();
        // Quando avremo più utenti si aprirà un utente
        return false;
    }
}