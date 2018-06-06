package it.polito.mad.koko.kokolab3.messaging.tabShowChat;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener;
import it.polito.mad.koko.kokolab3.messaging.Message;
import it.polito.mad.koko.kokolab3.messaging.MessageManager;
import it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

/**
 * Created by Franci on 22/05/18.
 */

public class Conversation extends Fragment {

    private static final String TAG = "ConversationFragment";

    private FirebaseListAdapter<Message> adapter;     // All the messages of the selected chat
    private String chatID;
    private String senderId;
    private String senderUsername;
    private String senderImage;
    private String senderToken;
    private String receiverId;
    private String receiverUsername;
    private String receiverImage;
    private String receiverToken;
    private ListView chatListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView myListView = getActivity().findViewById(R.id.list_chat); // da lasciare?!++

        // ----Set autoscroll of listview when a new message arrives----//
        myListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        myListView.setStackFromBottom(true);

        // Parameters from ShowChat activity
        //senderId = getArguments().getString("senderId");
        senderId = ProfileManager.getCurrentUserID();

        //savedInstanceState = this.getArguments();
        //if (savedInstanceState != null) {
            /*senderUsername = getArguments().getString("senderUsername");
            senderImage = getArguments().getString("senderImage");
            senderToken = getArguments().getString("senderToken");
            receiverId = getArguments().getString("receiverId");
            receiverUsername = getArguments().getString("receiverUsername");
            receiverImage = getArguments().getString("receiverImage");
            receiverToken = getArguments().getString("receiverToken");*/
        chatID = getArguments().getString("chatID");
        //}

        Query query = FirebaseDatabase.getInstance().getReference().child("chats").child(chatID).child("messages");

        //FirebaseListOptions<Message> for retrieving data from firebase
        //query is reference
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.conversation_fragment)
                .setQuery(query, Message.class)
                .build();
        Log.d(TAG, String.valueOf(options.getSnapshots()));

        //FirebaseListAdapter for create ListAdapter Ui from firebaseUi
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View view, Message message, int position) {
                Log.d(TAG, String.valueOf(message));
                TextView messageTextSender = view.findViewById(R.id.message_text_sender);
                TextView messageTextReciver = view.findViewById(R.id.message_text_reciver);
                ImageView checkImage = view.findViewById(R.id.check_image);

                ViewSwitcher viewSwitcher = view.findViewById(R.id.switcher_conversation);
                LinearLayout linearLayout = view.findViewById(R.id.layout_sender);

                if (message.getText() == null || message.getText().compareTo("") == 0) {
                    linearLayout.setVisibility(View.INVISIBLE);
                    messageTextSender.setBackgroundResource(0);
                } else {

                    if (message.getSender().compareToIgnoreCase(senderId) == 0) {
                        linearLayout.setVisibility(View.VISIBLE);
                        if (viewSwitcher.getCurrentView() != view.findViewById(R.id.layout_sender)) {
                            viewSwitcher.showPrevious();
                        }
                        messageTextSender.setText(message.getText());
                        //messageText.setTextColor(getResources().getColor(R.color.secondary_text));
                        //messageText.setGravity(Gravity.RIGHT);

                        if (message.getCheck().compareTo("true") == 0)
                            checkImage.setVisibility(View.VISIBLE);
                        else
                            checkImage.setVisibility(View.INVISIBLE);

                    } else {
                        //messageText.setBackgroundResource(R.drawable.rounde_rectangle);
                        if (viewSwitcher.getCurrentView() != view.findViewById(R.id.layout_reciver)) {
                            viewSwitcher.showNext();
                        }
                        //messageTextReciver.setGravity(Gravity.LEFT);
                        messageTextReciver.setText(message.getText());
                        MessageManager.setFirebaseCheck(chatID, adapter.getRef(position).getKey());
                        checkImage.setVisibility(View.INVISIBLE);

                    }
                }
            }
        };
        myListView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFirebaseMessagingService.setActiveChat(chatID);
    }

    @Override
    public void onPause() {
        super.onPause();
        MyFirebaseMessagingService.clearActiveChat();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
