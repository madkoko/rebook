package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.messaging.MessageManager;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class ShowBook extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private static final String TAG = "ShowBook";
    private ProfileManager profileManager;
    private Book book;
    private Button sendRequest;
    private String receiverToken;
    private JSONArray regArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book);
        profileManager = ProfileManager.getInstance();

        TextView isbn, title, author, publisher, editionYear, conditions;
        ImageView bookImage;

        isbn = findViewById(R.id.show_book_isbn);
        title = findViewById(R.id.show_book_title);
        author = findViewById(R.id.show_book_author);
        publisher = findViewById(R.id.show_book_publisher);
        editionYear = findViewById(R.id.show_book_edition_year);
        conditions = findViewById(R.id.show_book_conditions);
        bookImage = findViewById(R.id.show_book_photo);

        Intent i = getIntent();
        if (i.getExtras().get("book") != null) {
            book = (Book) i.getExtras().get("book");

            if (book.getISBN() != null) isbn.setText(book.getISBN());
            if (book.getTitle() != null) title.setText(book.getTitle());
            if (book.getAuthor() != null) author.setText(book.getAuthor());
            if (book.getPublisher() != null) publisher.setText(book.getPublisher());
            if (book.getEditionYear() != null) editionYear.setText(book.getEditionYear());
            if (book.getBookConditions() != null) conditions.setText(book.getBookConditions());
            Picasso.get().load(book.getImage()).into(bookImage);
            //Picasso.get().load(i.getExtras().get("bookPhoto").toString()).into(bookImage);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (profileManager.getProfile(book.getUid()).getTokenMessage() != null) {
            // Sender
            String senderId = FirebaseAuth.getInstance().getUid();
            Profile senderProfile = profileManager.getProfile(senderId);

            // Receiver
            String receiverId = book.getUid();
            Profile receiverProfile = profileManager.getProfile(receiverId);
            receiverToken = receiverProfile.getTokenMessage();

            // TODO debugging
            Log.d("device_token", "Token is: " + receiverToken);
            String authUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
            sendRequest = findViewById(R.id.send_request);
            if(authUser.compareTo(book.getUid())!=0) {
                sendRequest.setVisibility(View.VISIBLE);
                sendRequest.setOnClickListener(
                        v -> MessageManager.sendRequestNotification(
                                // Sender info
                                senderId,                       // sender ID
                                senderProfile.getName(),        // sender username
                                senderProfile.getImage(),      // sender image
                                senderProfile.getTokenMessage(),// sender token

                                // Receiver info
                                receiverId,                     // receiver ID
                                receiverProfile.getName(),      // receiver username
                                receiverProfile.getImage(),    // receiver image
                                receiverToken,                  // receiver token

                                // Book info
                                book.getTitle()                 // book title
                        )
                );
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        if (profileManager.getProfile(book.getUid()).getPosition() != null) {
            String pos = profileManager.getProfile(book.getUid()).getPosition();
            Log.d(TAG, pos);
            String lat = pos.substring(pos.indexOf("(") + 1, pos.indexOf(","));
            String lng = pos.substring(pos.indexOf(",") + 1, pos.indexOf(")"));
            Log.d(TAG, lat + " " + lng);
            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lng);

            LatLng position = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(position));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }
}
