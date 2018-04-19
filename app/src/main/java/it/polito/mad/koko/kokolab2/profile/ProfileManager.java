package it.polito.mad.koko.kokolab2.profile;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileManager {

    private List<Profile> list = Collections.synchronizedList(new ArrayList());
    private static ProfileManager ourInstance=null;
    private DatabaseReference usersRef;
    private StorageReference storageRef;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String userId;
    private Map<String, Object> childUpdates;
    private String downloadUrl;
    private FirebaseUser firebaseUser;



    public ProfileManager(FirebaseDatabase database, FirebaseUser firebaseUser, FirebaseStorage storage){
        this.database=database;
        this.firebaseUser=firebaseUser;
        this.storage=storage;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = database.getReference().child("users").child(userId);
        storageRef = storage.getReference().child("users").child(userId);


    }

    public ProfileManager() {
    }

    /**
     * synchronized method for different thread
     * @return ProfileManager instance
     */
    public static synchronized ProfileManager getOurInstance() {
        if (ourInstance == null) ourInstance = new ProfileManager();
        return ourInstance;
    }

    /**
     * for future implementation list of users
     * @return list of user
     */
    public List<Profile> getProfiles() {
        return list;
    }

    /**
     * for future implementation user
     * @param i position of user
     * @return position of user from the list
     */
    public Profile getProfile(int i){
        synchronized (list) {
            return list.get(i);
        }
    }

    /**
     * Manager for add Profile on Firebase
     * @param name name of user
     * @param email email of user
     * @param phone phone of user
     * @param location location of user
     * @param bio bio of user
     */

    public void addProfile(String name, String email, String phone, String location, String bio, String imgUrl){

        //This is for future implementation of Auth
        /*Profile profile=new Profile(name,email,phone,location,bio,imgUrl);
        usersRef.push().setValue(profile);*/

        usersRef.child("name").setValue(name);
        usersRef.child("email").setValue(email);
    }


    public void editProfile(String name, String email, String phone, String location, String bio, byte[] data) {

        childUpdates = new HashMap<>();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", userId.toString())
                .build();
        UploadTask uploadTask = storageRef.putBytes(data,metadata);
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
        firebaseUser.updateProfile(new UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .build()
        );
    }

}
