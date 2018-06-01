package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.ByteArrayOutputStream;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.messaging.MessageManager;
import it.polito.mad.koko.kokolab3.messaging.ShowChat;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.request.Request;
import it.polito.mad.koko.kokolab3.request.RequestManager;

public class ShowBook extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private static final String TAG = "ShowBook";
    private Book book;
    private JSONArray regArray;

    private Intent i;

    private Button sendRequest;
    private Button sendMessage;
    private LinearLayout sendingLayout;

    private String senderId;
    private Profile senderProfile;
    private String senderUsername;
    private String senderImage;
    private String senderToken;

    private String receiverId;
    private Profile receiverProfile;
    private String receiverUsername;
    private String receiverImage;
    private String receiverToken;

    private String bookId;
    private String bookName;

    private String chatID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book);

        TextView isbn, title, author, publisher, editionYear, conditions;
        ImageView bookImage;

        isbn = findViewById(R.id.show_book_isbn);
        title = findViewById(R.id.show_book_title);
        author = findViewById(R.id.show_book_author);
        publisher = findViewById(R.id.show_book_publisher);
        editionYear = findViewById(R.id.show_book_edition_year);
        conditions = findViewById(R.id.show_book_conditions);
        bookImage = findViewById(R.id.show_book_photo);

        i = getIntent();

        if (i.getExtras().get("book") != null) {
            book = (Book) i.getExtras().get("book");
            if (book.getISBN() != null) isbn.setText(book.getISBN());
            if (book.getTitle() != null) {
                bookName = book.getTitle();
                title.setText(bookName);
            }
            if (book.getAuthor() != null) author.setText(book.getAuthor());
            if (book.getPublisher() != null) publisher.setText(book.getPublisher());
            if (book.getEditionYear() != null) editionYear.setText(book.getEditionYear());
            if (book.getBookConditions() != null) conditions.setText(book.getBookConditions());
            bookId = i.getStringExtra("bookId");
            Picasso.get().load(book.getImage()).into(bookImage);
            //Picasso.get().load(i.getExtras().get("bookPhoto").toString()).into(bookImage);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ProfileManager.getProfile(book.getUid()).getTokenMessage() != null) {

            // Sender Info
            senderId = ProfileManager.getCurrentUserID();
            senderProfile = profileManager.getProfile();
            senderUsername = senderProfile.getName();
            senderImage = senderProfile.getImage();
            senderToken = senderProfile.getTokenMessage();

            // Receiver Info
            receiverId = book.getUid();
            receiverProfile = profileManager.getProfile(receiverId);
            receiverUsername = receiverProfile.getName();
            receiverImage = receiverProfile.getImage();
            receiverToken = receiverProfile.getTokenMessage();

            // TODO debugging
            Log.d("device_token", "Token is: " + receiverToken);
            String authUser= ProfileManager.getCurrentUserID();

            sendingLayout = findViewById(R.id.sending_layout);  // >>> Start Chat | Send Message Layout
            sendRequest = findViewById(R.id.send_request);      // >>> Send Request Button
            sendMessage = findViewById(R.id.send_message);      // >>> Send Message Button

            if(authUser.compareTo(book.getUid())!=0) {

                // 1. Set Sending Layout visible
                sendingLayout.setVisibility(View.VISIBLE);

                // 2. Put Sender & Receiver info into Intent
                i.putExtra("senderId", this.senderId);
                i.putExtra("senderUsername", this.senderUsername);
                i.putExtra("senderImage", this.senderImage);
                i.putExtra("senderToken", this.senderToken);
                i.putExtra("receiverId", this.receiverId);
                i.putExtra("receiverUsername", this.receiverUsername);
                i.putExtra("receiverImage", this.receiverImage);
                i.putExtra("receiverToken", this.receiverToken);

                // 3. Click on "Book Request"
                sendRequest.setOnClickListener(
                        v -> {

                            // 3A. Sen a Request Notification
                            MessageManager.sendRequestNotification(    //createMsg

                                    // Sender info
                                    senderId,                               // Sender ID
                                    senderProfile.getName(),                // Sender Username
                                    senderProfile.getImage(),               // sender Image
                                    senderProfile.getTokenMessage(),        // Sender Token

                                    // Receiver info
                                    receiverId,                             // Receiver ID
                                    receiverProfile.getName(),              // Receiver Username
                                    receiverProfile.getImage(),             // Receiver Image
                                    receiverToken,                          // Receiver Token

                                    // Book info
                                    book.getTitle(),                        // Book Title

                                    // Chat info
                                    MessageManager.getChatID()
                            );

                            // 3B. Create a new Request
                            //      >>> Create a ByteArray to save the image of the book on FireBase
                            bookImage.setDrawingCacheEnabled(true);
                            bookImage.buildDrawingCache();
                            Bitmap bitmap = bookImage.getDrawingCache();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] shown_image = baos.toByteArray();
                            //      >>> Create a new Request
                            Request req = new Request(senderId, receiverId, bookId, bookName, book.getImage());
                            String reqId = senderId+""+bookId;
                            RequestManager.Companion.newRequest(req, shown_image, reqId);

                            // 3C. Create or resume Chat
                            boolean chatFlag = true;
                            MessageManager.createChat(i, book.getTitle(), chatFlag);
                        });

                // 4. Click on "Open Chat"
                sendMessage.setOnClickListener(v -> {

                    boolean chatFlag = false;
                    MessageManager.createChat(i, book.getTitle(), chatFlag);

                    Intent showChat = new Intent (getApplicationContext(), ShowChat.class);
                    showChat.putExtra("originClass", "showBook");
                    startActivity(showChat);

                });

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        if (ProfileManager.getProfile(book.getUid()).getPosition() != null) {
            String pos = ProfileManager.getProfile(book.getUid()).getPosition();
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
