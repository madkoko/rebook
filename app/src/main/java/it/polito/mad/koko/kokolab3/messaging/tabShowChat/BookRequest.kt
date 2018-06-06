package it.polito.mad.koko.kokolab3.messaging.tabShowChat

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import it.polito.mad.koko.kokolab3.R
import it.polito.mad.koko.kokolab3.firebase.DatabaseManager
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener
import it.polito.mad.koko.kokolab3.messaging.UserChatInfo
import it.polito.mad.koko.kokolab3.profile.ProfileManager
import it.polito.mad.koko.kokolab3.request.Request
import it.polito.mad.koko.kokolab3.request.RequestManager
import it.polito.mad.koko.kokolab3.profile.ShowProfile

import org.w3c.dom.Text
import java.util.*
import android.widget.FrameLayout


@SuppressLint("ValidFragment")
class BookRequest(flag: Int, receiverInfo: UserChatInfo?) : Fragment() {

    companion object {
        private val TAG = "BookRequest"


    }


    var adapter: FirebaseListAdapter<Request>? = null       // All requests sent to the user
    //var requesterId: String? = null                         // Second party id
    var myId: String? = null                                // My id
    var bookId: String? = null

    val flag: Int = flag
    val requester: UserChatInfo? = receiverInfo

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle): View? {

        val rootBookReqView = inflater.inflate(R.layout.request_fragment, container, false)
        val listBookReqView = rootBookReqView.findViewById<ListView>(R.id.list_chat) // Carica parte grafica lista

        // UI elements
        reqListView = rootBookReqView.findViewById<ListView>(R.id.list_chat)

        return rootBookReqView
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myListView: ListView
        val myReqClass = Request::class

        myId = ProfileManager.getCurrentUserID()

        val query = DatabaseManager.get("requests").orderByChild("receiverId").equalTo(myId)

        // All requests
        if (flag == 0) {
            myListView = activity.findViewById<ListView>(R.id.list_home_chats) as ListView

            val options = FirebaseListOptions.Builder<Request>() //savedInstanceState null
                    .setLayout(R.layout.request_fragment)
                    .setQuery(query, Request::class.java)
                    .build()

            Log.d(TAG, options.snapshots.toString())

            Log.d(TAG, "siamo tra options e populate")

            adapter = object : FirebaseListAdapter<Request>(options) { //ok not null

                override fun populateView(v: View?, model: Request?, position: Int) {

                    val bookTitle = v!!.findViewById<TextView>(R.id.req_book_title) as TextView
                    val bookRequest = v.findViewById<ImageView>(R.id.book_request) as ImageView
                    val ratingBar = v.findViewById<RatingBar>(R.id.rating_bar_request) as RatingBar
                    val feedbackEditText = v.findViewById<EditText>(R.id.feedback_edit_text) as EditText


                    Log.d(TAG, "siamo in populate")

                    bookTitle.text = model!!.bookName
                    Picasso.get().load(model.bookImage).into(bookRequest)

                    val acceptButton = v.findViewById<Button>(R.id.accept)
                    val declineButton = v.findViewById<Button>(R.id.decline)

                    val senderName = v.findViewById<TextView>(R.id.req_requester)
                    if (model.senderId.equals(ProfileManager.getCurrentUserID()))
                        senderName.setText("Request by me")
                    else {
                        senderName.setText("Request by " + model.senderName)
                        senderName.setOnClickListener(View.OnClickListener {
                            val showProfile = Intent(activity, ShowProfile::class.java)
                            showProfile.putExtra("UserID", model.senderId)
                            ProfileManager.retrieveInformationUser(model.senderId)
                            startActivity(showProfile)
                        })
                    }


                    val linearLayoutRated = v.findViewById<LinearLayout>(R.id.linear_layout_rated)

                    val ratedSwitch = v.findViewById<ViewSwitcher>(R.id.rated_switch)


                    if (model.status.equals("pending")) {
                        acceptButton.visibility = View.VISIBLE
                        declineButton.visibility = View.VISIBLE
                        declineButton.setOnClickListener {
                            RequestManager.declineRequest(
                                    getRef(position).key,
                                    activity.applicationContext,
                                    null
                            )
                        }
                        acceptButton.setOnClickListener {
                            RequestManager.acceptRequest(
                                    getRef(position).key,
                                    activity.applicationContext,
                                    null,
                                    model.bookId
                            )
                        }
                    } else if (model.status.equals("returning")) {
                        acceptButton.visibility = View.VISIBLE
                        declineButton.visibility = View.INVISIBLE
                        acceptButton.setText(R.string.check_if_return)
                        acceptButton.setOnClickListener {
                            RequestManager.returnBook(getRef(position).key)
                            FirebaseDatabase.getInstance().reference.child("books").child(model.bookId).child("sharable").setValue("yes")
                        }
                    } else if (model.status == "returned") {
                        if (ratedSwitch.currentView != v.findViewById(R.id.linear_layout_rated))
                            ratedSwitch.showNext();
                        //
                        //linearLayoutRated.setLayoutParams(LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                        //
                        linearLayoutRated.setLayoutParams(FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                        //
                        acceptButton.visibility = View.VISIBLE
                        declineButton.visibility = View.INVISIBLE
                        acceptButton.setText(R.string.currency)
                        acceptButton.setOnClickListener {
                            ProfileManager.addRating(model.senderId, ratingBar.rating.toString(), feedbackEditText.text.toString())
                            RequestManager.putSenderRate(getRef(position).key, ratingBar.rating.toInt().toString())
                            //
                            linearLayoutRated.setLayoutParams(FrameLayout.LayoutParams(0, 0))
                            //
                            if (ratedSwitch.currentView != v.findViewById(R.id.layout_before))
                                ratedSwitch.showPrevious();
                            val ratedText = v.findViewById<TextView>(R.id.rated_text)
                            ratedText.setText(R.string.rated_added)
                            if (model.ratingReceiver != null && !model.ratingReceiver!!.isEmpty() && model.ratingReceiver!!.compareTo("") != 0) {
                                RequestManager.ratedTransition(getRef(position).key)

                            }
                        }
                        //buttonReturn.setOnClickListener({ v2 -> ProfileManager.getInstance().addRating(model.receiverId, ratingBar.numStars) })
                    } else {
                        acceptButton.visibility = View.INVISIBLE
                        declineButton.visibility = View.INVISIBLE
                    }
                    if (!model.ratingSender.equals("")) {
                        ratingBar.visibility = View.INVISIBLE
                        acceptButton.visibility = View.INVISIBLE
                        feedbackEditText.visibility = View.INVISIBLE
                    }

                }


                override fun onDataChanged() {
                    if (adapter!!.count == 0) {
                        myListView.emptyView = activity.findViewById<View>(R.id.no_requests_found)
                        Toast.makeText(activity.applicationContext, R.string.no_requests_found, Toast.LENGTH_SHORT).show()
                    }
                }

            }

            if (adapter != null) {
                myListView.adapter = adapter
            }
            // Requests with the user with whom the chat is opened
        } else {
            myListView = activity.findViewById<ListView>(R.id.list_chat) as ListView
            myListView.isStackFromBottom = false

            /*  Listener in charge of populating the ListView containing all chat requests
                with the user with whom the chat is opened */
            val thisChatRequestsListener = object : OnGetDataListener {
                override fun onSuccess(dataSnapshot: DataSnapshot) {
                    val allCurrentUserRequests = HashMap<String, Request>()
                    if (dataSnapshot.exists()) {
                        // Retrieve all the books from Firebase
                        for (bookSnapshot in dataSnapshot.children) {
                            val request = bookSnapshot.getValue(Request::class.java)
                            allCurrentUserRequests.put(bookSnapshot.key, request!!)
                        }
                    }
                    val thisChatRequests = HashMap<String, Request>()
                    // For each current user's request
                    for ((key, value) in allCurrentUserRequests)
                    // If the request involves the user with whom the chat is opened
                        if (requester!!.secondPartyId.compareTo(allCurrentUserRequests.get(key)!!.senderId!!) === 0)
                        // Add it to the current chat requests map
                            thisChatRequests.put(key, value)

                    myListView.adapter = object : BaseAdapter() {

                        override fun getCount(): Int {
                            return thisChatRequests.size
                        }

                        override fun getItem(i: Int): Any {
                            return thisChatRequests.keys.toTypedArray()[i]
                        }

                        override fun getItemId(i: Int): Long {
                            return 0
                        }

                        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
                            var v = view
                            if (v == null)
                                v = activity.layoutInflater.inflate(R.layout.request_fragment, viewGroup, false)

                            val bookTitle = v!!.findViewById<TextView>(R.id.req_book_title) as TextView
                            val bookRequest = v.findViewById<ImageView>(R.id.book_request) as ImageView
                            val ratingBar = v.findViewById<RatingBar>(R.id.rating_bar_request) as RatingBar
                            val feedbackEditText = v.findViewById<EditText>(R.id.feedback_edit_text) as EditText

                            val requestId = thisChatRequests.keys.toTypedArray()[i]

                            Log.d(TAG, "siamo in populate")
                            val model = thisChatRequests.get(requestId)
                            bookTitle.text = model!!.bookName
                            Picasso.get().load(model.bookImage).into(bookRequest)

                            val senderName = v.findViewById<TextView>(R.id.req_requester)
                            if (model.senderId.equals(ProfileManager.getCurrentUserID()))
                                senderName.setText(R.string.request_by_me)
                            else {
                                senderName.setText("Request by " + model.senderName)
                                senderName.setOnClickListener(View.OnClickListener {
                                    val showProfile = Intent(activity, ShowProfile::class.java)
                                    showProfile.putExtra("UserID", model.senderId)
                                    ProfileManager.retrieveInformationUser(model.senderId)
                                    startActivity(showProfile)
                                })
                            }

                            val acceptButton = v.findViewById<Button>(R.id.accept)
                            val declineButton = v.findViewById<Button>(R.id.decline)

                            val linearLayoutRated = v.findViewById<LinearLayout>(R.id.linear_layout_rated)

                            val ratedSwitch = v.findViewById<ViewSwitcher>(R.id.rated_switch)


                            if (model.status.equals("pending")) {
                                acceptButton.visibility = View.VISIBLE
                                declineButton.visibility = View.VISIBLE
                                declineButton.setOnClickListener {
                                    RequestManager.declineRequest(
                                            requestId,
                                            activity.applicationContext,
                                            null
                                    )
                                }
                                acceptButton.setOnClickListener {
                                    RequestManager.acceptRequest(
                                            requestId,
                                            activity.applicationContext,
                                            null,
                                            model.bookId
                                    )
                                }
                            } else if (model.status.equals("returning")) {
                                acceptButton.visibility = View.VISIBLE
                                declineButton.visibility = View.INVISIBLE
                                acceptButton.setText(R.string.check_if_return)
                                acceptButton.setOnClickListener {
                                    RequestManager.returnBook(requestId)
                                    FirebaseDatabase.getInstance().reference.child("books").child(model.bookId).child("sharable").setValue("yes")
                                }
                            } else if (model.status == "returned") {
                                ratedSwitch.showNext();
                                //
                                linearLayoutRated.setLayoutParams(FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                                //
                                acceptButton.visibility = View.VISIBLE
                                declineButton.visibility = View.INVISIBLE
                                acceptButton.setText(R.string.currency)
                                acceptButton.setOnClickListener {
                                    ProfileManager.addRating(model.senderId, ratingBar.rating.toString(), feedbackEditText.text.toString())
                                    RequestManager.putSenderRate(requestId, ratingBar.rating.toInt().toString())
                                    //
                                    linearLayoutRated.setLayoutParams(FrameLayout.LayoutParams(0, 0))
                                    //
                                    ratedSwitch.showPrevious();
                                    val ratedText = v.findViewById<TextView>(R.id.rated_text)
                                    ratedText.setText(R.string.rated_added)
                                    if (model.ratingReceiver != null && !model.ratingReceiver!!.isEmpty() && model.ratingReceiver!!.compareTo("") != 0) {
                                        RequestManager.ratedTransition(requestId)
                                    }
                                }
                                //buttonReturn.setOnClickListener({ v2 -> ProfileManager.getInstance().addRating(model.receiverId, ratingBar.numStars) })
                            } else {
                                acceptButton.visibility = View.INVISIBLE
                                declineButton.visibility = View.INVISIBLE
                            }
                            if (!model.ratingSender.equals("")) {
                                ratingBar.visibility = View.INVISIBLE
                                acceptButton.visibility = View.INVISIBLE
                                feedbackEditText.visibility = View.INVISIBLE
                            }

                            return v
                        }
                    }
                }

                override fun onFailed(databaseError: DatabaseError) {}
            }

            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError?) {
                    thisChatRequestsListener.onFailed(databaseError!!)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    thisChatRequestsListener.onSuccess(dataSnapshot)
                }
            })
        }
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