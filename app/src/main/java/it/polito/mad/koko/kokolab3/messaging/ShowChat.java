package it.polito.mad.koko.kokolab3.messaging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chat);

        messages=(ArrayList<Message>) getIntent().getExtras().get("messages");

        ListView chatsListView = findViewById(R.id.chat_listview);

        // set the list view to show all the books

        if (messages != null) {

            //Log.d(TAG, "book_list onStart ShowBooks" + book_list.toString());

            chatsListView.setAdapter(new BaseAdapter() {

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

                    String currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if(messages.get(i).getSender().equalsIgnoreCase(currentUserID))
                        messageText.setGravity(Gravity.RIGHT);
                    else
                        messageText.setGravity(Gravity.LEFT);

                    return view;
                }
            });
        }
    }
}
