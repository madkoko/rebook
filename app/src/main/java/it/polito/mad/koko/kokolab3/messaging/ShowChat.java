package it.polito.mad.koko.kokolab3.messaging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import it.polito.mad.koko.kokolab3.R;

public class ShowChat extends AppCompatActivity {

    /**
     * All the messages of the selected chat
     */
    //private ArrayList<Message> messages;
    private String chatId;
    private String currentUserID;
    private BaseAdapter baseAdapter;
    private Query query;
    private String TAG= "ShowChatActivity";

    private FirebaseListOptions<Message> options;
    private ListView chatsListView;
    private FirebaseListAdapter<Message> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chat);


        //messages=(ArrayList<Message>) getIntent().getExtras().get("messages");
        chatId= getIntent().getStringExtra("chatId");
        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        query = FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).child("messages");


        chatsListView = findViewById(R.id.chat_listview);
        EditText editText = findViewById(R.id.send_message);
        Button send= findViewById(R.id.send);

        send.setOnClickListener(v -> {
            if(editText.getText().toString()!=null && editText.getText().toString()!=""){
                MessageManager.createMessage(chatId, currentUserID, editText.getText().toString());
                editText.setText("");
                //((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
            }
        });
        //FirebaseListOptions<Message> for retrieving data from firebase
        //query is reference
        options = new FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.adapter_show_chat)
                .setQuery(query, Message.class)
                .build();
        Log.d(TAG, String.valueOf(options.getSnapshots()));

        //FirebaseListAdapter for create ListAdapter Ui from firebaseUi
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View view, Message model, int position) {
                Log.d(TAG, String.valueOf(model));
                TextView messageText =  view.findViewById(R.id.message_text);

                messageText.setText(model.getText());

                if(model.getSender().equalsIgnoreCase(currentUserID))
                    messageText.setGravity(Gravity.RIGHT);
                else
                    messageText.setGravity(Gravity.LEFT);
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

        // set the list view to show all the books
        /*
        if (messages != null) {

            //Log.d(TAG, "book_list onStart ShowBooks" + book_list.toString());


            baseAdapter = new BaseAdapter() {

                @Override
                public int getCount() {
                    return messages.size();
                }

                @Override
                public Object getItem(int i) {
                    return messages.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(final int i, View view, ViewGroup viewGroup) {
                    if (view == null)
                        view = getLayoutInflater().inflate(R.layout.adapter_show_chat, viewGroup, false);

                    TextView messageText = (TextView) view.findViewById(R.id.message_text);
                    messageText.setText(messages.get(i).getText());

                    if(messages.get(i).getSender().equalsIgnoreCase(currentUserID))
                        messageText.setGravity(Gravity.RIGHT);
                    else
                        messageText.setGravity(Gravity.LEFT);

                    return view;
                }
            };
            chatsListView.setAdapter(baseAdapter);
        }
        */
