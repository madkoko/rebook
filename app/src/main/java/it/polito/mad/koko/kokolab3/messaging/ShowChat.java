package it.polito.mad.koko.kokolab3.messaging;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.firebase.DatabaseManager;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.util.AlertManager;

public class ShowChat extends AppCompatActivity {

    private static final String TAG = "ShowChatActivity";

    /**
     * All the messages of the selected chat
     */
    private FirebaseListAdapter<Message> adapter;

    /**
     * This chat's ID
     */
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chat);

        // Retrieving the chat id
        chatId = getIntent().getStringExtra("chatId");

        // Retrieving the sender information
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Profile senderProfile = ProfileManager.getInstance().getProfile(senderId);
        String senderUsername = senderProfile.getName();
        String senderImage = senderProfile.getImgUrl();
        String senderToken = senderProfile.getTokenMessage();

        // Retrieving the receiver information
        String receiverId = null, receiverUsername = null, receiverImage = null, receiverToken = null;
        Map<String, Map<String, String>> userChatIDs = MessageManager.getUserChatIDs();
        for (String chatIdIterator : userChatIDs.keySet()) { // Cycling across all user's chats
            // When the right chat is reached by the iterator
            if (chatId.compareTo(chatIdIterator) == 0) {
                // Retrieving the receiver map
                Map<String, String> receiver = userChatIDs.get(chatIdIterator);

                // Retrieving the receiver information
                receiverId = receiver.get("secondPartyId");
                receiverUsername = receiver.get("secondPartyUsername");
                receiverImage = receiver.get("secondPartyImage");
                receiverToken = receiver.get("secondPartyToken");

                break;
            }
        }

        //Set title.
        setTitle(receiverUsername);

        // If still no receiver has been found
        if (receiverId == null) {
            // Show an error dialog
            AlertManager.noUserDialog(this);

            return;
        }

        // Receiver info must be final in order to be used in a lambda function
        String finalReceiverId = receiverId;
        String finalReceiverUsername = receiverUsername;
        String finalReceiverImage = receiverImage;
        String finalReceiverToken = receiverToken;

        Query query = FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).child("messages");

        // UI elements
        ListView chatListView = findViewById(R.id.chat_listview);
        EditText messageEditor = findViewById(R.id.send_message);
        Button send = findViewById(R.id.send);

        send.setOnClickListener((View v) -> {
            if (messageEditor.getText().toString() != null && messageEditor.getText().toString().compareTo("") != 0) {
                // Retrieving the message text
                String messageText = messageEditor.getText().toString();

                // Creating a new message entry in Firebase
                MessageManager.createMessage(chatId, senderId, finalReceiverId, messageText);

                // Sending the corresponding notification
                MessageManager.sendMessageNotification(// Sender info
                        senderId,                       // sender ID
                        senderUsername,        // sender username
                        senderImage,      // sender image
                        senderToken,// sender token

                        // Receiver info
                        finalReceiverId,                     // receiver ID
                        finalReceiverUsername,      // receiver username
                        finalReceiverImage,    // receiver image
                        finalReceiverToken,                  // receiver token

                        // Book info
                        null,                 // book title

                        // Chat info
                        chatId,

                        messageText
                );

                // Clearing the text editor
                messageEditor.setText("");
            }
        });

        //FirebaseListOptions<Message> for retrieving data from firebase
        //query is reference
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.adapter_show_chat)
                .setQuery(query, Message.class)
                .build();
        Log.d(TAG, String.valueOf(options.getSnapshots()));

        //FirebaseListAdapter for create ListAdapter Ui from firebaseUi
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View view, Message model, int position) {
                Log.d(TAG, String.valueOf(model));
                TextView messageText = view.findViewById(R.id.message_text);
                ImageView checkImage = view.findViewById(R.id.check_image);

                messageText.setText(model.getText());

                if (model.getSender().equalsIgnoreCase(senderId)) {
                    //messageText.setTextColor(getResources().getColor(R.color.secondary_text));
                    messageText.setGravity(Gravity.RIGHT);

                    if (model.getCheck().compareTo("true")==0)
                        checkImage.setVisibility(View.VISIBLE);
                } else {
                    //messageText.setBackgroundResource(R.drawable.rounde_rectangle);
                    messageText.setGravity(Gravity.LEFT);
                    MessageManager.setFirebaseCheck(chatId, adapter.getRef(position).getKey());

                }
            }
        };
        chatListView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();


        MyFirebaseMessagingService.setActiveChat(chatId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyFirebaseMessagingService.clearActiveChat();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

