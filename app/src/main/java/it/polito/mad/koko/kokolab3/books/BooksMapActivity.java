package it.polito.mad.koko.kokolab3.books;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class BooksMapActivity extends FragmentActivity implements OnMapReadyCallback {

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // ******************* Google *********************
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // ******************* Davide **********************
        profileManager = ProfileManager.getInstance();
        if(profileManager.getProfile(book.getUid()).getPosition() != null) {
            String pos = profileManager.getProfile(book.getUid()).getPosition();
            Log.d(TAG, pos);
            String lat= pos.substring(pos.indexOf("(")+1,pos.indexOf(","));
            String lng= pos.substring(pos.indexOf(",")+1, pos.indexOf(")"));
            Log.d(TAG, lat+" "+lng);
            double latitude = Double.parseDouble(lat);
            double longitude= Double.parseDouble(lng);

            LatLng position = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(position));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        }

        // ********************** Io ************************
        ConcurrentMap<String, Profile> users = profileManager.getAllUsers();
        for(Map.Entry<String, Profile> user: users.entrySet()) {
            if(getIntent().getStringExtra(user.getKey()) != null);
                // TODO continue here
        }
    }
}