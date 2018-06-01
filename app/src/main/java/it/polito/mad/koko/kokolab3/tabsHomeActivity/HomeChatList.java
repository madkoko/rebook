package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import it.polito.mad.koko.kokolab3.messaging.ShowChat;
import it.polito.mad.koko.kokolab3.messaging.UserChatInfo;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.ui.CircleTransform;

public class HomeChatList extends Fragment{
    private static final String TAG = "HomeChatListFragment";
    private FirebaseListAdapter<UserChatInfo> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chats_adapter_layout, container, false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String currentUserID = ProfileManager.getCurrentUserID();

        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("chats");

        ListView chatsListView = getActivity().findViewById(R.id.list_home_chats);


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

                // Receiver info
                String secondPartyUsername = model.getSecondPartyUsername();
                String secondPartyId = model.getSecondPartyId();
                String secondPartyImage = model.getSecondPartyImage();
                String lastMessage=model.getLastMessage();
                String chatID = adapter.getRef(position).getKey();

                // My info
                Profile senderProfile = ProfileManager.getProfile();
                String senderId = ProfileManager.getCurrentUserID();
                String senderUsername = senderProfile.getName();
                String senderImage = senderProfile.getImage();
                String senderToken = senderProfile.getTokenMessage();
                UserChatInfo senderInfo = new UserChatInfo(senderId, senderUsername, senderImage, lastMessage, senderToken);

                ImageView userThumbnail = (ImageView) view.findViewById(R.id.user_thumbnail);
                TextView chatDest = (TextView) view.findViewById(R.id.chat_dest);
                TextView lastMessageView = (TextView) view.findViewById(R.id.last_message);

                chatDest.setText(secondPartyUsername);
                Picasso.get().load(secondPartyImage).transform(new CircleTransform()).into(userThumbnail);

                lastMessageView.setText(lastMessage);

                view.setOnClickListener(v -> {
                    Intent showChat = new Intent(getActivity(), ShowChat.class);
                    showChat.putExtra("chatID", chatID);
                    showChat.putExtra("originClass", "homeChatList");
                    showChat.putExtra("receiverInfo", model);
                    showChat.putExtra("senderInfo", senderInfo);

                    startActivity(showChat);
                });
            }
        };
        chatsListView.setAdapter(adapter);

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
