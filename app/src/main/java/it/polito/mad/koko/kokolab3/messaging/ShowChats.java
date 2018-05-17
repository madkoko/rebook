package it.polito.mad.koko.kokolab3.messaging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;

public class ShowChats extends AppCompatActivity {

    /**
     * Map ChatID-Messages with all the user's chats
     */
    private ArrayList<Chat> userChats;

    /**
     * Map ChatID-ReceiverID with all the user's chats
     */
    private Map<String, Map<String, String>> userChatIDs;

    private static final String TAG = "ShowChats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chats);

        userChats = MessageManager.getUserChats();
        userChatIDs = MessageManager.getUserChatIDs();

        ListView chatsListView = findViewById(R.id.chats_listview);

        // set the list view to show all the books

        if (userChats != null && userChatIDs != null) {

            //Log.d(TAG, "book_list onStart ShowBooks" + book_list.toString());

            chatsListView.setAdapter(new BaseAdapter() {

                @Override
                public int getCount() {
                    return userChatIDs.size();
                }

                @Override
                public Object getItem(int i) {
                    return userChatIDs.keySet().toArray()[i];
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(final int i, View view, ViewGroup viewGroup) {
                    if (view == null)
                        view = getLayoutInflater().inflate(R.layout.chats_adapter_layout, viewGroup, false);

                    ImageView userThumbnail = (ImageView) view.findViewById(R.id.user_thumbnail);
                    TextView chatDest = (TextView) view.findViewById(R.id.chat_dest);
                    TextView lastMessageText = (TextView) view.findViewById(R.id.last_message);
                    String chatID = (String) userChatIDs.keySet().toArray()[i];

                    Map<String, String> chatInfo = userChatIDs.get(chatID);

                    String secondPartyUsername = chatInfo.get("secondPartyUsername");
                    String secondPartyId = chatInfo.get("secondPartyId");
                    String secondPartyImage = chatInfo.get("secondPartyImage");
                    Picasso.get().load(secondPartyImage).fit().centerCrop().into(userThumbnail);

                    chatDest.setText(secondPartyUsername);

                    ArrayList<Message> messages = new ArrayList<>();
                    for (Chat chat : userChats) {
                        if (chat.getChatID().equalsIgnoreCase(chatID)) {
                            messages = chat.getChatMessages();
                            Message lastMessage = messages.get(messages.size() - 1);
                            String text = lastMessage.getText().toString();
                            lastMessageText.setText(text);
                        }
                    }
                    final ArrayList<Message> chatMessages = messages;
                    view.setOnClickListener(v -> {
                        Intent showChatIntent = new Intent(getApplicationContext(), ShowChat.class);
                        showChatIntent.putExtra("messages", chatMessages);
                        showChatIntent.putExtra("chatId", chatID);
                        startActivity(showChatIntent);
                    });

                    return view;
                }
            });
        }

    }
}
