package it.polito.mad.koko.kokolab2;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class User{


    private String  name,
                    email,
                    phone,
                    location,
                    bio;

    /**
     * User profile data is stored in a firebase database.
     */
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;

    /**
     *  class that implement User
     * @param mDatabase firebase database
     * @param mFirebaseUser firebase user information
     */
    public User(DatabaseReference mDatabase, FirebaseUser mFirebaseUser) {
        this.mDatabase = mDatabase;
        this.mFirebaseUser = mFirebaseUser;
    }

    /**
     *
     * @param name of user
     */
    public void setName(String name) {
        this.name = name;
        mDatabase.child("users").child(mFirebaseUser.getUid()).child("name").setValue(name);

    }

    /**
     *
     * @param email of user
     */
    public void setEmail(String email) {
        this.email = email;
        mDatabase.child("users").child(mFirebaseUser.getUid()).child("email").setValue(email);
    }

    /**
     *
     * @param phone of user
     */

    public void setPhone(String phone) {
        this.phone = phone;
        mDatabase.child("users").child(mFirebaseUser.getUid()).child("phone").setValue(phone);
    }

    /**
     *
     * @param location of user
     */
    public void setLocation(String location) {
        this.location = location;
        mDatabase.child("users").child(mFirebaseUser.getUid()).child("location").setValue(location);
    }

    /**
     *
     * @param bio of user
     */
    public void setBio(String bio) {
        this.bio = bio;
        mDatabase.child("users").child(mFirebaseUser.getUid()).child("bio").setValue(bio);
    }
}
