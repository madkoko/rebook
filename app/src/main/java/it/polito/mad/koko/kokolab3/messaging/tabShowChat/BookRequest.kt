package it.polito.mad.koko.kokolab3.messaging.tabShowChat

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import it.polito.mad.koko.kokolab3.R
import it.polito.mad.koko.kokolab3.profile.ProfileManager
import it.polito.mad.koko.kokolab3.request.Request
import it.polito.mad.koko.kokolab3.request.RequestManager


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

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle): View? {

        val rootBookReqView = inflater.inflate(R.layout.request_fragment, container, false)
        val listBookReqView = rootBookReqView.findViewById<ListView>(R.id.list_chat) // Carica parte grafica lista

        // UI elements
        reqListView = rootBookReqView.findViewById<ListView>(R.id.list_chat)

        return rootBookReqView
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val myListView = activity.findViewById(R.id.list_chat) as ListView

        val myReqClass = Request::class

        myId = FirebaseAuth.getInstance().currentUser!!.uid //ok not null

        val query = FirebaseDatabase.getInstance().reference.child("requests").orderByChild("receiverId").equalTo(myId)


        val options = FirebaseListOptions.Builder<Request>() //savedInstanceState null
                .setLayout(R.layout.request_fragment)
                .setQuery(query, Request::class.java)
                .build()

        Log.d(TAG, options.snapshots.toString())

        Log.d(TAG, "siamo tra options e populate")


        adapter = object : FirebaseListAdapter<Request>(options) { //ok not null

            override fun populateView(v: View?, model: Request?, position: Int) {

                val bookTitle = v!!.findViewById(R.id.req_book_title) as TextView
                val bookRequest = v.findViewById(R.id.book_request) as ImageView
                val ratingBar = v.findViewById(R.id.rating_bar_request) as RatingBar


                Log.d(TAG, "siamo in populate")

                bookTitle.text = model!!.bookName
                Picasso.get().load(model.bookImage).into(bookRequest)

                val acceptButton = v.findViewById<Button>(R.id.accept)
                val declineButton = v.findViewById<Button>(R.id.decline)

                ratingBar.setVisibility(View.INVISIBLE)

                if (model.status.equals("pending")) {
                    acceptButton.setVisibility(View.VISIBLE)
                    declineButton.setVisibility(View.VISIBLE)
                    declineButton.setOnClickListener {
                        RequestManager.declineRequest(getRef(position).key)
                    }
                    acceptButton.setOnClickListener {
                        RequestManager.acceptRequest(getRef(position).key)
                    }
                } else if (model.status.equals("returning")) {
                    acceptButton.setVisibility(View.VISIBLE)
                    declineButton.setVisibility(View.INVISIBLE)
                    acceptButton.setText(R.string.check_if_return)
                    acceptButton.setOnClickListener {
                        RequestManager.retunBook(getRef(position).key)
                    }
                } else if (model.status == "returned") {
                    acceptButton.setVisibility(View.VISIBLE)
                    declineButton.setVisibility(View.INVISIBLE)
                    acceptButton.setText(R.string.currency)
                    ratingBar.setVisibility(View.VISIBLE)
                    acceptButton.setOnClickListener {
                        ProfileManager.getInstance().addRating(model.senderId, ratingBar.rating.toString())
                        RequestManager.putSenderRate(getRef(position).key, ratingBar.rating.toInt().toString())
                        if (model.ratingReceiver != null && !model.ratingReceiver!!.isEmpty() && model.ratingReceiver!!.compareTo("") != 0)
                            RequestManager.ratedTransition(getRef(position).key)
                    }
                    //buttonReturn.setOnClickListener({ v2 -> ProfileManager.getInstance().addRating(model.receiverId, ratingBar.numStars) })
                }else {
                    acceptButton.setVisibility(View.INVISIBLE)
                    declineButton.setVisibility(View.INVISIBLE)
                }
                if (!model.ratingSender.equals("")){
                    ratingBar.setVisibility(View.INVISIBLE)
                }

            }
        }

        if (adapter != null) {
            myListView!!.adapter = adapter
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