package it.polito.mad.koko.kokolab3.request

import android.annotation.SuppressLint
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import it.polito.mad.koko.kokolab3.profile.Profile
import it.polito.mad.koko.kokolab3.profile.ProfileManager
import it.polito.mad.koko.kokolab3.ui.ImageManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by Franci on 23/05/18.
 */
class RequestManager() {
    private val TAG = "RequestManager"

/*
    private var instance: ProfileManager? = null

    /**
     * Firebase objects
     */
    private var usersRef: DatabaseReference
    private var storageRef: StorageReference? = null
    private var childUpdates: MutableMap<String, Any>? = null
    private var downloadUrl: String? = null

    private var allUsers: ConcurrentMap<String, Profile> = ConcurrentHashMap()

    /**
     * synchronized method for different thread
     * @return ProfileManager instance
     */
    @Synchronized
    fun getInstance(): ProfileManager {
        if (instance == null)
            instance = it.polito.mad.koko.kokolab3.profile.ProfileManager()
        return instance
    }

    fun reset() {
        instance = it.polito.mad.koko.kokolab3.profile.ProfileManager()
    }

    protected fun ProfileManager(): ??? {
        usersRef = FirebaseDatabase.getInstance().reference.child("users")
    }

    /* METHOD TO RETRIEVE ALL THE USERS AND USE IT INTO SHOW SEARCHED BOOKS
     * CREATED BY FRANCESCO PETRO
     * */

    fun populateUsersList() {
        synchronized(allUsers) {
            usersRef.addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                allUsers = ConcurrentHashMap()
                                allUsers.clear()

                                allUsers.putAll((dataSnapshot.value as Map<String, Profile>?)!!)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
        }
    }

    fun getAllUsers(): ConcurrentMap<String, Profile> {
        synchronized(allUsers) {
            return allUsers
        }
    }

    fun getProfile(Uid: String): Profile {
        synchronized(allUsers) {
            val userInfo = allUsers[Uid] as Map<String, String>

            return Profile(
                    userInfo["name"],
                    userInfo["email"],
                    userInfo["phone"],
                    userInfo["location"],
                    userInfo["bio"],
                    userInfo["image"],
                    userInfo["position"],
                    userInfo["tokenMessage"])
        }
    }

    /**
     * Manager for add Profile on Firebase
     * @param email email of user
     */
    @SuppressLint("LongLogTag")
    fun addProfile(uid: String, email: String) {
        //This is for future implementation of Auth
        /*Profile profile=new Profile(name,email,phone,location,bio,imgUrl);
        usersRef.push().setValue(profile);*/


        //Profile profile = new Profile(name,email);
        //usersRef.setValue(profile);
        usersRef.child(uid).child("email").setValue(email)
    }


    fun updateProfile(id: String, name: String, email: String, phone: String, location: String, bio: String, data: ByteArray, latLng: String?, storageRef: StorageReference) {
        val Ref = usersRef.child(id)
        this.storageRef = storageRef
        childUpdates = HashMap()
        /*
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", profileId.toString())
                .build();
                */
        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            downloadUrl = taskSnapshot.downloadUrl!!.toString()
            Ref.child("image").setValue(downloadUrl)
            if (downloadUrl != null) ImageManager.loadBitmap(downloadUrl)
        }
        childUpdates!!.put("name", name)
        childUpdates!!.put("email", email)
        childUpdates!!.put("phone", phone)
        childUpdates!!.put("location", location)
        childUpdates!!.put("bio", bio)
        if (latLng != null) childUpdates!!.put("position", latLng)
        Ref.updateChildren(childUpdates!!)
        /*firebaseUser.updateProfile(new UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .build()
        );
        */
    }


    fun profileIsNotPresent(uid: String): Boolean {
        synchronized(allUsers) {
            val it = allUsers.entries.iterator()
            while (it.hasNext()) {
                val entry = it.next() as Entry<*, *>
                if (entry.key == uid) {
                    return false
                }
            }
        }
        return true
    }

    fun addToken(token: String, uid: String) {
        usersRef.child(uid).child("tokenMessage").setValue(token)
    }
    */
}