package it.polito.mad.koko.kokolab3.profile;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.auth.Authenticator;
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
    private ProfileManager profileManager;
    private Authenticator authenticator;
    private Bitmap bmp;
    private Profile profile;
    private FloatingActionButton edit;
    private Toolbar toolbar;
    private TabLayout tabs;
    private ImageView user_photo;

    /**
     * Instantiating the activity for the first time.
     * @param savedInstanceState    activity's previously saved state.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Retrieving the ProfileManager singleton
        profileManager = ProfileManager.getInstance();

        authenticator = new Authenticator(this);


        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        // Loading the XML layout file
        setContentView(R.layout.activity_show_profile);
        user_photo=findViewById(R.id.user_photo);



        //Loading UserID from intent
        i = getIntent();
        mFirebaseUser= i.getExtras().getString("UserID");

        profile = profileManager.getProfile(authenticator.getUser().getUid());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbar = findViewById(R.id.technique_three_toolbar);
            toolbar.setTitle(profile.getName());

            if (mFirebaseUser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                edit = findViewById(R.id.fab);
                edit.setOnClickListener(v -> {
                    //Only if Auth user is equal to user from intent, we can use this menu
                    Intent i = new Intent(getApplicationContext(), EditProfile.class);
                    startActivity(i);
                    finish();
                });
            }
        }




        tabs = (TabLayout) findViewById(R.id.tabs);


        tabs.addTab(tabs.newTab().setText(R.string.show_profile));
        tabs.addTab(tabs.newTab().setText(R.string.books));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





    }

    /**
     *
     * Setting the edit profile button in the title bar.
     * @param menu menu object to be instantiated.
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

        try {
            FileInputStream in = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "temp_profile");
            bmp = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(bmp != null) {
            Bitmap bitmap = BlurBuilder.blur(this, bmp);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            user_photo.setBackground(drawable);
        }

        //

        if(profile.getImage()!=null) {
            //Picasso.get().load(profile.getImage()).into(user_photo);
            // Get the data from an ImageView and apply blur effect
            Picasso.get().load(profile.getImage()).transform(new CircleTransform()).into(user_photo);
        }
    }
}

