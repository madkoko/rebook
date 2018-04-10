package it.polito.mad.koko.kokolab2;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class User {

    private String name,
            email,
            location,
            bio;
    /**
     * User profile data is stored in a firebase database.
     */
    private DatabaseReference mDatabase;
    private String userId;


    public User(String userId, DatabaseReference mDatabase){
        this.userId=userId;
        this.mDatabase=mDatabase;
    }


    public String getName() {
        readName(new FirebaseCallback() {
            @Override
            public void onCallback(String value) {
                name=value;
            }
        });
        return name;
    }



    public void setName(String name) {
        this.name=name;
        mDatabase.child("users").child(userId).child("name").setValue(name);
    }

    public String getEmail() {/*
        DatabaseReference emailReference = mDatabase.child("users").child(userId).child("email");
        emailReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        mDatabase.child("users").child(userId).child("email").setValue(email);
    }

    public String getLocation() {/*
        DatabaseReference nameReference = databaseUserInfo.child("location");
        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {/*
        DatabaseReference nameReference = databaseUserInfo.child("bio");
        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bio=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        return bio;
    }

    private void setBio(String bio) {
        this.bio = bio;
    }


    public void readName(final FirebaseCallback firebaseCallback){
        DatabaseReference nameReference = mDatabase.child("users").child(userId).child("name");
        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name =dataSnapshot.getValue().toString();
                Log.d("TAG1", name);
                setName(name);
                firebaseCallback.onCallback(name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface FirebaseCallback{
        void onCallback(String value);
    }



}
