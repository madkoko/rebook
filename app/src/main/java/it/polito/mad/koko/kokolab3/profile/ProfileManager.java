package it.polito.mad.koko.kokolab3.profile;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.polito.mad.koko.kokolab3.ui.ImageManager;

/**
 * Singleton profile manager class
 */
public class ProfileManager {

    private static final String TAG = "ProfileManager";

    private static Profile currentUser;

    private static Profile otherUser;

    /**
     * Unique instance
     */
    private static ProfileManager instance = null;

    /**
     * Firebase objects
     */
    private DatabaseReference usersRef;
    private StorageReference storageRef;
    private Map<String, Object> childUpdates;
    private String downloadUrl;

    private static ConcurrentMap<String,Profile> allUsers = new ConcurrentHashMap<String, Profile>();

    /**
     * synchronized method for different thread
     * @return ProfileManager instance
     */
    public static synchronized ProfileManager getInstance() {
        if(instance == null)
            instance = new ProfileManager();
        return instance;
    }

    public static void reset() {
        instance = new ProfileManager();
    }

    protected ProfileManager() {
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    /* METHOD TO RETRIEVE ALL THE USERS AND USE IT INTO SHOW SEARCHED BOOKS
     * CREATED BY FRANCESCO PETRO
     * */

    public void populateUsersList(){
        synchronized (allUsers) {
            usersRef.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                allUsers = new ConcurrentHashMap<>();
                                allUsers.clear();

                                allUsers.putAll((Map<String, Profile>)dataSnapshot.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    public ConcurrentMap<String, Profile> getAllUsers(){
        synchronized (allUsers) {
            return allUsers;
        }
    }

    public Profile getProfile(String Uid) {
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
                    userInfo.get("tokenMessage"),
                    userInfo.get("rating"));

            return profile;
        }
    }
    /**
     * Manager for add Profile on Firebase
     * @param email email of user
     */
    @SuppressLint("LongLogTag")
    public void addProfile(String uid, String email){
        //This is for future implementation of Auth
        /*Profile profile=new Profile(name,email,phone,location,bio,imgUrl);
        usersRef.push().setValue(profile);*/


        //Profile profile = new Profile(name,email);
        //usersRef.setValue(profile);
        usersRef.child(uid).child("email").setValue(email);
    }


    public void editProfile(String id, String name, String email, String phone, String location, String bio, byte[] data, String latLng, StorageReference storageRef) {
        DatabaseReference Ref = usersRef.child(id);
        this.storageRef=storageRef;
        childUpdates = new HashMap<>();
        /*
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", profileId.toString())
                .build();
                */
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            downloadUrl = taskSnapshot.getDownloadUrl().toString();
            Ref.child("image").setValue(downloadUrl);
            if(downloadUrl!=null)ImageManager.loadBitmap(downloadUrl);
        });
        childUpdates.put("name", name);
        childUpdates.put("email", email);
        childUpdates.put("phone", phone);
        childUpdates.put("location", location);
        childUpdates.put("bio", bio);
        if(latLng!=null)childUpdates.put("position",latLng);
        Ref.updateChildren(childUpdates);
        /*firebaseUser.updateProfile(new UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .build()
        );
        */
    }


    public boolean profileIsNotPresent(String uid) {
        synchronized (allUsers){
            Iterator it = allUsers.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry entry = (Map.Entry)it.next();
                if(entry.getKey().equals(uid)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addToken(String token, String uid) {
        usersRef.child(uid).child("tokenMessage").setValue(token);
    }

    // Listener to retrieve the current user from firebase
    public void retrieveCurrentUser() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> currentUserSnapshot = (Map<String, String>) dataSnapshot.getValue();
                    String bio = currentUserSnapshot.get("bio");
                    String email = currentUserSnapshot.get("email");
                    String image = currentUserSnapshot.get("image");
                    String location = currentUserSnapshot.get("location");
                    String name = currentUserSnapshot.get("name");
                    String phone = currentUserSnapshot.get("phone");
                    String position = currentUserSnapshot.get("position");
                    String tokenMessage = currentUserSnapshot.get("tokenMessage");
                    String rating = currentUserSnapshot.get("rating");
                    currentUser = new Profile(name, email, phone, location, bio, image, position, tokenMessage, rating);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Profile getCurrentUser() {
        return currentUser;
    }

    public void retriveInformationUser(String uid) {
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> otherUserSnapshot = (Map<String, String>) dataSnapshot.getValue();
                    String bio = otherUserSnapshot.get("bio");
                    String email = otherUserSnapshot.get("email");
                    String image = otherUserSnapshot.get("image");
                    String location = otherUserSnapshot.get("location");
                    String name = otherUserSnapshot.get("name");
                    String phone = otherUserSnapshot.get("phone");
                    String position = otherUserSnapshot.get("position");
                    String tokenMessage = otherUserSnapshot.get("tokenMessage");
                    String rating = otherUserSnapshot.get("rating");
                    otherUser = new Profile(name, email, phone, location, bio, image, position, tokenMessage, rating);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Profile getOtherUser() {return otherUser;}

    public void setRating(String uid, int valutation){
        usersRef.child(uid).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String rating = (String) dataSnapshot.getValue();
                if(rating!= null && !rating.isEmpty()){
                    int ratingNum = Integer.parseInt(rating);
                    ratingNum+=valutation;
                    String finalRating = ""+ratingNum;
                    usersRef.child(uid).child("rating").setValue(finalRating);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
