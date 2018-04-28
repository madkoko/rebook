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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.koko.kokolab3.auth.Authenticator;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.BookManager;
import it.polito.mad.koko.kokolab3.books.InsertBook;
import it.polito.mad.koko.kokolab3.books.SearchBooks;
import it.polito.mad.koko.kokolab3.books.ShowBooks;
import it.polito.mad.koko.kokolab3.profile.EditProfile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    /**
     * Custom class managing the user authentication
     */
    private Authenticator authenticator;

    /**
     * User profile information
     */
    private ProfileManager profileManager;

    /**
     * Result codes needed to distinguish among all possible activities launched
     * by this one.
     */
    private static final int    AUTH = 10;

    private int INSERT_BOOK = 1;
    /**
     * Request code for the activity "ShowBooks" to show only the current user's books
     */
    private int USER_BOOKS = 0;

    private int SEARCH_BOOKS = 2;
    private ValueEventListener valueEventListener;
    private DatabaseReference useRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticator = new Authenticator(this);

        // UI
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent insertBook = new Intent(getApplicationContext(), InsertBook.class);
                insertBook.putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                BookManager.removeUserBooksEventListener();
                BookManager.removeSearchBooksEventListener();
                startActivityForResult(insertBook, INSERT_BOOK);
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

        new BookManager();

        // creation of the BookManager if the user is authenticated
        if (authenticator.hasLoggedIn()) {

            // Retrieving the ProfileManager singleton
            profileManager = ProfileManager.getInstance();
            profileManager.loadProfile(authenticator.getDatabase().getReference().child("users").child(authenticator.getUser().getUid()));
            BookManager.populateUserBookList();
        }
    }

    /**
     * When the sign-in flow is complete
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_BOOKS && resultCode != RESULT_CANCELED) {

            Intent showSearchBooks = new Intent(getApplicationContext(), ShowBooks.class);
            showSearchBooks.putExtra("request_code", SEARCH_BOOKS);
            startActivity(showSearchBooks);
        }

        // Debugging
        Log.d(TAG, "HomeActivity::onActivityResult() has been called");
        Log.d(TAG, "requestCode: " + requestCode);
        Log.d(TAG, "resultCode: " + resultCode);

        // Returning in HomeActivity from an Authentication procedure
        if(resultCode == AUTH) {
            // Debug
            Log.d(TAG, "Returning in HomeActivity from an Authentication procedure.");

            // Inform the user of the successful authentication

            Toast.makeText(this, "Successfully signed in", Toast.LENGTH_LONG).show();
            authenticator.instantiateUser();

            profileManager = ProfileManager.getInstance();
            Log.d(TAG, String.valueOf(profileManager));
            useRef = authenticator.getDatabase().getReference().child("users").child(authenticator.getUser().getUid());
            profileManager.loadProfile(useRef);

            // Creating the Firebase user entry in the database
            profileManager.addProfile(
                    authenticator.getUser().getEmail()
            );
            BookManager.populateUserBookList();
            valueEventListener = useRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String position = dataSnapshot.child("position").getValue(String.class);
                    if (position == null || position == "") {
                        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
            i.putExtra("UserID", authenticator.getUser().getUid());
            startActivity(i);

        } else if (id == R.id.edit_profile) {
            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(intent);

        } else if (id == R.id.my_books) {

            Intent showBooks = new Intent(getApplicationContext(), ShowBooks.class);
            showBooks.putExtra("request_code", USER_BOOKS);
            startActivity(showBooks);

        } else if (id == R.id.search_books) {
            Intent searchBooks = new Intent(getApplicationContext(), SearchBooks.class);
            BookManager.removeUserBooksEventListener();
            startActivityForResult(searchBooks, SEARCH_BOOKS);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sign_out) {
            authenticator.signOut();
            useRef.removeEventListener(valueEventListener);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
