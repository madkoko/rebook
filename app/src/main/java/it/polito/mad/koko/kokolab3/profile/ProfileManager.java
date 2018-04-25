package it.polito.mad.koko.kokolab3.profile;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton profile manager class
 */
public class ProfileManager {

    /**
     * Unique instance
     */
    private static ProfileManager instance = null;

    /**
     * User data
     */
    private Profile profile;
    // private static Map<String,Profile> profileMap = null;
    private String profileId;

    /**
     * Firebase objects
     */
    private DatabaseReference usersRef;
    private StorageReference storageRef;
    private Map<String, Object> childUpdates;
    private String downloadUrl;

    private List<Profile> list = Collections.synchronizedList(new ArrayList());

    /**
     * synchronized method for different thread
     * @return ProfileManager instance
     */
    public static synchronized ProfileManager getInstance() {
        if(instance == null)
            instance = new ProfileManager();
        return instance;
    }

    protected ProfileManager() {
        profile = new Profile();
    }

    public void loadProfile(DatabaseReference usersRef){
        this.usersRef=usersRef;
        usersRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        synchronized (list) {
                            profile.setName(dataSnapshot.child("name").getValue(String.class));
                            profile.setEmail(dataSnapshot.child("email").getValue(String.class));
                            profile.setBio(dataSnapshot.child("bio").getValue(String.class));
                            profile.setLocation(dataSnapshot.child("location").getValue(String.class));
                            profile.setPhone(dataSnapshot.child("phone").getValue(String.class));
                            profile.setImgUrl(dataSnapshot.child("image").getValue(String.class));
                            list.clear();
                            list.add(profile);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public Profile getProfile(){
        synchronized (list) {
            return list.get(0);
        }
    }

    public String getProfileId() {
        return profileId;
    }

    /**
     * Manager for add Profile on Firebase
     * @param email email of user
     */
    @SuppressLint("LongLogTag")
    public void addProfile(String email){
        //This is for future implementation of Auth
        /*Profile profile=new Profile(name,email,phone,location,bio,imgUrl);
        usersRef.push().setValue(profile);*/


        //Profile profile = new Profile(name,email);
        //usersRef.setValue(profile);
        usersRef.child("email").setValue(email);
    }


    public void editProfile(String name, String email, String phone, String location, String bio, byte[] data, StorageReference storageRef) {
        this.storageRef=storageRef;
        childUpdates = new HashMap<>();
        /*
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", profileId.toString())
                .build();
                */
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                usersRef.child("image").setValue(downloadUrl);
            }
        });
        childUpdates.put("name", name);
        childUpdates.put("email", email);
        childUpdates.put("phone", phone);
        childUpdates.put("location", location);
        childUpdates.put("bio", bio);
        usersRef.updateChildren(childUpdates);
        /*firebaseUser.updateProfile(new UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .build()
        );
        */
    }

}
