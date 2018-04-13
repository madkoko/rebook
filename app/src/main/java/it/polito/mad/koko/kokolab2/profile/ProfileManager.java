package it.polito.mad.koko.kokolab2.profile;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProfileManager {

    private List<Profile> list = Collections.synchronizedList(new ArrayList());
    private static ProfileManager ourInstance=null;
    private DatabaseReference booksRef;



    public ProfileManager(){

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
     * @param database reference
     */

    public void addProfile(String name, String email, String phone, String location, String bio, FirebaseDatabase database){
        booksRef = database.getReference().child("users");
        Profile profile=new Profile(name,email,phone,location,bio);
        booksRef.push().setValue(profile);
    }
}
