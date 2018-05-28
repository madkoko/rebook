package it.polito.mad.koko.kokolab3.auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import it.polito.mad.koko.kokolab3.auth.custom.ChooserActivity;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class AuthenticationUI {

    private static final String TAG = "AuthenticationUI";

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
     * It creates a login/sign in UI depending on the
     * specific setting.
     */
    public static void launch(AppCompatActivity activity) {
        Log.d(TAG, "launch() called");

        // If the user has not logged in
        if (!ProfileManager.hasLoggedIn()) {
            Log.d(TAG, "User has not logged in already");

            if (FIREBASE_UI)
                firebaseUI(activity);
            else
                customAuthUI(activity);
        } else
            Log.d(TAG, "User has already logged in");
    }

    /**
     * It creates the pre-defined login/sign in UI made
     * available by Firebase.
     */
    private static void firebaseUI(AppCompatActivity activity) {
        // Setting authentication providers
        List<com.firebase.ui.auth.AuthUI.IdpConfig> providers = Arrays.asList(
                new com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder().build(),
                new com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        activity.startActivityForResult(
                // Get an instance of AuthUI based on the default app
                com.firebase.ui.auth.AuthUI.getInstance()
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
    private static void customAuthUI(AppCompatActivity activity) {
        activity.startActivityForResult(
                new Intent(activity.getApplicationContext(), ChooserActivity.class), 1
        );
    }
}
