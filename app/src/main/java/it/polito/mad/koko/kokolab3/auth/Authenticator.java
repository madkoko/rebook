package it.polito.mad.koko.kokolab3.auth;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Arrays;
import java.util.List;

import it.polito.mad.koko.kokolab3.auth.custom.ChooserActivity;

public class Authenticator {

    /**
     * Sign in request code
     */
    private static final int RC_SIGN_IN = 123;

    /**
     * If true, FirebaseUI will be used.
     * Otherwise, a custom login/sign in UI will be displayed.
     */
    private static final boolean FIREBASE_UI = false;

    /**
     * Firebase user objects
     */
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;

    /**
     * Authentication providers
     */
    private List<AuthUI.IdpConfig> providers;

    /**
     * Activity to which this authenticator manages the user
     * authentication process.
     */
    private AppCompatActivity activity;

    /**
     * Default constructor.
     * @param activity  the activity this authenticator refers to.
     */
    public Authenticator(AppCompatActivity activity) {
        this.activity = activity;

        if(FIREBASE_UI)
            setProviders();

        instantiateUser();
    }

    /**
     *  auth  instances  Auth
     *  user instances Profile
     */
    public void instantiateUser(){
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Log.d("debug", "Profile is: " + String.valueOf(user));
    }

    /**
     * Returns true if the user has logged in,
     * false otherwise.
     * @return  whether the user has logged in or not.
     */
    public boolean hasLoggedIn(){
        if(user == null)
            return false;
        else
            return true;
    }

    /**
     * It creates a login/sign in UI depending on the
     * specific setting.
     */
    public void authUI(){
        Log.d("debug", "authUI() called");

        if(!hasLoggedIn()) {
            Log.d("debug", "User has not logged in already");

            if(FIREBASE_UI)
                firebaseUI();
            else
                customAuthUI();

            instantiateUser();
        }
        else
            Log.d("debug", "User has already logged in");
    }

    /**
     * Setting authentication providers for FirebaseUI.
     */
    private void setProviders() {
        // Setting authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
    }

    /**
     * It creates the pre-defined login/sign in UI made
     * available by Firebase.
     */
    private void firebaseUI() {
        // Create and launch sign-in intent
        activity.startActivityForResult(
            // Get an instance of AuthUI based on the default app
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAllowNewEmailAccounts(true)
                .setIsSmartLockEnabled(true)
                .build(),
            RC_SIGN_IN
        );
    }

    /**
     * It creates a custom login/sign in UI.
     */
    private void customAuthUI() {
        activity.startActivityForResult(
            new Intent(activity.getApplicationContext(), ChooserActivity.class),1
        );
    }

    /**
     * Performs the user sign out.
     */
    public void signOut() {
        Log.d("debug","signOut() called");
        auth.signOut();
        user = null;

        Log.d("debug","database: " + database + "\nauth: " + auth + "\nuser: " + user);

        authUI();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public FirebaseStorage getStorage() { return FirebaseStorage.getInstance(); }

    public static int getRcSignIn() {
        return RC_SIGN_IN;
    }

}
