package it.polito.mad.koko.kokolab3.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.tabs.PagerAdapter;
import it.polito.mad.koko.kokolab3.ui.*;

public class ShowProfile extends AppCompatActivity {

    private static final String TAG = "ShowProfile";

    private Intent i;

    /**
     * Profile profile data is stored in a firebase database.
     */
    private String mFirebaseUser;

    /**
     * User profile information
     */
    private Bitmap bmp;
    private Profile profile;
    private FloatingActionButton edit;
    private Toolbar toolbar;
    private TabLayout tabs;
    private ImageView user_photo;
    private ViewPager viewPager;
    private int position = 0;
    private LinearLayout ln;

    /**
     * Instantiating the activity for the first time.
     *
     * @param savedInstanceState activity's previously saved state.
     */
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        // Loading the XML layout file
        setContentView(R.layout.activity_show_profile);
        ln = findViewById(R.id.Linear);
        user_photo = findViewById(R.id.user_photo);

        //Loading UserID from intent
        i = getIntent();
        mFirebaseUser = i.getExtras().getString("UserID");
        edit = findViewById(R.id.fab);

        if (mFirebaseUser.equals(ProfileManager.getCurrentUserID())) {
            profile = ProfileManager.getProfile();
            Log.d(TAG,profile.toString());
            edit.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.INVISIBLE);
            profile = ProfileManager.getOtherUser();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbar = findViewById(R.id.technique_three_toolbar);
            toolbar.setTitle(profile.getName());
            edit.setOnClickListener(v -> {
                //Only if Auth user is equal to user from intent, we can use this menu
                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(i);
                finish();
            });

        }

        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText(R.string.show_profile));
        tabs.addTab(tabs.newTab().setText(R.string.books));
        tabs.addTab(tabs.newTab().setText(R.string.comments));

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabs.getTabCount(), mFirebaseUser);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        setPosition(getPosition());
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setOnTouchListener((v, event) -> setScroll(event));
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onResume() {
        super.onResume();

        /*try {
            ImageManager.loadBitmap(profile.getImage());
            FileInputStream in = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "temp_profile");
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d(TAG, "profile.getImage(): " + profile.getImage());
        bmp = ImageManager.getBitmapFromURL(profile.getImage());

        if (bmp != null) {
            Bitmap bitmap = BlurBuilder.blur(this, bmp);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            user_photo.setBackground(drawable);
        }

        //

        if (profile.getImage() != null) {
            //Picasso.get().load(profile.getImage()).into(user_photo);
            // Get the data from an ImageView and apply blur effect
            Picasso.get().load(profile.getImage()).transform(new CircleTransform()).into(user_photo);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setPosition(int position) {
        this.position = position;
    }

    private int getPosition() {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    private boolean setScroll(MotionEvent event) {
        float distanceX = event.getX();
        if (getPosition() == 0 && distanceX > 350.00)
            onBackPressed();
        return false;
    }
}

