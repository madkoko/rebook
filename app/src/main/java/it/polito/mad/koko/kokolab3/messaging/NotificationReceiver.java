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

        // profileManager instance
        profileManager = ProfileManager.getInstance();

        // Current profile information
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Profile currentProfile = profileManager.getProfile(currentUserId);

        // Depending on the notification action
        switch (intent.getAction()) {
            // The book owner has accepted the book exchange
            case ACCEPT_ACTION:
                // Sender data
                String senderId = intent.getStringExtra("senderId");
                String senderUsername = intent.getStringExtra("senderUsername");
                String senderImage = intent.getStringExtra("senderImage");
                String senderToken = intent.getStringExtra("senderToken");

                // Receiver data
                String receiverId = intent.getStringExtra("receiverId");
                String receiverUsername = intent.getStringExtra("receiverUsername");
                String receiverImage = intent.getStringExtra("receiverImage");

                // Book data
                String bookName = intent.getStringExtra("book");

                // Notifying the requester
                MessageManager.sendPositiveResponseNotification(
                        // Sender info
                        receiverId,         // sender ID
                        receiverUsername,   // sender username
                        receiverImage,      // sender image

                        // Receiver info
                        senderId,           // receiver ID
                        senderUsername,     // receiver username
                        senderToken,        // receiver token
                        senderImage,        // receiver image

                        // Book info
                        bookName           // book title
                );

                // Creating a chat with the user
                MessageManager.createChat(
                        // Sender info
                        receiverId,         // sender ID
                        receiverUsername,   // sender username
                        receiverImage,      // sender image

                        // Receiver info
                        senderId,           // receiver ID
                        senderUsername,     // receiver username
                        senderImage,        // receiver image

                        // Message info
                        MessageManager.FIRST_CHAT_MESSAGE
                );

                // Opening the chat UI
                /*Intent showChat=new Intent(context,ShowChat.class);
                showChat.putExtra("messages",MessageManager.getMessageID());
                showChat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(showChat);*/

                // Opening all user's chats
                Intent showChats = new Intent(context, ShowChats.class);
                showChats.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(showChats);
                //DefaultMessagesActivity.open(context);

                break;

            // The book owner has not accepted the book exchange
            case DECLINE_ACTION:
                // Showing a book exchange declined message
                Toast.makeText(context, "Book exchange declined!", Toast.LENGTH_LONG).show();

                break;
        }
    }
}
