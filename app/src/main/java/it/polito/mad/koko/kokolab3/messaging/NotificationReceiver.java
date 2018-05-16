package it.polito.mad.koko.kokolab3.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.ACCEPT_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.DECLINE_ACTION;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    private ProfileManager profileManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Debugging
        Log.d(TAG, "New notification. Action: " + intent.getAction());

        // Retrieving all chat messages
        MessageManager.removeUserChatsMessagesListener();

        // In case a book exchange response has been received, whether it's positive or not
        if(intent.getAction().compareTo(ACCEPT_ACTION) == 0 || intent.getAction().compareTo(DECLINE_ACTION) == 0) {
            // Retrieving the book exchange outcome
            boolean exchangeAccepted = intent.getAction().compareTo(ACCEPT_ACTION) == 0;

            // Sending a response notification
            MessageManager.sendResponseNotification(intent, exchangeAccepted);

            // If the book exchange has been accepted
            if(exchangeAccepted) {
                // Creating a chat with the user
                MessageManager.createChat(intent);

                // Opening the chat UI
                /*Intent showChat=new Intent(context,ShowChat.class);
                showChat.putExtra("messages",MessageManager.getMessageID());
                showChat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(showChat);*/
                MessageManager.populateUserMessages();
                Intent showChats=new Intent(context,ShowChats.class);
                showChats.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(showChats);
                //DefaultMessagesActivity.open(context);
            }
            // If the book exchange has not been accepted
            else {
                // Showing a book exchange declined message
                Toast.makeText(context, "Book exchange declined!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
