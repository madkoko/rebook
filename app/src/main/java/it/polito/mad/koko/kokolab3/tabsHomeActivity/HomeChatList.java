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

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

                String secondPartyUsername = model.getSecondPartyUsername();
                String secondPartyId = model.getSecondPartyId();
                String secondPartyImage = model.getSecondPartyImage();
                String lastMessage=model.getLastMessage();
                String chatID = adapter.getRef(position).getKey();

                ImageView userThumbnail = (ImageView) view.findViewById(R.id.user_thumbnail);
                TextView chatDest = (TextView) view.findViewById(R.id.chat_dest);
                TextView lastMessageView = (TextView) view.findViewById(R.id.last_message);

                chatDest.setText(secondPartyUsername);
                Picasso.get().load(secondPartyImage).transform(new CircleTransform()).into(userThumbnail);

                lastMessageView.setText(lastMessage);

                view.setOnClickListener(v -> {
                    Intent showChat = new Intent(getActivity(), ShowChat.class);
                    showChat.putExtra("chatId", chatID);
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
