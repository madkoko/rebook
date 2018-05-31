package it.polito.mad.koko.kokolab3.profile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

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
    private static boolean running;

    /**
     * Initializing static fields
     */
    static {
        currentUserProfileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieving the updated user info from Firebase
                currentUserProfile = dataSnapshot.getValue(Profile.class);
                Log.d(TAG, "This user profile has been updated: " + currentUserProfile.toString());

                /*  Saving the updated user info into a binary file in case the
                    application will be closed */
                try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("profile.bin"));) {
                    outputStream.writeObject(currentUserProfile);
                } catch(Exception e) {}

                // Checking whether the user has completed the registration process
                Log.d(TAG, "Registration completed: " + ProfileManager.hasCompletedRegistration());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ProfileService#onStartCommand()");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        running = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "ProfileService#onCreate()");

        running = true;

        attachCurrentUserProfileListener();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    public static boolean isRunning() {
        return running;
    }
}