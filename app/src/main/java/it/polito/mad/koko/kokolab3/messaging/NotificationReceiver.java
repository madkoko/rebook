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

        //Instance of profileManager
        profileManager = ProfileManager.getInstance();

        //Instance of auth profile
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Profile profile = profileManager.getProfile(currentUserId);

        // Depending on the notification action
        switch (intent.getAction()) {
            // The book owner has accepted the book exchange
            case ACCEPT_ACTION:
                // First test message
                String sender = intent.getStringExtra("sender");
                String senderId = intent.getStringExtra("senderId");
                String senderImage = intent.getStringExtra("senderImage");
                String bookName = intent.getStringExtra("book");

                // Notifying the requester
                /*MessageManager.sendPositiveResponseNotification(
                        // Sender info
                        senderId,                       // sender ID
                        senderProfile.getName(),        // sender username
                        senderProfile.getImgUrl(),      // sender image

                        // Receiver info
                        receiverId,                     // receiver ID
                        receiverProfile.getName(),      // receiver username
                        receiverToken,                  // receiver token
                        receiverProfile.getImgUrl(),    // receiver image

                        // Book info
                        book.getTitle()                 // book title
                );*/

                // Creating a chat with the user
                MessageManager.createChat(
                        sender,
                        profile.getName(),
                        senderId,
                        currentUserId,
                        senderImage,
                        profile.getImgUrl(),
                        "Hi, " + sender + " would like to exchange " + bookName + " with you!"
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
