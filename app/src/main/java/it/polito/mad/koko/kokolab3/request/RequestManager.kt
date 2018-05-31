package it.polito.mad.koko.kokolab3.request

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import it.polito.mad.koko.kokolab3.books.Book
import it.polito.mad.koko.kokolab3.firebase.DatabaseManager
import it.polito.mad.koko.kokolab3.messaging.UserChatInfo
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

    private var storageRef: StorageReference? = null
    private var childUpdates: MutableMap<String, Any>? = null
    private var downloadUrl: String? = null


    // Create a new Request and push it on Firebase
    companion object {

        private var reqIdRetrieved: String? = null

        fun newRequest(req: Request, data: ByteArray, reqId: String) {

            val database = FirebaseDatabase.getInstance()
            val storage = FirebaseStorage.getInstance()
            var downloadUrl: String;

            checkExistingReq(reqId);

            if (reqIdRetrieved == null) {
                val reqDatabaseRef = database.reference.child("requests")
                reqDatabaseRef.child(reqId).setValue(req);
                val reqStorageRef = storage.reference.child("requests")
                val uploadTask = reqStorageRef!!.child(reqId).putBytes(data)

                uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    downloadUrl = taskSnapshot.downloadUrl!!.toString()
                    //Log.d(TAG,downloadUrl);
                    req.bookImage = downloadUrl
                    //ref.child(bookKey).child("image").setValue(downloadUrl);
                    reqDatabaseRef.child(reqId).setValue(req)
                })
            }
        }


        // Check if the same Request has already been sent
        private fun checkExistingReq(reqId: String) {

            /* 1. Create the 'requests' child
        val database = FirebaseDatabase.getInstance()
        val reqRef = database.reference.child("requests")*/

            // 2. Search if a Request between Send & Receiver for the same book is already existing
            val reqRef = FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("requests")
                    .child(reqId);

            reqRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {
                        reqIdRetrieved = dataSnapshot.key;
                    } else {
                        reqIdRetrieved = null;
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            });

        }
    }
}

           // Log.d(TAG, req.toString())

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
       }
        */

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
