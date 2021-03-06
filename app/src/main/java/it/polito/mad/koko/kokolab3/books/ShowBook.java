package it.polito.mad.koko.kokolab3.books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener;
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
    private String bookTitle;

    private boolean isImageFitToScreen;

    private String chatID = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                bookTitle = book.getTitle();
                title.setText(bookTitle);
            }
            if (book.getAuthor() != null) author.setText(book.getAuthor());
            if (book.getPublisher() != null) publisher.setText(book.getPublisher());
            if (book.getEditionYear() != null) editionYear.setText(book.getEditionYear());
            if (book.getBookConditions() != null) conditions.setText(book.getBookConditions());
            bookId = i.getStringExtra("bookId");

            Picasso.get().load(book.getImage()).into(bookImage);
            //bookImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            if (book.getUid().compareToIgnoreCase(ProfileManager.getCurrentUserID()) != 0)
                BookManager.addVisualization(bookId);

        }
        isImageFitToScreen =false;
        //int gravity = bookImage.getForegroundGravity();
        /*
        bookImage.setOnClickListener(v -> {
            if(isImageFitToScreen) {
                isImageFitToScreen=false;
                bookImage.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
                bookImage.setAdjustViewBounds(true);
                bookImage.setScaleType(ImageView.ScaleType.CENTER);
            }else{
                isImageFitToScreen=true;
                bookImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                //bookImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        });*/
        ViewSwitcher viewSwitcher = findViewById(R.id.switcher_for_map);
        LinearLayout bookView = findViewById(R.id.book_view);
        LinearLayout mapView = findViewById(R.id.map_view);
        Button buttonShowMap = findViewById(R.id.show_book_position);
        Button returnToBook = findViewById(R.id.return_to_book);

        //viewSwitcher.getCurrentView();
        buttonShowMap.setOnClickListener(v->{
            if (viewSwitcher.getCurrentView()!= mapView){
                viewSwitcher.showNext();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map_book);
                mapFragment.getMapAsync(this);
            }
        });
        returnToBook.setOnClickListener(vReturn->{
            if (viewSwitcher.getCurrentView()!=bookView)
                viewSwitcher.showPrevious();
        });
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        if (ProfileManager.getProfile(book.getUid()).getTokenMessage() != null) {

            // Sender Info
            senderId = ProfileManager.getCurrentUserID();
            senderProfile = ProfileManager.getProfile();
            senderUsername = senderProfile.getName();
            senderImage = senderProfile.getImage();
            senderToken = senderProfile.getTokenMessage();

            // Receiver Info
            receiverId = book.getUid();
            receiverProfile = ProfileManager.getProfile(receiverId);
            receiverUsername = receiverProfile.getName();
            receiverImage = receiverProfile.getImage();
            receiverToken = receiverProfile.getTokenMessage();

            // TODO debugging
            Log.d("device_token", "Token is: " + receiverToken);
            String authUser = ProfileManager.getCurrentUserID();

            sendingLayout = findViewById(R.id.sending_layout);  // >>> Start Chat | Send Message Layout
            sendRequest = findViewById(R.id.send_request);      // >>> Send Request Button
            sendMessage = findViewById(R.id.open_chat);      // >>> Send Message Button


            //if the book isn't sharable the request button is hidden
            if (i.getStringExtra("sharable") != null)
                if (i.getStringExtra("sharable").compareToIgnoreCase("no") == 0)
                    sendRequest.setVisibility(View.INVISIBLE);


            if (authUser.compareTo(book.getUid()) != 0) {

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

                            BookManager.isSharable(bookId, new OnGetDataListener() {
                                @Override
                                public void onSuccess(DataSnapshot data) {
                                    if (data.exists()) {
                                        if (data.getValue().toString().compareToIgnoreCase("no") == 0) {
                                            Toast.makeText(ShowBook.this, "Sorry, this book has already being shared!", Toast.LENGTH_LONG).show();
                                        } else {
                                            // 3C. Create or resume Chat
                                            boolean chatFlag = true;
                                            MessageManager.createChat(i, book.getTitle(), chatFlag);

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
                                                    bookId,                                 // Book ID
                                                    bookTitle,                               // Book Title

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
                                            Request request = new Request(senderId, receiverId, bookId, bookTitle, book.getImage(), "pending", "", "", senderUsername);
                                            String requestId = senderId + "" + bookId;
                                            RequestManager.Companion.createRequest(request, shown_image, requestId);
                                        }
                                    }
                                }

                                @Override
                                public void onFailed(DatabaseError databaseError) {

                                }
                            });
                        });

                // 4. Click on "Open Chat"
                sendMessage.setOnClickListener(v -> {

                    boolean chatFlag = false;
                    MessageManager.createChat(i, book.getTitle(), chatFlag);

                    Intent showChat = new Intent(getApplicationContext(), ShowChat.class);
                    showChat.putExtra("originClass", "showBook");
                    startActivity(showChat);

                });

            }
            else
                sendingLayout.setVisibility(View.INVISIBLE);

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
