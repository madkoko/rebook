package it.polito.mad.koko.kokolab3.messaging.tabShowChat;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        // importa parametri dall'act: String strtext = getArguments().getString("edttext");

        View rootConversationView = inflater.inflate(R.layout.conversation_fragment, container, false); // !! da fare conversation_fragment
        ListView listConversationView = rootConversationView.findViewById(R.id.list_chat); // Carica parte grafica lista

        // UI elements
        chatListView = rootConversationView.findViewById(R.id.list_chat);

        return rootConversationView;
    }

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
                TextView messageText = view.findViewById(R.id.message_text);
                ImageView checkImage = view.findViewById(R.id.check_image);

                messageText.setText(message.getText());

                if (message.getSender().equalsIgnoreCase(senderId)) {
                    //messageText.setTextColor(getResources().getColor(R.color.secondary_text));
                    messageText.setGravity(Gravity.RIGHT);

                    MessageManager.isRead(chatID, adapter.getRef(position).getKey(), new OnGetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot data) {
                            if (data.exists()) {
                                Log.d(TAG, data.getValue().toString());
                                if (data.child("sender").getValue().toString().compareToIgnoreCase(ProfileManager.getCurrentUserID()) == 0) {

                                    if (data.child("check").getValue().toString().compareTo("true") == 0)
                                        checkImage.setVisibility(View.VISIBLE);

                                    else
                                        checkImage.setVisibility(View.INVISIBLE);

                                }
                            }
                        }

                        @Override
                        public void onFailed(DatabaseError databaseError) {

                        }
                    });

                } else {
                    //messageText.setBackgroundResource(R.drawable.rounde_rectangle);
                    messageText.setGravity(Gravity.LEFT);
                    MessageManager.setFirebaseCheck(chatID, adapter.getRef(position).getKey());
                    checkImage.setVisibility(View.INVISIBLE);

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
