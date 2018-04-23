package it.polito.mad.koko.kokolab2.profile;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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


public class ProfileManager {

    private List<Profile> lista = Collections.synchronizedList(new ArrayList());
    private static Map<String,Profile> profileMap=null;
    private static ProfileManager ourInstance=null;
    private DatabaseReference usersRef;
    private StorageReference storageRef;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String userId;
    private Map<String, Object> childUpdates;
    private String downloadUrl;
    private FirebaseUser firebaseUser;
    private Profile profile;



    public ProfileManager(){
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference().child("users").child(userId);
        profile = new Profile();
        loadProfile();
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
        return lista;
    }


    public static Map<String, Profile> getProfile(){
        return profileMap;
    }


    public void loadProfile(){
        usersRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profileMap = new HashMap<>();
                        profileMap.clear();
                        profile.setName(dataSnapshot.child("name").getValue(String.class));
                        profile.setEmail(dataSnapshot.child("email").getValue(String.class));
                        profile.setBio(dataSnapshot.child("bio").getValue(String.class));
                        profile.setLocation(dataSnapshot.child("location").getValue(String.class));
                        profile.setPhone(dataSnapshot.child("phone").getValue(String.class));
                        profile.setImgUrl(dataSnapshot.child("image").getValue(String.class));
                        profileMap.put(userId, profile);
                        System.out.println(profileMap);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                });
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
        /*firebaseUser.updateProfile(new UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .build()
        );
        */
    }

}
