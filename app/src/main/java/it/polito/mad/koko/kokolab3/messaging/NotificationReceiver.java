package it.polito.mad.koko.kokolab3.messaging;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import it.polito.mad.koko.kokolab3.request.RequestManager;

import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.ACCEPT_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.DECLINE_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.MESSAGE_ACTION;
import static it.polito.mad.koko.kokolab3.messaging.MyFirebaseMessagingService.REQUEST_ACTION;

public class NotificationReceiver extends BroadcastReceiver { //entra come prima cosa dopo che clicco accetta

    private static final String TAG = "NotificationReceiver";

    private static String activeChatId = ""; // Currently active chat
    private static boolean isActive = true;

    @Override
    public void onReceive(Context context, Intent intent){

        // [ Debug ]
        Log.d(TAG, "New notification. Action: " + intent.getAction());


        String senderId= intent.getStringExtra("senderId");
        String bookId=intent.getStringExtra("bookId");

        // Retrieving all chat messages
        // MessageManager.removeUserChatsMessagesListener();

        // [ Debug ]
        Log.d(TAG, "New notification. Action II : " + intent.getStringExtra("chatID"));

        synchronized (activeChatId) {
            // Retrieving the chat ID corresponding to this new notification
            String chatID = intent.getStringExtra("chatID");
            // If the chat corresponding to this notification is not currently active
            if((chatID != null) && (chatID.compareTo(activeChatId) != 0)) {
                isActive = false;
            }
        }

        // In case a book exchange request has been received
        if (intent.getAction().compareTo(REQUEST_ACTION) == 0) {
            // TODO a summarized ShowProfile activity should be started here

            // Starting the showProfile activity
            /* Intent showProfileIntent = new Intent(context, ShowProfile.class);
            showProfileIntent.putExtra("UserID", intent.getStringExtra("UserID"));
            context.startActivity(showProfileIntent);*/

        } else if (intent.getAction().compareTo(MESSAGE_ACTION) == 0) {

            // Starting the showChat activity
            Intent showChatIntent = new Intent(context, ShowChat.class);
            intent.getExtras();
            showChatIntent.putExtra("chatID", intent.getStringExtra("chatID"));
            showChatIntent.putExtra("originClass", "NotificationReceiver");

            UserChatInfo receiverInfo = (UserChatInfo) intent.getExtras().get("receiverInfo");
            showChatIntent.putExtra("receiverInfo", receiverInfo);

            UserChatInfo senderInfo = (UserChatInfo) intent.getExtras().get("senderInfo");
            showChatIntent.putExtra("senderInfo", senderInfo);

            showChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if(!isActive) {
                context.startActivity(showChatIntent);
            }
        }

        // In case a book exchange response has been received, whether it's positive or not
        else if (intent.getAction().compareTo(ACCEPT_ACTION) == 0 || intent.getAction().compareTo(DECLINE_ACTION) == 0) {
            // Retrieving the book exchange outcome
            boolean exchangeAccepted = intent.getAction().compareTo(ACCEPT_ACTION) == 0;

            UserChatInfo receiverInfo = (UserChatInfo)intent.getExtras().get("receiverInfo");
            UserChatInfo senderInfo = (UserChatInfo)intent.getExtras().get("senderInfo");

            // If the book exchange has not been accepted
            if (!exchangeAccepted)
                // Sending a negative response notification : !!! l'intent ha chatID nll!!!!
                MessageManager.sendResponseNotification(intent, exchangeAccepted);

            // If the book exchange has been accepted
            if (exchangeAccepted) {
                // Creating a chat with the user
                String chatID = intent.getStringExtra("chatID"); //MessageManager.getChatID();

                RequestManager.Companion.acceptRequest(senderId+""+bookId);

                // Sending a positive response notification
                //intent.putExtra("chatID", chatID);
                MessageManager.sendResponseNotification(intent, exchangeAccepted);

                //MessageManager.populateUserMessages();

                // Starting the showChat activity
                Intent showChatIntent = new Intent(context, ShowChat.class);
                showChatIntent.putExtra("chatID", chatID);
                showChatIntent.putExtra("originClass", "notificationReceiver");
                // Qua devo puttare le userChatInfo
                showChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                showChatIntent.putExtra("receiverInfo", receiverInfo);
                showChatIntent.putExtra("senderInfo", senderInfo);

                if(!isActive) {
                    context.startActivity(showChatIntent);
                }

            }

            // If the book exchange has not been accepted
            else if (intent.getAction().compareTo(DECLINE_ACTION) == 0) {
                // Showing a book exchange declined message
                Toast.makeText(context, "Book exchange declined!", Toast.LENGTH_LONG).show();
                RequestManager.Companion.declineRequest(senderId+""+bookId);
            }
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.cancel((int)intent.getExtras().get("notificationID"));

        }
    }
}
