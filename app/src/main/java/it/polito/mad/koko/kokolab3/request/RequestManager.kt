package it.polito.mad.koko.kokolab3.request

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener

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
        private val database = FirebaseDatabase.getInstance()

        fun createRequest(req: Request, data: ByteArray, reqId: String) {

            val storage = FirebaseStorage.getInstance()
            var downloadUrl: String;

            checkExistingReq(reqId, object : OnGetDataListener {
                override fun onStart() {
                }

                override fun onSuccess(dataSnapshot: DataSnapshot?) {
                    reqIdRetrieved = if (dataSnapshot!!.exists()) {
                        dataSnapshot!!.key;
                    } else {
                        null;
                    }

                    if (reqIdRetrieved == null) {
                        val reqDatabaseRef = database.reference.child("requests")
                        reqDatabaseRef.child(reqId).setValue(req);

                        /*val reqStorageRef = storage.reference.child("requests")
                        val uploadTask = reqStorageRef!!.child(reqId).putBytes(data)

                        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                            downloadUrl = taskSnapshot.downloadUrl!!.toString()
                            //Log.d(TAG,downloadUrl);
                            req.bookImage = downloadUrl
                            //ref.child(bookKey).child("image").setValue(downloadUrl);
                            reqDatabaseRef.child(reqId).setValue(req)
                        })*/
                    }
                }

                override fun onFailed(databaseError: DatabaseError?) {
                }
            })


        }


        // Check if the same Request has already been sent
        private fun checkExistingReq(reqId: String, listener: OnGetDataListener) {
            listener?.onStart()

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
                    listener?.onSuccess(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    listener?.onFailed(databaseError)
                }
            });
        }

        // *** ACCEPT a BOOK REQUEST ***
        //      >>> Change Request status
        public fun acceptRequest(reqId: String?) {
            val reqDatabaseRef = database.reference.child("requests")
                    .child(reqId)
                    .child("status")
                    .setValue("onBorrow")
        }

        // *** DECLINE a BOOK REQUEST ***
        //      >>> Delete the Request on Firebase
        //      >>> Delete the Request on Book Requests List in the Tab Menu
        public fun declineRequest(reqId: String?) {
            val reqDatabaseRef = database.reference.child("requests")
                    .child(reqId)
                    .removeValue()
        }

        public fun retunRequest(reqId: String?) {
            val reqDatabaseRef = database.reference.child("requests")
                    .child(reqId)
                    .child("status")
                    .setValue("returning")
        }

        fun retunBook(reqId: String?) {
            val reqDatabaseRef = database.reference.child("requests")
                    .child(reqId)
                    .child("status")
                    .setValue("returned")
        }


        fun ratedTransition(reqId: String?) {
            val reqDatabaseRef = database.reference.child("requests")
                    .child(reqId)
                    .child("status")
                    .setValue("rated")
        }

        fun putReceiverRate(reqId: String?, rate: String?){
            val reqDatabaseRef = database.reference.child("requests")
                    .child(reqId)
                    .child("ratingReceiver")
                    .setValue(rate)
        }
        fun putSenderRate(reqId: String?, rate: String?){
            val reqDatabaseRef = database.reference.child("requests")
                    .child(reqId)
                    .child("ratingSender")
                    .setValue(rate)
        }

    }


}