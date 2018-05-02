package it.polito.mad.koko.kokolab3.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.auth.Authenticator;
import it.polito.mad.koko.kokolab3.ui.*;

public class ShowProfile extends AppCompatActivity {

    private static final String TAG = "ShowProfile";

    /**
     * Profile profile data.
     */
    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_location;
    private TextView tv_bio;
    private ImageView user_photo;
    private Intent i;

    /**
     * Profile profile data is stored in a firebase database.
     */
    private String mFirebaseUser;

    /**
     * User profile information
     */
    private ProfileManager profileManager;
    private Authenticator authenticator;
    private CardView card_view;
    private Target loadtarget;
    private Bitmap bmp;

    /**
     * Instantiating the activity for the first time.
     * @param savedInstanceState    activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieving the ProfileManager singleton
        profileManager = ProfileManager.getInstance();

        authenticator = new Authenticator(this);


        // Loading the XML layout file
        setContentView(R.layout.activity_show_profile);

        //Loading UserID from intent
        i = getIntent();
        mFirebaseUser= i.getExtras().getString("UserID");


        // Restoring UI fields containing user info
        tv_name=findViewById(R.id.user_name);
        tv_email=findViewById(R.id.user_email);
        tv_location=findViewById(R.id.user_location);
        tv_bio=findViewById(R.id.user_bio);
        user_photo=findViewById(R.id.user_photo);
        card_view=findViewById(R.id.cv);
    }

    /**
     *
     * Setting the edit profile button in the title bar.
     * @param menu      menu object to be instantiated.
     * @return true, whether the menu object has been inserted or not.
     * @return false, Auth user != user from intent
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mFirebaseUser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
            return true;
        }
        return false;

    }

    /**
     * Handling the menu item selection.
     * @param item      item that has been selected.
     * @return          whether the menu item has been handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Profile editing
            case R.id.edit:
                //Only if Auth user is equal to user from intent, we can use this menu
                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(i);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Filling all the UI fields retrieving all the needed information from
     * firebase.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onResume() {
        super.onResume();

        Profile profile = profileManager.getProfile(authenticator.getUser().getUid());
        tv_name.setText(profile.getName());
        tv_email.setText(profile.getEmail());
        tv_location.setText(profile.getLocation());
        tv_bio.setText(profile.getBio());


        try {
            FileInputStream in = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "temp_profile");
            bmp = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BlurBuilder.blur(this, bmp);
        Drawable drawable = new BitmapDrawable(getResources(),bitmap);
        card_view.setBackground(drawable);
        //



        if(profile.getImgUrl()!=null) {
            //Picasso.get().load(profile.getImgUrl()).into(user_photo);
            // Get the data from an ImageView and apply blur effect
            Picasso.get().load(profile.getImgUrl()).transform(new CircleTransform()).into(user_photo);




        }
    }
}

