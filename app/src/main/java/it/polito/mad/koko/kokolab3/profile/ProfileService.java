package it.polito.mad.koko.kokolab3.profile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileService extends Service {

    private static final String TAG = "ProfileService";

    @Override
    public void onCreate() {
        super.onCreate();

        /*ProfileManager.getCurrentUserReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileManager.currentUserProfile = dataSnapshot.getValue(Profile.class);
                Log.d(TAG, "This user profile has been updated: " + ProfileManager.currentUserProfile.toString());

                // Checking whether the user has completed the registration process
                ProfileManager.checkRegistrationCompleted();
                Log.d(TAG, "Registration completed: " + ProfileManager.hasCompletedRegistration());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        });*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
