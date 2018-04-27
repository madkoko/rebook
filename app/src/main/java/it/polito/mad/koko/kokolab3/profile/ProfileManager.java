package it.polito.mad.koko.kokolab3.profile;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    private static final String TAG = "ProfileManager";

    /**
     * Unique instance
     */
    private static ProfileManager instance = null;

    /**
     * User data
     */
    private Profile profile;
    // private static Map<String,Profile> profileMap = null;

    /**
     * Firebase objects
     */
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private Map<String, Object> childUpdates;
    private String downloadUrl;

    private List<Profile> listAutProfile = Collections.synchronizedList(new ArrayList());
    private List<Profile> listForBook= Collections.synchronizedList(new ArrayList());

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

    public void loadProfile(DatabaseReference userRef){
        this.userRef=userRef;
        userRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        synchronized (listAutProfile) {
                            profile.setName(dataSnapshot.child("name").getValue(String.class));
                            profile.setEmail(dataSnapshot.child("email").getValue(String.class));
                            profile.setBio(dataSnapshot.child("bio").getValue(String.class));
                            profile.setLocation(dataSnapshot.child("location").getValue(String.class));
                            profile.setPhone(dataSnapshot.child("phone").getValue(String.class));
                            profile.setImgUrl(dataSnapshot.child("image").getValue(String.class));
                            profile.setPosition(dataSnapshot.child("position").getValue(String.class));
                            listAutProfile.clear();
                            listAutProfile.add(profile);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    public Profile getProfile(){
        synchronized (listAutProfile) {
            return listAutProfile.get(0);
        }
    }


    /**
     *  For implementation of listBooks
     * @param usersStringRef List of String with userId
     */

    public void loadListForBook(List<String> usersStringRef){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        listForBook.clear();
        for (int i = 0; i<usersStringRef.size(); i++){
            DatabaseReference usersRef = database.getReference().child("users").child(usersStringRef.get(i));
            usersRef.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            synchronized (listForBook) {
                                profile.setName(dataSnapshot.child("name").getValue(String.class));
                                profile.setEmail(dataSnapshot.child("email").getValue(String.class));
                                profile.setBio(dataSnapshot.child("bio").getValue(String.class));
                                profile.setLocation(dataSnapshot.child("location").getValue(String.class));
                                profile.setPhone(dataSnapshot.child("phone").getValue(String.class));
                                profile.setImgUrl(dataSnapshot.child("image").getValue(String.class));
                                profile.setPosition(dataSnapshot.child("position").getValue(String.class));
                                listForBook.add(profile);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    }

    /**
     *
     * @return list of profile for each book
     */

    public List<Profile> getBookProfiles(){
        synchronized (listForBook){
            return listForBook;
        }
    }

    /**
     *
     * @param i list position of profile
     * @return one profile from position i
     */

    public Profile getBookProfile(int i){
        synchronized (listForBook){
            return listForBook.get(i);
        }
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
        userRef.child("email").setValue(email);
    }


    public void editProfile(String name, String email, String phone, String location, String bio, byte[] data, String latLng, StorageReference storageRef) {
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
                userRef.child("image").setValue(downloadUrl);
            }
        });
        childUpdates.put("name", name);
        childUpdates.put("email", email);
        childUpdates.put("phone", phone);
        childUpdates.put("location", location);
        childUpdates.put("bio", bio);
        if(latLng!=null)childUpdates.put("position",latLng);
        userRef.updateChildren(childUpdates);
        /*firebaseUser.updateProfile(new UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .build()
        );
        */
    }

}
