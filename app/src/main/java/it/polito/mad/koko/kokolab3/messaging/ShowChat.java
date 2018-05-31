package it.polito.mad.koko.kokolab3.messaging;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.messaging.tabShowChat.BookRequestList;
import it.polito.mad.koko.kokolab3.messaging.tabShowChat.Conversation;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.util.AlertManager;

public class ShowChat extends AppCompatActivity {

    private static final String TAG = "ShowChatActivity";
    private FirebaseListAdapter<Message> adapter; // All the messages of the selected chat
    private String chatID; // chat ID
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
    private String receiverId;
    private String receiverUsername;
    private String receiverImage;
    private String receiverToken;

    private Button send;
    private EditText messageEditor;
    private LinearLayout sendMsgLayout;
    private Fragment conversation;
    private Fragment bookRequestList;

    private Intent i;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_show_chat);

        this.savedInstanceState = bundle;
        i = getIntent();

        // 1. Check the origin class from whom user is coming

        // >>> CASE A) User is coming from "ShowBook" [click on "Send Message" button from Book view]
        if(i.getStringExtra("originClass").equals("showBook")) {

            // A1. Retrieve Chat ID
            chatID = MessageManager.getChatID();

            // A2. Remove Event Listener
            if (chatID != null) {
                MessageManager.removeChatRefListener();
            }

            // A3. Retrieve *Sender* informations
            senderId = MessageManager.getSenderId();
            senderUsername = MessageManager.getSenderUsername();
            senderImage = MessageManager.getSenderImage();
            senderToken = MessageManager.getSenderToken();

            // A4. Retrieve *Receiver* informations
            receiverId = MessageManager.getReceiverId();
            receiverUsername = MessageManager.getReceiverUsername();
            receiverImage = MessageManager.getReceiverImage();
            receiverToken = MessageManager.getReceiverToken();

        }

        // >>> CASE B) User is coming from "ShowChat" or "ShowChats" [Click in "Chat" tab or "Chats" menù]
        else if (i.getStringExtra("originClass").equals("homeChatList")){

            // B1. Retrieve Chat ID
            chatID = (String) i.getExtras().get("chatID");

            // B2. Retrieve *Sender* informations
            senderProfile = ProfileManager.getInstance().getCurrentUser();
            senderId = FirebaseAuth.getInstance().getUid();
            senderUsername = senderProfile.getName();
            senderImage = senderProfile.getImage();
            senderToken = senderProfile.getTokenMessage();

            // B3. Retrieve *Receiver* informations
            UserChatInfo secondParty = (UserChatInfo) i.getExtras().get("receiverInfo"); // SWAP >>> correttp!?
            receiverId = secondParty.getSecondPartyId();
            receiverUsername = secondParty.getSecondPartyUsername();
            receiverImage = secondParty.getSecondPartyImage();
            receiverToken = secondParty.getSecondPartyToken();
        }

        // >>> CASE C) Default case: user is coming from anywhere else (Tapping the notification)
        else{
            // B1. Retrieve Chat ID
            chatID = (String) i.getExtras().get("chatID");

            // B2. Retrieve *Sender* informations
            senderProfile = ProfileManager.getInstance().getCurrentUser();
            senderId = FirebaseAuth.getInstance().getUid();
            senderUsername = senderProfile.getName();
            senderImage = senderProfile.getImage();
            senderToken = senderProfile.getTokenMessage();

            // B3. Retrieve *Receiver* informations
            UserChatInfo secondParty = (UserChatInfo) i.getExtras().get("senderInfo"); // SWAP >>> correttp!?
            receiverId = secondParty.getSecondPartyId();
            receiverUsername = secondParty.getSecondPartyUsername();
            receiverImage = secondParty.getSecondPartyImage();
            receiverToken = secondParty.getSecondPartyToken();
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


        // 5 UI for send message

        messageEditor = findViewById(R.id.send_message);
        send = findViewById(R.id.send);

        // 5.1 Send message
        send.setOnClickListener((View v) -> {
            if (!messageEditor.getText().toString().isEmpty()) { // Ho tolto il controllo su null perché non era mai possibile fosse vero
                String messageText = messageEditor.getText().toString();                        // >>> Get the msg's Text
                MessageManager.createMessage(chatID, senderId, receiverId, messageText);   // >>> Create a new message entry in Firebase
                MessageManager.sendMessageNotification(                                         // >>> Send the corresponding notification
                        senderId,
                        senderUsername,
                        senderImage,
                        senderToken,
                        receiverId,
                        receiverUsername,
                        receiverImage,
                        receiverToken,
                        null, // perchè?
                        chatID,
                        messageText
                );
                messageEditor.setText("");                                                      // >>> Clear the text editor
            }
        });

        // 5. Final UI implementation
        conversation = new Conversation();
        bookRequestList = new BookRequestList();

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
        bun.putString("chatID", chatID);

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
                sendMsgLayout = findViewById(R.id.send_msg_layout);
                sendMsgLayout.setVisibility(View.INVISIBLE);

                getFragmentManager().beginTransaction().add(android.R.id.content, bookRequestList).commit();

                break;

            default: // ** no need **
                break;
        }

    }

    private void closeFragment(int position) {
        switch (position) {

            case 0: // *** CHAT TAB ***
                getFragmentManager().beginTransaction().remove(conversation).commit();
                sendMsgLayout = findViewById(R.id.send_msg_layout);
                sendMsgLayout.setVisibility(View.GONE);
                break;

            case 1: // *** BOOK REQ TAB ***
                //getFragmentManager().beginTransaction().remove(conversation).commit();
                break;

            default: // ** no need **
                break;
        }

    }
}