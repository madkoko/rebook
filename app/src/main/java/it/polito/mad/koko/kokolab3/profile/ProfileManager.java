package it.polito.mad.koko.kokolab3.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.polito.mad.koko.kokolab3.firebase.DatabaseManager;
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener;
import it.polito.mad.koko.kokolab3.ui.ImageManager;

public class ProfileManager {

    private static final String TAG = "ProfileManager";

    /**
     * Firebase objects
     */
    private static Map<String, Object> newUserObject;

    /**
     * Current user's Profile
     */
    private static Profile currentUserProfile;
    private static String currentUserImageURL;
    private static final File profileFile = new File("data/data/it.polito.mad.koko.kokolab3/files/profile.bin");

    /**
     * All users' profiles
     */
    private static ConcurrentMap<String, Profile> allUsers = new ConcurrentHashMap<>();

    /**
     * Retrieving all users (used when showing searched books)
     */
    public static void populateUsersList() {
        synchronized (allUsers) {
            DatabaseManager.get("users").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Initializing the allUsers data structure
                                allUsers = new ConcurrentHashMap<>();
                                allUsers.clear();

                                // Downloading all users' profiles
                                allUsers.putAll((Map<String, Profile>) dataSnapshot.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    /**
     * Returns true if the user has logged in,
     * false otherwise.
     *
     * @return whether the user has logged in or not.
     */
    public static boolean hasLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * @return the current user object on Firebase.
     */
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * @return the current user ID on Firebase.
     */
    public static String getCurrentUserID() {
        return hasLoggedIn() ? getCurrentUser().getUid() : null;
    }

    /**
     * @return the current user database reference on Firebase.
     */
    public static DatabaseReference getCurrentUserReference() {
        return hasLoggedIn() ? DatabaseManager.get("users", getCurrentUserID()) : null;
    }

    /**
     * It returns the specified user profile information.
     * @param Uid   the desired user profile.
     * @return      the specified user profile information.
     */
    public static Profile getProfile(String Uid) {
        synchronized (allUsers) {
            Map<String, String> userInfo = (Map<String, String>) allUsers.get(Uid);

            Profile profile = new Profile(
                    userInfo.get("name"),
                    userInfo.get("email"),
                    userInfo.get("phone"),
                    userInfo.get("location"),
                    userInfo.get("bio"),
                    userInfo.get("image"),
                    userInfo.get("position"),
                    userInfo.get("tokenMessage"));

            return profile;
        }
    }

    /**
     * @return  the current user profile information
     */
    public static Profile getProfile() {
        Profile currentUserProfile = null;

        // Retrieving the current profile object from the binary file
        try(ObjectInputStream oinf = new ObjectInputStream(new FileInputStream(profileFile))) {
            currentUserProfile = (Profile)oinf.readObject();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return currentUserProfile;
    }

    /**
     * Manager for add Profile on Firebase
     *
     * @param email email of user
     */
    @SuppressLint("LongLogTag")
    public static void addProfile(String uid, String email) {
        DatabaseManager.set(email, "users", uid, "email");
    }

    /**
     * It updates a profile on Firebase using the specified data.
     *
     * @param id
     * @param name
     * @param email
     * @param phone
     * @param location
     * @param bio
     * @param data
     * @param position
     * @param storageRef
     */
    public static void updateProfile(String id,
                                     String name,
                                     String email,
                                     String phone,
                                     String location,
                                     String bio,
                                     byte[] data,
                                     String position,
                                     StorageReference storageRef) {

        DatabaseReference userChildFirebaseReference = DatabaseManager.get("users", id);

        // Uploading the new profile image
        storageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            // Retrieving the new profile image URL
            currentUserImageURL = taskSnapshot.getDownloadUrl().toString();

            // Updating the current user profile offline object
            getProfile().setImage(currentUserImageURL);

            // Updating it on Firebase
            userChildFirebaseReference.child("image").setValue(currentUserImageURL);

            // Loading the new image in the UI
            if (currentUserImageURL != null)
                ImageManager.loadBitmap(currentUserImageURL);
        });

        // New user info data structure
        newUserObject = new HashMap<>();

        // Filling the new user info map
        newUserObject.put("name", name);
        newUserObject.put("email", email);
        newUserObject.put("phone", phone);
        newUserObject.put("location", location);
        newUserObject.put("bio", bio);
        newUserObject.put("position", position);

        // Saving new info in Firebase
        userChildFirebaseReference.updateChildren(newUserObject);
    }

    public static void addToken(String uid, String token) {
        DatabaseManager.set(token, "users", uid, "tokenMessage");
    }

    /**
     * It performs the logout operation.
     */
    public static void logout() {
        Log.d(TAG, "Logging out...");

        FirebaseAuth.getInstance().signOut();

        Log.d(TAG, "Logged out.");
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
        Profile profile = getProfile();

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

    /**
     * It reads the current user profile information immediately.
     * @param context
     * @param listener
     */
    public static void readProfile(Context context, final OnGetDataListener listener) {
        listener.onStart();
        ProfileManager.getCurrentUserReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieving the updated user info from Firebase
                currentUserProfile = dataSnapshot.getValue(Profile.class);
                Log.d(TAG, "This user profile has been updated: " + currentUserProfile.toString());

                /*  Saving the updated user info into a binary file in case the
                    application will be closed */
                try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(profileFile))) {
                    outputStream.writeObject(currentUserProfile);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Profile saved into: " + profileFile.getAbsolutePath());

                // Calling the listener's callback
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());

                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * @return  true if the current user profile file already exists.
     *          false otherwise.
     */
    public static boolean profileFileExists() {
        return profileFile.exists();
    }
}
