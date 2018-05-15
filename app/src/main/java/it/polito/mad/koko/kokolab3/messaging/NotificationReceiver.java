package it.polito.mad.koko.kokolab3.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import it.polito.mad.koko.kokolab3.ui.chat.DefaultMessagesActivity;

import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.ACCEPT_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.DECLINE_ACTION;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Debugging
        Log.d(TAG, "New notification. Action: " + intent.getAction());

        // Depending on the notification action
        switch (intent.getAction()) {
            // The book owner has accepted the book exchange
            case ACCEPT_ACTION:
                // Notifying the requester
                

                // Creating a chat with the user
                MessageManager.createChat(
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        intent.getStringExtra("accepter")
                );

                // Opening the chat UI
                DefaultMessagesActivity.open(context);

                break;

            // The book owner has not accepted the book exchange
            case DECLINE_ACTION:
                // Showing a book exchange declined message
                Toast.makeText(context, "Book exchange declined!", Toast.LENGTH_LONG).show();

                break;
        }
    }
}
