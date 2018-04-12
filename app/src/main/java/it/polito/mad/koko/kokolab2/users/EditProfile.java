package it.polito.mad.koko.kokolab2.users;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.mad.koko.kokolab2.R;

public class EditProfile extends AppCompatActivity{

    /**
     * Profile pic source
     */
    private static final int    CAMERA_REQUEST = 0,
                                GALLERY = 1;
    /**
     * Profile pic URI
     */
    private String user_photo_profile;

    /**
     * User profile data is stored in a shared XML file.
     */
    private String MY_PREFS_NAME = "MySharedPreferences";
    private SharedPreferences sharedPreferences;

    /**
     * User profile data.
     */
    private EditText et_name;
    private EditText et_password;
    private EditText et_email;
    private EditText et_phone;
    private EditText et_location;
    private EditText et_bio;
    private ImageView user_photo;

    /**
     *  Firebase login user, firebase database and user class
     */
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private User user;
    /**
     * Filling all the UI text fields and the user profile pic with all the
     * previous values shown in the ShowProfile activity.
     * It also adds an edit profile pic button and the save button to save
     * the current modifications in the sharedPreferences XML file.
     * @param savedInstanceState    activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Debugging
        Log.d("debug","onCreate");

        // Loading the XML layout file
        setContentView(R.layout.activity_edit_profile);

        // Restoring UI fields containing user info
        et_name=findViewById(R.id.edit_user_name);
        et_password=findViewById(R.id.edit_user_password);
        et_email=findViewById(R.id.edit_user_email);
        et_phone=findViewById(R.id.edit_user_phone);
        et_location=findViewById(R.id.edit_user_location);
        et_bio=findViewById(R.id.edit_user_bio);

        // Restore the profile pic from the sharedPreferences data structure
        sharedPreferences=getApplicationContext().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        user_photo= findViewById(R.id.user_photo);

        // Edit profile pic button
        ImageButton user_photo_button = findViewById(R.id.user_photo_button);
        user_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });


        // inizialiate firebase user and database
        mFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        user = new User(mDatabase,mFirebaseUser);

        // Save button
        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Saving all UI fields values in the sharedPreferences Firebase database.
                user.setName(et_name.getText().toString());
                user.setEmail(et_email.getText().toString());
                user.setPhone(et_phone.getText().toString());
                user.setLocation(et_location.getText().toString());
                user.setBio(et_bio.getText().toString());
                // Saving all UI fields values in the sharedPreferences XML Preferences.
                editor.putString("user_photo",sharedPreferences.getString("user_photo_temp",null));
                editor.apply();

                // Terminating the activity
                finish();
            }
        });
    }

    /**
     * It displays an alert dialog by which the user can choose the camera or the gallery
     * to take his/her new profile pic.
     */
    private void startDialog() {
        // TODO debugging
        Log.d("debug","startDialog");

        // Alert dialog showing the two possibilities: camera or gallery
        // TODO Implement a context-menu instead, with custom style
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Select Profile Picture");

        // 'From gallery' option
        myAlertDialog.setPositiveButton("Gallery",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                // Launching the camera app
                startActivityForResult(pictureActionIntent, GALLERY);
            }
                });

        // 'From camera' option
        myAlertDialog.setNegativeButton("Camera",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // Requested from Android 7.0 Nougat
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // Saving the image file into the file system
                    File f = createImageFile();

                    // The image file couldn't be created
                    if(f == null)
                        // The camera app won't be launched
                        return;

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                    // Launching the camera app
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            });

        // Showing the alert dialog
        myAlertDialog.show();
    }

    /**
     * Upon taking a picture with the camera, it saves the image file. following
     * @return  the image file.
     */
    private File createImageFile() {
        // Timestamp format
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Filename format: 'JPEG' + timestamp + '_'
        String imageFileName="JPEG"+timeStamp+"_";

        // Pictures directory
        File storgeDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Trying to create the image file
        File imageFile = null;
        try {
            // Image file creation
            imageFile = File.createTempFile(imageFileName,".jpg",storgeDir);
        }
        // The image cannot be created
        catch(IOException e) {
            // Creating an alert dialog indicating all possible causes
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
            builder .setMessage(R.string.image_file_creation_error_message)
                    .setTitle(R.string.image_file_creation_error_title)
                    .setIcon(android.R.drawable.ic_dialog_alert);

            // Showing the dialog to the screen
            AlertDialog dialog = builder.create();
            dialog.show();

            return null;
        }

        // Saving the new profile pic
        user_photo_profile = "file:"+imageFile.getAbsolutePath();

        // TODO debugging
        Log.d("debug", user_photo_profile);

        // Returning the image file created
        return imageFile;
    }

    /**
     * Called as a result of the new profile picture activity, whether it is taken from
     * the camera or from the gallery.
     * @param requestCode   where the profile pic has been acquired.
     * @param resultCode    whether the operation has been performed successfully or not.
     * @param data          an Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO debugging
        Log.d("debug","onActivityResult");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // If the photo has been picked from the gallery
        if(requestCode == GALLERY && resultCode != RESULT_CANCELED) {
            editor.putString("user_photo_temp", data.getData().toString());
            editor.apply();
        }

        // If the photo has been taken with the camera
        if(requestCode == CAMERA_REQUEST && resultCode != RESULT_CANCELED) {
            editor.putString("user_photo_temp", user_photo_profile);

            // TODO debugging
            Log.d("debug",user_photo_profile);

            editor.apply();
        }
    }

    /**
     * Filling all the UI fields retrieving all the needed information from the
     * sharedPreferences XML file.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // TODO debugging
        Log.d("debug", "onResume");

        // Updating the sharedPreferences data structure containing user info
        sharedPreferences=getApplicationContext().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        // Restoring all UI values
        DatabaseReference nameReference = mDatabase.child("users").child(String.valueOf(mFirebaseUser.getUid()));
        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                et_name.setText(dataSnapshot.child("name").getValue(String.class));
                et_email.setText(dataSnapshot.child("email").getValue(String.class));
                et_phone.setText(dataSnapshot.child("phone").getValue(String.class));
                et_location.setText(dataSnapshot.child("location").getValue(String.class));
                et_bio.setText(dataSnapshot.child("bio").getValue(String.class));
                //Log.d("TAG",dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            };

        });
        et_password.setText(sharedPreferences.getString("user_password", null));

        // Restoring the profile picture
        Picasso.get().load(sharedPreferences.getString("user_photo_temp", null)).fit().centerCrop().into(user_photo);
    }

}
