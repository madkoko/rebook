package it.polito.mad.koko.kokolab3;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.auth.AuthenticationUI;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.koko.kokolab3.books.InsertBook;
import it.polito.mad.koko.kokolab3.books.SearchBooks;
import it.polito.mad.koko.kokolab3.books.ShowBooks;
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener;
import it.polito.mad.koko.kokolab3.messaging.MessageManager;
import it.polito.mad.koko.kokolab3.messaging.MyFirebaseInstanceIDService;
import it.polito.mad.koko.kokolab3.messaging.UserChatInfo;
import it.polito.mad.koko.kokolab3.messaging.tabShowChat.BookRequest;
import it.polito.mad.koko.kokolab3.profile.EditProfile;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;
import it.polito.mad.koko.kokolab3.tabsHomeActivity.HomeChatList;
import it.polito.mad.koko.kokolab3.tabsHomeActivity.HomeListBook;
import it.polito.mad.koko.kokolab3.tabsHomeActivity.HomeSharingBook;
import it.polito.mad.koko.kokolab3.ui.CircleTransform;
import it.polito.mad.koko.kokolab3.ui.ImageManager;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private static final String PACKAGE_NAME = "it.polito.mad.koko.kokolab3";

    /**
     * Result codes needed to distinguish among all possible activities launched
     * by this one.
     */
    private static final int AUTH = 10;

    private int INSERT_BOOK = 20;

    private int FIRST_LOGIN_EDIT_PROFILE = 30,
            LOGOUT_FROM_EDIT_PROFILE = 3,
            EDIT_PROFILE = 50;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 40;

    /**
     * Request code for the activity "ShowBooks" to show only the current user's books
     */
    private int USER_BOOKS = 0;
    private ListView listView;
    private ViewSwitcher viewSwitcher;
    private LinearLayout layoutRecycler;
    private LinearLayout layoutList;
    private Fragment homeListBook;
    private Fragment homeListChats;
    private Fragment homeSharingBook;
    private Fragment homeRequestBook;
    private NavigationView navigationView;

    //private int SEARCH_BOOKS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate() called");

        // check if camera and storage are allowed
        checkAndRequestPermissions();

        // If the local offline file containing the current user's information does not exist
        if (!ProfileManager.profileFileExists())
            // Force a logout operation
            ProfileManager.logout();

        // UI
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent insertBook = new Intent(getApplicationContext(), InsertBook.class);
            insertBook.putExtra("uid", ProfileManager.getCurrentUserID());
            startActivityForResult(insertBook, INSERT_BOOK);
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);

        // Launching the authentication UI
        AuthenticationUI.launch(this);

        ProfileManager.populateUsersList();

        // UI (tabs)
        viewSwitcher = findViewById(R.id.home_switcher);
        layoutRecycler = findViewById(R.id.home_recycler_switcher);
        layoutList = findViewById(R.id.home_list_switcher);
        TabLayout tab_layout = findViewById(R.id.tabs_home);
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        //Tab for list of Books
        tab_layout.addTab(tab_layout.newTab().setText(R.string.home));
        homeListBook = new HomeListBook();
        //Tab for list of Chats
        tab_layout.addTab(tab_layout.newTab().setText(R.string.chats_activity_title));
        homeListChats = new HomeChatList();
        //Tab for list of borrowed books
        tab_layout.addTab(tab_layout.newTab().setText(R.string.borrowed));
        homeSharingBook = new HomeSharingBook();
        //Tab for pending Request
        tab_layout.addTab(tab_layout.newTab().setText(R.string.request_book));
        //Fag for fragment
        int flag = 0;
        homeRequestBook = new BookRequest(flag, null);
        //Set first fragment
        selectFragment(0);
        //Add listener to tab_layout
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected" + String.valueOf(tab.getPosition()));
                selectFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabUnselected" + String.valueOf(tab.getPosition()));
                removeFragment(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabReselected" + String.valueOf(tab.getPosition()));
            }
        });

        // If the user has already logged in
        if (ProfileManager.hasLoggedIn()) {
            Log.d(TAG, "Registration completed: " + ProfileManager.hasCompletedRegistration());

            // If the user has not completed the registration process already
            if (!ProfileManager.hasCompletedRegistration()) {
                Intent editProfileIntent = new Intent(getApplicationContext(), EditProfile.class);
                editProfileIntent.putExtra("showLogoutButton", true);
                // Start the EditProfile activity
                startActivityForResult(editProfileIntent, FIRST_LOGIN_EDIT_PROFILE);

                return;
            }

            // Retrieve all current user's chats
            // TODO do we still need this?
            MessageManager.setUserChatsIDListener();
            MessageManager.populateUserChatsID();
        }
    }

    private void removeFragment(int position) {
        switch (position) {
            case 0:
                getFragmentManager().beginTransaction().remove(homeListBook).commit();
                break;
            case 1:
                getFragmentManager().beginTransaction().remove(homeListChats).commit();
                break;
            case 2:
                getFragmentManager().beginTransaction().remove(homeSharingBook).commit();
                break;
            case 3:
                getFragmentManager().beginTransaction().remove(homeRequestBook).commit();
                break;
            default:
                break;
        }

    }

    private void selectFragment(int position) {
        switch (position) {
            case 0:
                if (viewSwitcher.getCurrentView() != layoutRecycler) {
                    viewSwitcher.showPrevious();
                    getFragmentManager().beginTransaction().add(android.R.id.content, homeListBook).commit();
                }
                //new HomeListBook();
                break;
            case 1:
                if (viewSwitcher.getCurrentView() != layoutList) {
                    viewSwitcher.showNext();
                }
                getFragmentManager().beginTransaction().add(android.R.id.content, homeListChats).commit();
                break;
            case 2:
                if (viewSwitcher.getCurrentView() != layoutList) {
                    viewSwitcher.showNext();
                }
                getFragmentManager().beginTransaction().add(android.R.id.content, homeSharingBook).commit();
                break;
            case 3:
                if (viewSwitcher.getCurrentView() != layoutList) {
                    viewSwitcher.showNext();
                }
                getFragmentManager().beginTransaction().add(android.R.id.content, homeRequestBook).commit();
            default:

                break;
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

        Log.d(TAG, "onActivityResult() called");

        // Debugging
        Log.d(TAG, "HomeActivity::onActivityResult() has been called");
        Log.d(TAG, "requestCode: " + requestCode);
        Log.d(TAG, "resultCode: " + resultCode);

        // Returning in HomeActivity from an Authentication procedure
        if (resultCode == AUTH) {
            // Debug
            Log.d(TAG, "Returning in HomeActivity from an Authentication procedure.");

            // Inform the user of the successful authentication
            Toast.makeText(this, "Successfully signed in", Toast.LENGTH_LONG).show();

            // Retrieve all current user's chats
            MessageManager.setUserChatsIDListener();
            MessageManager.populateUserChatsID();

            // Loading the new profile on Firebase
            ProfileManager.addProfile(ProfileManager.getCurrentUserID(), ProfileManager.getCurrentUser().getEmail());
            MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
            myFirebaseInstanceIDService.onTokenRefresh();

            // If this is a new user or the user has not finished the registration
            ProfileManager.readProfile(new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot data) {
                    /*  If the user has not completed the registration procedure
                        (for instance it is a new user) */
                    if (!ProfileManager.hasCompletedRegistration()) {
                        Intent editProfileIntent = new Intent(getApplicationContext(), EditProfile.class);
                        editProfileIntent.putExtra("showLogoutButton", true);
                        // Start the EditProfile activity
                        startActivityForResult(editProfileIntent, FIRST_LOGIN_EDIT_PROFILE);
                    }
                    /*  If the user has already completed the registration and
                        has a profile picture */
                    else if (ProfileManager.getProfile().getImage() != null) {
                        // Load the profile picture in the UI
                        Profile p = ProfileManager.getProfile();
                        ImageManager.loadBitmap(p.getImage());

                    }
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                }
            });

        } else if (resultCode == LOGOUT_FROM_EDIT_PROFILE) {
            ProfileManager.logout();
            AuthenticationUI.launch(this);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() called");

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
        Log.d(TAG, "onOptionsItemSelected() called");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent searchBooks = new Intent(getApplicationContext(), SearchBooks.class);
            // BookManager.removeUserBooksEventListener();
            // startActivityForResult(searchBooks, SEARCH_BOOKS);
            startActivity(searchBooks);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected() called");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.view_profile) {
            Intent i = new Intent(getApplicationContext(), ShowProfile.class);
            i.putExtra("UserID", ProfileManager.getCurrentUserID());
            startActivity(i);

        } else if (id == R.id.edit_profile) {
            startActivity(new Intent(getApplicationContext(), EditProfile.class));
        } else if (id == R.id.my_books) {

            Intent showBooks = new Intent(getApplicationContext(), ShowBooks.class);
            showBooks.putExtra("request_code", USER_BOOKS);
            startActivity(showBooks);

        } else if (id == R.id.search_books) {
            Intent searchBooks = new Intent(getApplicationContext(), SearchBooks.class);
            // BookManager.removeUserBooksEventListener();
            // startActivityForResult(searchBooks, SEARCH_BOOKS);
            startActivity(searchBooks);
        } else if (id == R.id.sign_out) {
            ProfileManager.logout();
            AuthenticationUI.launch(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView sideMenuImage=navigationView.getHeaderView(0).findViewById(R.id.sideMenuImage);
        Picasso.get().load(R.mipmap.logo).fit().centerCrop().into(sideMenuImage);
        if(ProfileManager.getProfile()!=null){
            TextView sideMenuEmail=navigationView.getHeaderView(0).findViewById(R.id.sideMenuEmail);
            sideMenuEmail.setText(ProfileManager.getProfile().getEmail());
        }
    }

    /**
     * method to check camera and storage permissions
     */
    private void checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int writeStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);

        }
    }

}