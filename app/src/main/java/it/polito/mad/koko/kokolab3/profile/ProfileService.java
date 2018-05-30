package it.polito.mad.koko.kokolab3.profile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class ProfileService extends Service {

    private static final String TAG = "ProfileService";

    private static final String PACKAGE_NAME = "it.polito.mad.koko.kokolab3";

    /**
     * Current user's Profile
     */
    private static Profile currentUserProfile;
    private static final ValueEventListener currentUserProfileListener;

    /**
     * True upon completing the registration
     */
    private static boolean registrationCompleted;

    /**
     *
     */
    private static Context context;
    private static boolean running;

    static {
        currentUserProfileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieving the updated user info from Firebase
                currentUserProfile = dataSnapshot.getValue(Profile.class);
                Log.d(TAG, "This user profile has been updated: " + currentUserProfile.toString());

                /*  Saving the updated user info into a SharedPreference in case the
                    application will be closed */
                SharedPreferences.Editor sharedPreferencesEditor = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE).edit();
                sharedPreferencesEditor.putString("Profile", new Gson().toJson(currentUserProfile)).commit();
                sharedPreferencesEditor.apply();

                // Checking whether the user has completed the registration process
                Log.d(TAG, "Registration completed: " + hasCompletedRegistration());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        };
    }



    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        running = true;
        attachCurrentUserProfileListener();
    }

    public static Profile getCurrentUserProfile() {
        // Retrieving the current profile object from the SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(sharedPreferences.getString("Profile", ""), Profile.class);
    }

    /**
     * It checks whether this user has completed the registration.
     * This is done by checking that all minimum fields have been properly set.
     *
     * @return  true if the user has completed the registration.
     *          false otherwise.
     */
    public static boolean hasCompletedRegistration() {
        // Retrieving the current profile object
        Profile profile = getCurrentUserProfile();

        // 'name' field must be set
        String name = profile.getName();
        if (name == null || name.isEmpty() || name.compareTo("") == 0)
            return false;

        // 'position field must be set
        String position = profile.getPosition();
        if (position == null || position.isEmpty() || position.compareTo("") == 0)
            return false;

        return true;
    }

    public static void refreshCurrentUserProfileListener() {
        detachCurrentUserProfileListener();
        attachCurrentUserProfileListener();
    }

    /**
     * Attaching the value listener to the current user's Firebase child
     */
    private static void attachCurrentUserProfileListener() {
        if(ProfileManager.getCurrentUserReference() != null)
            ProfileManager.getCurrentUserReference().addValueEventListener(currentUserProfileListener);
    }

    /**
     * Detaching the value listener to the current user's Firebase child
     */
    protected static void detachCurrentUserProfileListener() {
        if(ProfileManager.getCurrentUserReference() != null)
            ProfileManager.getCurrentUserReference().removeEventListener(currentUserProfileListener);
        currentUserProfile = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isRunning() {
        return running;
    }
}
