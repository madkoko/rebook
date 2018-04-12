package it.polito.mad.koko.kokolab2;

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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import it.polito.mad.koko.kokolab2.books.InsertBook;
import it.polito.mad.koko.kokolab2.books.ShowBooks;
import it.polito.mad.koko.kokolab2.users.EditProfile;
import it.polito.mad.koko.kokolab2.users.ShowProfile;
import it.polito.mad.koko.kokolab2.users.User;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;

    /**
     * Firebase user objects
     */
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    /**
     * Authentication providers
     */
    private List<IdpConfig> providers;

    private int INSERT_BOOK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting authentication providers
        providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        // UI
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instantiateUser();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent insertBook = new Intent(getApplicationContext(), InsertBook.class);
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

        Log.d("User is:", String.valueOf(mFirebaseUser));
        if(!hasLoggedIn()) {
            signInUI();
        }
    }

    /**
     *  mFirebaseAuth  instances  Auth
     *  mFirebaseUser instances User
     */
    private void instantiateUser(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    /**
     * Returns true if the user has logged in,
     * false otherwise.
     * @return  whether the user has logged in or not.
     */
    private boolean hasLoggedIn(){
        if(mFirebaseUser == null)
            return false;
        else
            return true;
    }

    /**
     * When the sign-in flow is complete
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in", Toast.LENGTH_LONG).show();
                instantiateUser();
                mDatabase = FirebaseDatabase.getInstance().getReference();

                //creation firebase (real time database) value
                User user = new User(mDatabase,mFirebaseUser);
                user.setName(mFirebaseUser.getDisplayName());
                user.setEmail(mFirebaseUser.getEmail());

                return;
            }else{
                //User pressed back button
                if (response == null) {
                    Toast.makeText(this, "User pressed back button", Toast.LENGTH_LONG).show();
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
        }
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
            i.putExtra("UserID", mFirebaseUser.getUid());
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

        } else if (id == R.id.sign_out)
            signOut();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * It creates a SingIn interface with Firebase-AuthUI
     */
    private void signInUI(){
        // Create and launch sign-in intent
        startActivityForResult(
            // Get an instance of AuthUI based on the default app
            AuthUI  .getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setAllowNewEmailAccounts(true)
                    .setIsSmartLockEnabled(true)
                    .build(),
            RC_SIGN_IN
        );
    }

    /**
     * Performs the user sign out.
     */
    private void signOut() {
        mFirebaseAuth.signOut();
        signInUI();
    }
}
