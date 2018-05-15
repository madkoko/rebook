package it.polito.mad.koko.kokolab3.messaging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.models.Card;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.R;

public class ShowChat extends AppCompatActivity {

    /**
     * All the messages of the selected chat
     */
    private ArrayList<Message> messages;
    private String chatId;
    private String currentUserID;
    private BaseAdapter baseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chat);

        //https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-firebaseui-to-populate-a-listview


        messages=(ArrayList<Message>) getIntent().getExtras().get("messages");
        chatId= getIntent().getStringExtra("chatId");
        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();

        ListView chatsListView = findViewById(R.id.chat_listview);
        EditText editText = findViewById(R.id.send_message);
        Button send= findViewById(R.id.send);

        send.setOnClickListener(v -> {
            if(editText.getText().toString()!=null && editText.getText().toString()!=""){
                MessageManager.createMessage(chatId, currentUserID, editText.getText().toString());
                editText.setText("");
                //((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
            }
        });

        // set the list view to show all the books

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
    }
}
