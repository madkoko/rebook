package it.polito.mad.koko.kokolab3.messaging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;

public class ShowChats extends AppCompatActivity {

    private FirebaseListAdapter<UserChatInfo> adapter;

    /**
     * create all the views
     */
    private ImageView userThumbnail;
    private TextView chatDest;
    private TextView lastMessageView;

    private static final String TAG = "ShowChats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chats);

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("chats");

        ListView chatsListView = findViewById(R.id.chats_listview);


        //FirebaseListOptions<UserChatInfo> to retrieve user chat informations from firebase
        //query is reference
        FirebaseListOptions<UserChatInfo> options = new FirebaseListOptions.Builder<UserChatInfo>()
                .setLayout(R.layout.chats_adapter_layout)
                .setQuery(query, UserChatInfo.class)
                .build();

        //FirebaseListAdapter to create ListAdapter Ui from firebaseUi
        adapter = new FirebaseListAdapter<UserChatInfo>(options) {

            @Override
            protected void populateView(View view, UserChatInfo model, int position) {

                Log.d(TAG, adapter.getRef(position).getKey());

                String secondPartyUsername = model.getSecondPartyUsername();
                String secondPartyId = model.getSecondPartyId();
                String secondPartyImage = model.getSecondPartyImage();
                String lastMessage=model.getLastMessage();
                String chatID = adapter.getRef(position).getKey();

                userThumbnail = (ImageView) view.findViewById(R.id.user_thumbnail);
                chatDest = (TextView) view.findViewById(R.id.chat_dest);
                lastMessageView = (TextView) view.findViewById(R.id.last_message);

                chatDest.setText(secondPartyUsername);
                Picasso.get().load(secondPartyImage).fit().centerCrop().into(userThumbnail);

                lastMessageView.setText(lastMessage);

                view.setOnClickListener(v -> {
                    Intent showChat = new Intent(getApplicationContext(), ShowChat.class);
                    showChat.putExtra("chatId", chatID);
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

