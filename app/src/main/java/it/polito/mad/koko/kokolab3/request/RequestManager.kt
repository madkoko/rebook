package it.polito.mad.koko.kokolab3.request

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.polito.mad.koko.kokolab3.firebase.DatabaseManager
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener
import it.polito.mad.koko.kokolab3.messaging.MessageManager

/**
 * Created by Franci on 23/05/18.
 */
class RequestManager() {

    // Create a new Request and push it on Firebase
    companion object {

        private val TAG = "RequestManager"

        private var retrievedRequestId: String? = null
        private val database = FirebaseDatabase.getInstance()


        fun createRequest(request: Request, data: ByteArray, requestId: String) {
            getRequest(requestId, object : OnGetDataListener {
                override fun onStart() {}

                override fun onSuccess(dataSnapshot: DataSnapshot?) {
                    Log.d(TAG, "dataSnapshot: " + dataSnapshot)
                    Log.d(TAG, "dataSnapshot!!.exists(): " + dataSnapshot!!.exists())
                    Log.d(TAG, "dataSnapshot.key: " + dataSnapshot.key)

                    retrievedRequestId = if (dataSnapshot!!.exists()) {
                        dataSnapshot!!.key
                    } else {
                        null
                    }

                    if (retrievedRequestId == null) {
                        val reqDatabaseRef = database.reference.child("requests")
                        reqDatabaseRef.child(requestId).setValue(request)

                        /*val reqStorageRef = storage.reference.child("requests")
                        val uploadTask = reqStorageRef!!.child(requestId).putBytes(data)

                        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                            downloadUrl = taskSnapshot.downloadUrl!!.toString()
                            //Log.d(TAG,downloadUrl);
                            request.bookImage = downloadUrl
                            //ref.child(bookKey).child("image").setValue(downloadUrl);
                            reqDatabaseRef.child(requestId).setValue(request)
                        })*/
                    }
                }

                override fun onFailed(databaseError: DatabaseError?) {}
            })


        }

        /**
         * It returns the specified request.
         */
        private fun getRequest(requestId: String, listener: OnGetDataListener) {
            listener?.onStart()

            /* 1. Create the 'requests' child
            val database = FirebaseDatabase.getInstance()
            val reqRef = database.reference.child("requests")*/

            // 2. Search if a Request between Send & Receiver for the same book is already existing
            DatabaseManager.get("requests", requestId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) { listener?.onSuccess(dataSnapshot) }

                override fun onCancelled(databaseError: DatabaseError) { listener?.onFailed(databaseError) }
            })
        }

        public fun acceptRequest(requestId: String?, context: Context, intent: Intent?) {
            // Sending a negative response notification
            if(intent != null)
                MessageManager.sendResponseNotification(intent, true)

            // Showing a book exchange declined message
            Toast.makeText(context, "Book exchange accepted!", Toast.LENGTH_LONG).show()

            // Creating the request object on Firebase
            DatabaseManager.set("onBorrow", "requests/$requestId/status")

            // Deleting the request notification
            if(intent != null)
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(intent?.extras!!.get("notificationID") as Int)
        }

        public fun declineRequest(requestId: String?, context: Context, intent: Intent?) {
            // Sending a negative response notification
            if(intent != null)
                MessageManager.sendResponseNotification(intent, false)

            // Showing a book exchange declined message
            Toast.makeText(context, "Book exchange declined!", Toast.LENGTH_LONG).show()

            // Deleting the request object on Firebase
            DatabaseManager.get("requests/$requestId").removeValue()

            // Deleting the request notification
            if(intent != null)
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(intent?.extras!!.get("notificationID") as Int)
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