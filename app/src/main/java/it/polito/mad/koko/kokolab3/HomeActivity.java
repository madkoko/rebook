package it.polito.mad.koko.kokolab3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import it.polito.mad.koko.kokolab3.auth.Authenticator;
import it.polito.mad.koko.kokolab3.books.BookManager;
import it.polito.mad.koko.kokolab3.books.InsertBook;
import it.polito.mad.koko.kokolab3.books.ShowBooks;
import it.polito.mad.koko.kokolab3.profile.EditProfile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Custom class managing the user authentication
     */
    private Authenticator authenticator;

    /**
     * User profile information
     */
    private ProfileManager profileManager;


    private int INSERT_BOOK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticator = new Authenticator(this);

        // UI
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent insertBook = new Intent(getApplicationContext(), InsertBook.class);
                insertBook.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivityForResult(insertBook,INSERT_BOOK);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        authenticator.authUI();

        // creation of the BookManager if the user is authenticated
        if(authenticator.hasLoggedIn()) {
            new BookManager();
        }
    }

    /**
     * When the sign-in flow is complete
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode == authenticator.getRcSignIn()) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in", Toast.LENGTH_LONG).show();
                authenticator.instantiateUser();

                // Creating the Firebase user entry in the database
                ProfileManager profileManager = ProfileManager.getInstance();
                profileManager.addProfile(
                    authenticator.getUser().getDisplayName(),
                    authenticator.getUser().getEmail(),
                    null,
                    null,
                    null,
                    authenticator.getDatabase(),
                    authenticator.getAuth().getUid()
                );

                return;
            } else {
                //Profile pressed back button
                if(response == null) {
                    Toast.makeText(this, "Profile pressed back button", Toast.LENGTH_LONG).show();
                    return;
                }
                //No internet connection.
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show();
                    return;
                }
                //Unknown error
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }*/

        Toast.makeText(this, "Successfully signed in", Toast.LENGTH_LONG).show();
        authenticator.instantiateUser();

        profileManager=null;
        Log.d("debug_profileId_1", String.valueOf(profileManager));
        profileManager=new ProfileManager();
        // Retrieving the ProfileManager singleton
        profileManager = ProfileManager.getInstance();
        Log.d("debug_profileId_2", String.valueOf(profileManager));


        // Creating the Firebase user entry in the database
        profileManager.addProfile(
            authenticator.getUser().getDisplayName(),
            authenticator.getUser().getEmail(),
            null,
            null,
            null,
            null
        );

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.view_profile) {
            Intent i = new Intent(getApplicationContext(), ShowProfile.class);
            i.putExtra("UserID", authenticator.getUser().getUid());
            startActivity(i);

        } else if (id == R.id.edit_profile) {
            Intent intent = new Intent(getApplicationContext(),EditProfile.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

            Intent showBooks= new Intent(getApplicationContext(),ShowBooks.class);
            startActivity(showBooks);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sign_out){
            authenticator.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String profileId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("debug_profileId_onRe",profileId);


        // Retrieving the ProfileManager singleton
        profileManager = ProfileManager.getInstance();



    }
}
