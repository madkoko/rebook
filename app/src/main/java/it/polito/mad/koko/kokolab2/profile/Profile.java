package it.polito.mad.koko.kokolab2.profile;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Profile {

    private String  name,
                    email,
                    phone,
                    location,
                    bio;

    /**
     * Profile profile data is stored in a firebase database.
     */
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;

    /**
     *  class that implement Profile
     * @param mDatabase firebase database
     * @param mFirebaseUser firebase user information
     */
    public Profile(DatabaseReference mDatabase, FirebaseUser mFirebaseUser) {
        this.mDatabase = mDatabase;
        this.mFirebaseUser = mFirebaseUser;
    }

    public Profile(String name, String email, String phone, String location, String bio) {
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.location=location;
        this.bio=bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
