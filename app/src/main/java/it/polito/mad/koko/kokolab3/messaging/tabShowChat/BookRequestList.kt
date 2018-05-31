package it.polito.mad.koko.kokolab3.messaging.tabShowChat

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.polito.mad.koko.kokolab3.R
import it.polito.mad.koko.kokolab3.messaging.Message
import it.polito.mad.koko.kokolab3.messaging.MessageManager
import it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService

/**
 * Created by Franci on 22/05/18.
 */

class BookRequestList() : Fragment() {

    private val TAG = "BookRequestList"

    /*

    private var adapter: FirebaseListAdapter<Message>? = null     // All the messages of the selected chat
    private var chatID: String? = null
    private var senderId: String? = null
    private val senderUsername: String? = null
    private val senderImage: String? = null
    private val senderToken: String? = null
    private val receiverId: String? = null
    private val receiverUsername: String? = null
    private val receiverImage: String? = null
    private val receiverToken: String? = null
    private var chatListView: ListView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle): View? {
        // importa parametri dall'act: String strtext = getArguments().getString("edttext");

        val rootConversationView = inflater.inflate(R.layout.conversation_fragment, container, false) // !! da fare conversation_fragment
        val listConversationView = rootConversationView.findViewById<ListView>(R.id.list_chat) // Carica parte grafica lista

        // UI elements
        chatListView = rootConversationView.findViewById(R.id.list_chat)

        return rootConversationView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myListView = activity.findViewById<ListView>(R.id.list_chat) // da lasciare?!

        // Parameters from ShowChat activity
        //senderId = getArguments().getString("senderId");
        senderId = FirebaseAuth.getInstance().currentUser!!.uid

        //savedInstanceState = this.getArguments();
        //if (savedInstanceState != null) {
        /*senderUsername = getArguments().getString("senderUsername");
            senderImage = getArguments().getString("senderImage");
            senderToken = getArguments().getString("senderToken");
            receiverId = getArguments().getString("receiverId");
            receiverUsername = getArguments().getString("receiverUsername");
            receiverImage = getArguments().getString("receiverImage");
            receiverToken = getArguments().getString("receiverToken");*/
        chatID = arguments.getString("chatID")
        //}

        val query = FirebaseDatabase.getInstance().reference.child("chats").child(chatID!!).child("messages")

        //FirebaseListOptions<Message> for retrieving data from firebase
        //query is reference
        val options = FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.conversation_fragment)
                .setQuery(query, Message::class.java)
                .build()
        Log.d(TAG, options.snapshots.toString())

        //FirebaseListAdapter for create ListAdapter Ui from firebaseUi
        adapter = object : FirebaseListAdapter<Message>(options) {
            override fun populateView(view: View, model: Message, position: Int) {
                Log.d(FirebaseListAdapter.TAG, model.toString())
                val messageText = view.findViewById<TextView>(R.id.message_text)
                val checkImage = view.findViewById<ImageView>(R.id.check_image)

                messageText.text = model.text

                if (model.sender.equals(senderId!!, ignoreCase = true)) {
                    //messageText.setTextColor(getResources().getColor(R.color.secondary_text));
                    messageText.gravity = Gravity.RIGHT

                    if (model.check.compareTo("true") == 0)
                        checkImage.visibility = View.VISIBLE
                } else {
                    //messageText.setBackgroundResource(R.drawable.rounde_rectangle);
                    messageText.gravity = Gravity.LEFT
                    MessageManager.setFirebaseCheck(chatID, adapter!!.getRef(position).key)

                }
            }
        }
        myListView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        MyFirebaseMessagingService.setActiveChat(chatID)
    }

    override fun onPause() {
        super.onPause()
        MyFirebaseMessagingService.clearActiveChat()
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    */
}