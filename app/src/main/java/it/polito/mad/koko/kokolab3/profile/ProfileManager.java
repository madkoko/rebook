package it.polito.mad.koko.kokolab3.profile;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.polito.mad.koko.kokolab3.firebase.DatabaseManager;
import it.polito.mad.koko.kokolab3.ui.ImageManager;

public class ProfileManager {

    private static final String TAG = "ProfileManager";

    /**
     * Firebase objects
     */
    private static Map<String, Object> newUserObject;
    private static String newImageURL;

    private static ConcurrentMap<String, Profile> allUsers;

    static {
        allUsers = new ConcurrentHashMap<>();
    }

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
                                allUsers = new ConcurrentHashMap<>();
                                allUsers.clear();

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

    public static ConcurrentMap<String, Profile> getAllUsers() {
        synchronized (allUsers) {
            return allUsers;
        }
    }

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

    public static Profile getProfile() {
        return ProfileService.getCurrentUserProfile();
    }

    /**
     * Manager for add Profile on Firebase
     *
     * @param email email of user
     */
    @SuppressLint("LongLogTag")
    public static void addProfile(String uid, String email) {
        //This is for future implementation of Auth
        /*Profile profile=new Profile(name,email,phone,location,bio,imgUrl);
        DatabaseManager.get("users").push().setValue(profile);*/

        //Profile profile = new Profile(name,email);
        //DatabaseManager.get("users").setValue(profile);

        DatabaseManager.set(email, "users", uid, "email");
    }


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
            newImageURL = taskSnapshot.getDownloadUrl().toString();

            // Updating the current user profile offline object
            ProfileService.getCurrentUserProfile().setImage(newImageURL);

            // Updating it on Firebase
            userChildFirebaseReference.child("image").setValue(newImageURL);

            // Loading the new image in the UI
            if (newImageURL != null)
                ImageManager.loadBitmap(newImageURL);
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

    public static boolean profileIsNotPresent(String uid) {
        synchronized (allUsers) {
            Iterator it = allUsers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getKey().equals(uid)) {
                    return false;
                }
            }
        }
        return true;
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
        ProfileService.detachCurrentUserProfileListener();

        Log.d(TAG, "Logged out.");
    }
}
