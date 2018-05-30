package it.polito.mad.koko.kokolab3.request

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import it.polito.mad.koko.kokolab3.books.Book
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


    private var instance: RequestManager? = null

    /**
     * Firebase objects
     */
    //private var requestsRef: DatabaseReference? = null
    //private val reqStorageRef: StorageReference? = null

    private var storageRef: StorageReference? = null
    private var childUpdates: MutableMap<String, Any>? = null
    private var downloadUrl: String? = null

    /*
    //private var allRequests: Map<String, Request>? = HashMap<String, Request>();

    /**
     * synchronized method for different thread
     * @return ProfileManager instance
     */
    @Synchronized
    fun getInstance(): RequestManager {
        if (instance == null)
            instance = it.polito.mad.koko.kokolab3.request.RequestManager()
        return instance
    }

    fun reset() {
        instance = it.polito.mad.koko.kokolab3.request.RequestManager()
    }

    protected fun RequestManager() {
        requestsRef = FirebaseDatabase.getInstance().reference.child("requests")
    }

    /* METHOD TO RETRIEVE ALL THE USERS AND USE IT INTO SHOW SEARCHED BOOKS

    fun populateUsersList() {
        synchronized(allRequests) {
            requestsRef?.addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                allRequests = HashMap<String, Request>()
                                allRequests.clear()

                                allRequests.putAll((dataSnapshot.value as Map<String, Request>?)!!)
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
*/

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
 */

    fun newRequest(req: Request, data: ByteArray) {

        /* non mi serve perché recupero tutto da req

        var senderID: String? = null;
        var receiverID: String? = null;
        var bookID: String? = null;

        var bookName: String? = null;
        var bookImage: String? = null;
        var status: String? = null;

        var rating: Int? = null;
        var comment: String? = null;
    */

        val database = FirebaseDatabase.getInstance()
        val storage = FirebaseStorage.getInstance()

        val reqDatabaseRef = database.reference.child("requests")

        val reqKey = reqDatabaseRef.push().getKey()

        val reqStorageRef = storage.reference.child("requests")
        val uploadTask = reqStorageRef!!.child(reqKey).putBytes(data)

        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            downloadUrl = taskSnapshot.downloadUrl!!.toString()
            //Log.d(TAG,downloadUrl);
            //req.image = downloadUrl
            //ref.child(bookKey).child("image").setValue(downloadUrl);
            reqDatabaseRef.child(reqKey).setValue(req)
        })

        Log.d(TAG, req.toString())



        /*

        val bookKey = booksDatabaseRef.push().getKey()

        val uploadTask = booksStorageRef.child(bookKey).putBytes(data)
        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            downloadUrl = taskSnapshot.downloadUrl!!.toString()
            //Log.d(TAG,downloadUrl);
            book.setImage(downloadUrl)
            //ref.child(bookKey).child("image").setValue(downloadUrl);
            booksDatabaseRef.child(bookKey).setValue(book)
        })
*/

        /*
        val Ref = usersRef.child(id)
        this.storageRef = storageRef
        childUpdates = HashMap()

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", profileId.toString())
                .build();


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
        firebaseUser.updateProfile(new UserProfileChangeRequest
                .Builder()
                .setDisplayName(name)
                .build()
        );
        */
    }

/*
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