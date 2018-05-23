package it.polito.mad.koko.kokolab3.messaging;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.messaging.tabShowChat.Conversation;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.tabsHomeActivity.topListBook;
import it.polito.mad.koko.kokolab3.util.AlertManager;

public class ShowChat extends AppCompatActivity {

    private static final String TAG = "ShowChatActivity";
    private FirebaseListAdapter<Message> adapter; // All the messages of the selected chat
    private String chatId; // chat ID
    private Bundle savedInstanceState;
    private String senderId;
    private Profile senderProfile;
    private String senderUsername;
    private String senderImage;
    private String senderToken;
    private String finalReceiverId;
    private String finalReceiverUsername;
    private String finalReceiverImage;
    private String finalReceiverToken;
    private Button send;
    private EditText messageEditor;
    private LinearLayout sendMsgLayout;
    private Fragment conversation;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_show_chat);

        this.savedInstanceState = bundle;

        // 1. Retrieve Chat ID
        chatId = getIntent().getStringExtra("chatId");

        // 2. Retrieve *sender* information
        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        senderProfile = ProfileManager.getInstance().getProfile(senderId);
        senderUsername = senderProfile.getName();
        senderImage = senderProfile.getImage();
        senderToken = senderProfile.getTokenMessage();

        // 3. Retrieve *receiver* information
        String receiverId = null, receiverUsername = null, receiverImage = null, receiverToken = null;
        Map<String, Map<String, String>> userChatIDs = MessageManager.getUserChatIDs();
        for (String chatIdIterator : userChatIDs.keySet()) {                       // >>> Cycling across all user's chats
            if (chatId.compareTo(chatIdIterator) == 0) {                           // >>> The right chat is reached by the iterator
                Map<String, String> receiver = userChatIDs.get(chatIdIterator);    // >>> Retrieve Receiver's map
                receiverId = receiver.get("secondPartyId");                        // >>> Retrieve Receiver's informations
                receiverUsername = receiver.get("secondPartyUsername");
                receiverImage = receiver.get("secondPartyImage");
                receiverToken = receiver.get("secondPartyToken");
                break;
            }
        }
        if (receiverId == null) {                                                   // !! No Receiver found? !!
            AlertManager.noUserDialog(this);                                // >>> Show an error dialog and return
            return;
        }
        finalReceiverId = receiverId;                                               // !! Receiver infos must be final in order to be used in a lambda function !!
        finalReceiverUsername = receiverUsername;
        finalReceiverImage = receiverImage;
        finalReceiverToken = receiverToken;

        // 4. Set Title (-> the chat title is the receiver Username)
        setTitle(receiverUsername);

        // 5. Send message
        send = findViewById(R.id.send);
        send.setOnClickListener((View v) -> {
            if (messageEditor.getText().toString() != null && messageEditor.getText().toString().compareTo("") != 0) {
                String messageText = messageEditor.getText().toString();                        // >>> Get the msg's Text
                MessageManager.createMessage(chatId, senderId, finalReceiverId, messageText);   // >>> Create a new message entry in Firebase
                MessageManager.sendMessageNotification(                                         // >>> Send the corresponding notification
                        senderId,
                        senderUsername,
                        senderImage,
                        senderToken,
                        finalReceiverId,
                        finalReceiverUsername,
                        finalReceiverImage,
                        finalReceiverToken,
                        null,
                        chatId,
                        messageText
                );
                messageEditor.setText("");                                                      // >>> Clear the text editor
            }
        });

        // 5. Final UI implementation
        conversation = new Conversation();

        // Parameters to be sent to ConversationFragment
        Bundle bun = new Bundle();
        /*
        savedInstanceState.putString("senderId", senderId);// this parametr is null
        savedInstanceState.putString("senderUsername", senderUsername);
        savedInstanceState.putString("senderImage", senderImage);
        savedInstanceState.putString("senderToken", senderToken);
        savedInstanceState.putString("receiverId", finalReceiverId);
        savedInstanceState.putString("receiverUsername", finalReceiverUsername);
        savedInstanceState.putString("receiverImage", finalReceiverImage);
        savedInstanceState.putString("receiverToken", finalReceiverToken);
        */
        bun.putString("chatId", chatId);

        // set Fragmentclass Arguments
        conversation.setArguments(bun);

        TabLayout tab_layout = findViewById(R.id.tabs_chat);
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.addTab(tab_layout.newTab().setText("Conversation"));
        tab_layout.addTab(tab_layout.newTab().setText("Book Requests"));

        selectFragment(0);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                closeFragment(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // nothing to do ?!?!??!?!?!?! Speriamo cazzo
            }

        });
    }


    // 6. Tab Menù
    private void selectFragment(int position) {
        switch (position) {

            case 0: // *** CHAT TAB ***

                // Set layout visibility: show send_msg_layout
                sendMsgLayout = findViewById(R.id.send_msg_layout);
                sendMsgLayout.setVisibility(View.VISIBLE);

                getFragmentManager().beginTransaction().add(android.R.id.content, conversation).commit();

                break;

            case 1: // *** BOOK REQ TAB ***

                // Set layout visibility: hide send_msg_layout
                //sendMsgLayout = findViewById(R.id.send_msg_layout);
                //sendMsgLayout.setVisibility(View.INVISIBLE);

                break;

            default: // ** no need **
                break;
        }

    }

    private void closeFragment(int position) {
        switch (position) {

            case 0: // *** CHAT TAB ***
                getFragmentManager().beginTransaction().remove(conversation).commit();
                break;

            case 1: // *** BOOK REQ TAB ***
                //getFragmentManager().beginTransaction().remove(conversation).commit();
                break;

            default: // ** no need **
                break;
        }

    }
}