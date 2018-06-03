package it.polito.mad.koko.kokolab3.messaging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class ShowChats extends AppCompatActivity {

    private FirebaseListAdapter<UserChatInfo> adapter;

    // Create all the views
    private ImageView userThumbnail;
    private TextView chatDest;
    private TextView lastMessageView;

    // Second party info
    private String secondPartyUsername;
    private String secondPartyId;
    private String secondPartyImage;
    private String lastMessage;
    private String chatID;

    private String currentUserID;

    private static final String TAG = "ShowChats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chats);

        setTitle(R.string.chats_activity_title);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("chats");
        ListView chatsListView = findViewById(R.id.chats_listview);

        // 1. Retrieve User chat informations from Firebase, Query query is the reference
        FirebaseListOptions<UserChatInfo> options = new FirebaseListOptions.Builder<UserChatInfo>()
                .setLayout(R.layout.chats_adapter_layout)
                .setQuery(query, UserChatInfo.class)
                .build();

        // 2. FirebaseListAdapter to create ListAdapter Ui from firebaseUi
        adapter = new FirebaseListAdapter<UserChatInfo>(options) {

            @Override
            protected void populateView(View view, UserChatInfo model, int position) {

                Log.d(TAG, adapter.getRef(position).getKey());

                // Second party info
                secondPartyUsername = model.getSecondPartyUsername();
                secondPartyId = model.getSecondPartyId();
                secondPartyImage = model.getSecondPartyImage();
                lastMessage = model.getLastMessage();

                // My info
                Profile senderProfile = ProfileManager.getProfile();
                String senderId = FirebaseAuth.getInstance().getUid();
                String senderUsername = senderProfile.getName();
                String senderImage = senderProfile.getImage();
                String senderToken = senderProfile.getTokenMessage();
                UserChatInfo senderInfo = new UserChatInfo(senderId, senderUsername, senderImage, lastMessage, senderToken);

                chatID = adapter.getRef(position).getKey();

                userThumbnail = (ImageView) view.findViewById(R.id.user_thumbnail);
                chatDest = (TextView) view.findViewById(R.id.chat_dest);
                lastMessageView = (TextView) view.findViewById(R.id.last_message);

                chatDest.setText(secondPartyUsername);
                Picasso.get().load(secondPartyImage).fit().centerCrop().into(userThumbnail);

                lastMessageView.setText(lastMessage);

                view.setOnClickListener(v -> {
                    Intent showChat = new Intent(getApplicationContext(), ShowChat.class);
                    showChat.putExtra("chatID", chatID);
                    showChat.putExtra("originClass", "showChats");
                    showChat.putExtra("receiverInfo", model);
                    showChat.putExtra("senderInfo", senderInfo);

                    startActivity(showChat);
                });

            }
        };

        chatsListView.setAdapter(adapter);

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

