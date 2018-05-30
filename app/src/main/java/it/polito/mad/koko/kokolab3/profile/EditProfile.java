package it.polito.mad.koko.kokolab3.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.auth.Authenticator;
import it.polito.mad.koko.kokolab3.util.AlertManager;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfile";

    /**
     * Profile pic source
     */
    private static final int
            CAMERA_REQUEST = 0,
            GALLERY = 1,
            PLACE_PICKER_REQUEST = 2;

    /**
     * Profile profile data.
     */
    private EditText et_name;
    private TextView et_email;
    private EditText et_phone;
    private EditText et_location;
    private EditText et_bio;
    private ImageView user_photo;
    //private android.support.design.widget.TextInputLayout text_user_location;

    /**
     * User profile information
     */
    private ProfileManager profileManager;

    /**
     * Firebase login profile, firebase database
     */
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private DatabaseReference nameReference;

    /**
     *
     */
    private Uri imageRef;
    private Bitmap imageBitmap;
    private boolean flagCamera;
    private boolean flagGallery;
    private Authenticator authenticator;
    private EditText map_button;
    private Profile p;
    private String latLng;

    /**
     * Filling all the UI text fields and the profile profile pic with all the
     * previous values shown in the ShowProfile activity.
     * It also adds an edit profile pic button and the save button to save
     * the current modifications in the sharedPreferences XML file.
     *
     * @param savedInstanceState activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticator = new Authenticator(this);
        profileManager= ProfileManager.getInstance();

        // TODO Debugging
        Log.d(TAG, "onCreate");

        // Retrieving the ProfileManager singleton
        p = profileManager.getCurrentUser();
        // Loading the XML layout file
        setContentView(R.layout.activity_edit_profile);

        // Restoring UI fields containing profile info
        et_name = findViewById(R.id.edit_user_name);
        //et_password=findViewById(R.id.edit_user_password);
        et_email = findViewById(R.id.edit_user_email);
        //
        et_email.setPaintFlags(et_email.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //
        et_phone = findViewById(R.id.edit_user_phone);
        et_location = findViewById(R.id.button_map);
        et_bio = findViewById(R.id.edit_user_bio);
        user_photo = findViewById(R.id.user_photo_edit);




        // Restoring from past instanceState
        if (savedInstanceState != null) {
            flagCamera = savedInstanceState.getBoolean("flagCamera");
            flagGallery = savedInstanceState.getBoolean("flagGallery");
            if (flagGallery)
                imageRef = Uri.parse(savedInstanceState.getString("imageRef"));
        }

        //map_button = findViewById(R.id.button_map);
        et_location.setOnClickListener(v -> PlaceApi());

        // Edit profile pic button
        ImageButton user_photo_button = findViewById(R.id.user_photo_button);
        user_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });

        // Save button
        Button save_button = findViewById(R.id.save_button);

        save_button.setOnClickListener(v -> {
            // If the user has selected a location


            if (!infoIsMissingFromUI()) {
                // Get the data from an ImageView as bytes
                user_photo.setDrawingCacheEnabled(true);
                user_photo.buildDrawingCache();
                Bitmap bitmap = user_photo.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] shown_image = baos.toByteArray();

                //Create a new profileManager
                profileManager.editProfile(
                        authenticator.getAuth().getUid(),
                        et_name.getText().toString(),
                        et_email.getText().toString(),
                        et_phone.getText().toString(),
                        et_location.getText().toString(),
                        et_bio.getText().toString(),
                        shown_image,
                        latLng,
                        authenticator.getStorage().getReference().child("users").child(authenticator.getAuth().getUid())
                );

                // Terminating the activity
                finish();
            }
        });
    }

    /**
     * Tests whether the location info and username have been specified or not via the
     * corresponding UI item.
     *
     * @return true if the user has not specified the location or username yet.
     * false otherwise.
     */

    private boolean infoIsMissingFromUI() {

        boolean checkUIMandatoryInfo = false;

        if (et_name != null && et_name.getText().toString().equals("")) {
            checkUIMandatoryInfo = true;
            et_name.setError("Please insert a valid username");
        }
        if (et_location != null && et_location.getText().equals("")) {
            checkUIMandatoryInfo = true;
            et_location.setError("Please insert a valid location");
        }

        return checkUIMandatoryInfo;
    }


    /**
     * Tests whether the location and username info is in the user profile (in Firebase) or not.
     *
     * @return true if the user hasn't a location and username associated with him/her yet.
     * false otherwise.
     */

    private boolean infoIsMissingFromUser() {
        String userLocation = profileManager.getProfile(authenticator.getUser().getUid()).getLocation();
        String username = profileManager.getProfile(authenticator.getUser().getUid()).getName();

        boolean infoIsMissing = false;

        if(
            (userLocation == null || (userLocation.isEmpty() || userLocation.equals("")))
                ||
            (username == null || (username.isEmpty() || username.equals("")))
        )
            infoIsMissing = true;

        return infoIsMissing;
    }


    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        //Log.d("location", "profileManager.getProfile(authenticator.getUser().getUid()).getLocation(): " + profileManager.getProfile(authenticator.getUser().getUid()).getLocation());
        //Log.d("location", "profileManager.getProfile(authenticator.getUser().getUid()).getLocation().equals(\"\"): " + profileManager.getProfile(authenticator.getUser().getUid()).getLocation().equals(""));

        if (!infoIsMissingFromUser() && !infoIsMissingFromUI())
            finish();
    }

    private void PlaceApi() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param savedInstanceState flag if we have selected camera or gallery
     *                           imageRef is uri of image gallery
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("flagCamera", flagCamera);
        savedInstanceState.putBoolean("flagGallery", flagGallery);
        if (flagGallery) {
            savedInstanceState.putString("imageRef", imageRef.toString());
        }
    }

    /**
     * It displays an alert dialog by which the profile can choose the camera or the gallery
     * to take his/her new profile pic.
     */
    private void startDialog() {
        // TODO debugging
        Log.d(TAG, "startDialog");

        // Alert dialog showing the two possibilities: camera or gallery
        // TODO Implement a context-menu instead, with custom style
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Select Profile Picture");

        // 'From gallery' option
        myAlertDialog.setPositiveButton("Gallery",
                (arg0, arg1) -> {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    // Launching the gallery app
                    startActivityForResult(pictureActionIntent, GALLERY);
                });

        // 'From camera' option
        myAlertDialog.setNegativeButton("Camera",
                (arg0, arg1) -> {
                    // Requested from Android 7.0 Nougat
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                        try {
                            // Launching the camera app
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        } catch(Exception e) {
                            // Creating an alert dialog indicating all possible causes
                            AlertManager.permissionDialog(EditProfile.this);
                        }
                    }
                });

        // Showing the alert dialog
        myAlertDialog.show();
    }

    /**
     * Upon taking a picture with the camera, it saves the Bitmap image.
     *
     * @param extras bundle with data from startDialog intent
     */
    private void createImageFile(Bundle extras) {
        imageBitmap = (Bitmap) extras.get("data");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "temp");
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        }// The image cannot be created
        catch (IOException e) {
            // Creating an alert dialog indicating all possible causes
            AlertManager.permissionDialog(EditProfile.this);
        }
    }

    /**
     * Called as a result of the new profile picture activity, whether it is taken from
     * the camera or from the gallery.
     *
     * @param requestCode where the profile pic has been acquired.
     * @param resultCode  whether the operation has been performed successfully or not.
     * @param data        an Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO debugging
        Log.d(TAG, "onActivityResult");

        // If the photo has been picked from the gallery
        if (requestCode == GALLERY && resultCode != RESULT_CANCELED) {
            // TODO debugging
            Log.d(TAG, "gallery");

            // crete a reference from uri gallery
            imageRef = data.getData();
            Log.d(TAG, "onActivityResult imageRef: " + imageRef.toString());


            // set flags for future state
            flagGallery = true;
            flagCamera = false;
        }

        // If the photo has been taken with the camera
        if (requestCode == CAMERA_REQUEST && resultCode != RESULT_CANCELED) {
            //Return uri from intent
            Bundle extras = data.getExtras();
            // TODO debugging
            Log.d(TAG, "camera");
            //create a new BitMap
            createImageFile(extras);
            // set flags for future state
            flagCamera = true;
            flagGallery = false;

        }

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            latLng = place.getLatLng().toString();
            final CharSequence address = place.getAddress();
            et_location.setText(address);

            String toastMsg = String.format("Place: %s", place.getName());
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        }


    }

    /**
     * Filling all the UI fields retrieving all the needed information from the
     * sharedPreferences XML file.
     */
    @Override
    protected void onResume() {
        super.onResume();


        et_name.setText(p.getName());
        et_email.setText(p.getEmail());
        et_bio.setText(p.getBio());
        et_location.setText(p.getLocation());
        et_phone.setText(p.getPhone());
        latLng = p.getPosition();


        if (flagCamera) {
            Bitmap tmp = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "temp");
            user_photo.setImageBitmap(tmp);
        } else if (flagGallery) {
            Picasso.get().load(imageRef).fit().centerCrop().into(user_photo);
        } else if (p.getImage() != null) {
            Picasso.get().load(p.getImage()).fit().centerCrop().into(user_photo);
        }
    }
}
