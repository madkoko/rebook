package it.polito.mad.koko.kokolab3.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import it.polito.mad.koko.kokolab3.profile.ProfileManager;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;

import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.ACCEPT_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.DECLINE_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.MESSAGE_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.REQUEST_ACTION;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    private ProfileManager profileManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Debugging
        Log.d(TAG, "New notification. Action: " + intent.getAction());

        // Retrieving all chat messages
        MessageManager.removeUserChatsMessagesListener();

        // In case a book exchange request has been received
        if (intent.getAction().compareTo(REQUEST_ACTION) == 0) {
            // Starting the showProfile activity
            Intent showProfileIntent = new Intent(context, ShowProfile.class);
            showProfileIntent.putExtra("UserID", intent.getStringExtra("UserID"));
            context.startActivity(showProfileIntent);
        } else if (intent.getAction().compareTo(MESSAGE_ACTION) == 0) {
            // Starting the showChat activity
            Intent showChatIntent = new Intent(context, ShowChat.class);
            showChatIntent.putExtra("chatId", intent.getStringExtra("chatId"));
            context.startActivity(showChatIntent);
        }
        // In case a book exchange response has been received, whether it's positive or not
        else if (intent.getAction().compareTo(ACCEPT_ACTION) == 0 || intent.getAction().compareTo(DECLINE_ACTION) == 0) {
            // Retrieving the book exchange outcome
            boolean exchangeAccepted = intent.getAction().compareTo(ACCEPT_ACTION) == 0;

            if (!exchangeAccepted)
                // Sending a negative response notification
                MessageManager.sendResponseNotification(intent, exchangeAccepted);

            // If the book exchange has been accepted
            if (exchangeAccepted) {
                // Creating a chat with the user
                String chatId = MessageManager.createChat(intent);

                // Sending a positive response notification
                intent.putExtra("chatId", chatId);
                MessageManager.sendResponseNotification(intent, exchangeAccepted);

                MessageManager.populateUserMessages();

                // Starting the showChat activity
                Intent showChatIntent = new Intent(context, ShowChat.class);
                showChatIntent.putExtra("chatId", chatId);
                context.startActivity(showChatIntent);
            }
            // If the book exchange has not been accepted
            else if (intent.getAction().compareTo(DECLINE_ACTION) == 0) {
                // Showing a book exchange declined message
                Toast.makeText(context, "Book exchange declined!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
