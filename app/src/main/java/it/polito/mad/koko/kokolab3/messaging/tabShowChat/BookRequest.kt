package it.polito.mad.koko.kokolab3.messaging.tabShowChat

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import it.polito.mad.koko.kokolab3.R
import it.polito.mad.koko.kokolab3.request.Request
import kotlin.reflect.KClass

/**
 * Created by Franci on 22/05/18.
 */

class BookRequest() : Fragment() {

    private val TAG = "BookRequest"

    var adapter: FirebaseListAdapter<Request>? = null       // All requests sent to the user
    var requesterId: String? = null                         // Second party id
    var myId: String? = null                                // My id
    var reqListView: ListView? = null
    var reqId: String? = null
    var bookId: String? = null

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle): View? {

       val rootBookReqView = inflater.inflate(R.layout.request_fragment, container, false)
       val listBookReqView = rootBookReqView.findViewById<ListView>(R.id.list_chat) // Carica parte grafica lista

       // UI elements
       reqListView = rootBookReqView.findViewById<ListView>(R.id.list_chat)

       return rootBookReqView
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val myListView = activity.findViewById<ListView>(R.id.list_chat)

        val myReqClass = Request::class

        myId = FirebaseAuth.getInstance().currentUser!!.uid //ok not null

        val query = FirebaseDatabase.getInstance().reference.child("requests").orderByChild("receiverId").equalTo(myId)


        val options = FirebaseListOptions.Builder<Request>() //savedInstanceState null
                .setLayout(R.layout.request_fragment)
                .setQuery(query, Request::class.java)
                .build()

        Log.d(TAG, options.snapshots.toString())

        Log.d(TAG, "siamo tra options e populate")


        adapter =  object: FirebaseListAdapter<Request>(options) { //ok not null

            override fun populateView(v: View?, model: Request?, position: Int) {

                val bookTitle = view.findViewById<TextView>(R.id.req_book_title)
                val bookRequest = view.findViewById<ImageView>(R.id.book_request)

                Log.d(TAG, "siamo in populate")

                bookTitle.text = model!!.bookName
                Picasso.get().load(model.bookImage).into(bookRequest)

                val acceptButton = view.findViewById<Button>(R.id.accept)
                val declineButton = view.findViewById<Button>(R.id.decline)

                if(model.status.equals("pending")) {
                    acceptButton.setVisibility(View.VISIBLE)
                    declineButton.setVisibility(View.VISIBLE)
                }
                else{
                    acceptButton.setVisibility(View.INVISIBLE)
                    declineButton.setVisibility(View.INVISIBLE)
                }

            }
        }

        if(adapter != null) {
            myListView?.adapter = adapter
        }

    }

    override fun onResume() {   // non serve
        super.onResume()
    }

    override fun onPause() {    // non serve
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

}