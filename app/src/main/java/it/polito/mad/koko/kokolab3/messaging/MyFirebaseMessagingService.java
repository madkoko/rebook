/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polito.mad.koko.kokolab3.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;
import it.polito.mad.koko.kokolab3.ui.ImageManager;
import it.polito.mad.koko.kokolab3.util.JsonUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Notification properties
     */
    private static final Class NOTIFICATION_CALLBACK = ShowProfile.class;
    private static final int NOTIFICATION_ICON = R.mipmap.icon,
            NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_MAX,
            NOTIFICATION_REQUEST_CODE = 1;
    /**
     * Accepting a book exchange request actions and properties
     */
    private static final String ACCEPT_BUTTON_STRING = "Accept";
    private static final int ACCEPT_ICON = R.mipmap.icon,
            ACCEPT_REQUEST_CODE = 2;
    protected static final String ACCEPT_ACTION = "accept";

    /**
     * Denying a book exchange request actions and properties
     */
    private static final String DECLINE_BUTTON_STRING = "Decline";
    private static final int DECLINE_ICON = R.mipmap.icon,
            DECLINE_REQUEST_CODE = 3;
    protected static final String DECLINE_ACTION = "decline";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages: data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ false) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if(remoteMessage.getNotification() != null) {
            String notificationType = remoteMessage.getData().get("type");
            boolean requestNotification = notificationType.compareTo("request") == 0;

            showNotification(remoteMessage, requestNotification);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See showNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param receivedMessage       the received remote message.
     * @param showResponseButtons   whether the response buttons should be displayed or not.
     */
    private void showNotification(RemoteMessage receivedMessage, boolean showResponseButtons) {
        // Retrieving notification and its useful objects
        RemoteMessage.Notification remoteNotification = receivedMessage.getNotification();
        String notificationTitle = remoteNotification.getTitle();
        String notificationBody = remoteNotification.getBody();

        // Debugging
        Log.d(TAG, "Notification data: " + JsonUtil.formatJson(receivedMessage.getData().toString()));

        // Retrieving the sender data object
        String senderJsonString = receivedMessage.getData().get("sender");
        Map<String, String> senderObject = // De-serializing the "sender" JSON object
                new Gson().fromJson(senderJsonString, new TypeToken<Map<String, String>>() {
                }.getType());

        // Retrieving sender information
        String senderId = senderObject.get("id");
        String senderUsername = senderObject.get("username");
        String senderImageURL = senderObject.get("image");
        String senderToken = senderObject.get("token");
        Bitmap senderImageBitmap = ImageManager.getBitmapFromURL(senderImageURL);

        // Retrieving the receiver data object
        String receiverJsonString = receivedMessage.getData().get("receiver");
        Map<String, String> receiverObject = // De-serializing the "sender" JSON object
                new Gson().fromJson(receiverJsonString, new TypeToken<Map<String, String>>() {
                }.getType());

        // Retrieving receiver information
        String receiverId = receiverObject.get("id");
        String receiverUsername = receiverObject.get("username");
        String receiverImageURL = receiverObject.get("image");

        // Retrieving the book data object
        String bookJsonString = receivedMessage.getData().get("book");
        Map<String, String> bookObject = // De-serializing the "book" JSON object
                new Gson().fromJson(bookJsonString, new TypeToken<Map<String, String>>() {
                }.getType());

        // Retrieving book information
        String bookTitle = bookObject.get("title");

        // Intent used upon tapping the notification
        Intent showSenderProfileIntent = new Intent(this, NOTIFICATION_CALLBACK);
        showSenderProfileIntent.putExtra("UserID", /*TODO put sender UID here*/ FirebaseAuth.getInstance().getUid());
        showSenderProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent showSenderProfilePendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, showSenderProfileIntent,
                PendingIntent.FLAG_ONE_SHOT);

        // Intent used upon accepting the book exchange request
        Intent acceptIntent = new Intent(this, NotificationReceiver.class);
        acceptIntent.setAction(ACCEPT_ACTION);
        loadExchangeIntentData(acceptIntent, senderObject, receiverObject, bookObject);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, ACCEPT_REQUEST_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent used upon declining the book exchange request
        Intent declineIntent = new Intent(this, NotificationReceiver.class);
        declineIntent.setAction(DECLINE_ACTION);
        loadExchangeIntentData(declineIntent, senderObject, receiverObject, bookObject);
        PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, DECLINE_REQUEST_CODE, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating the notification
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        // Notification's icon
                        .setSmallIcon(NOTIFICATION_ICON)

                        // Sender's user picture
                        .setLargeIcon(ImageManager.getCircleBitmap(senderImageBitmap))

                        // Title and expandable subtitle
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                        // .setSubText() // TODO cumulative number of requests

                        // Tapping the notification
                        .setContentIntent(showSenderProfilePendingIntent)

                        // Priorities, sound and style
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NOTIFICATION_PRIORITY)
                        .setOnlyAlertOnce(false)
                        .setColorized(true);

        // Notification action buttons
        if(showResponseButtons)
            notificationBuilder
                    .addAction(ACCEPT_ICON, ACCEPT_BUTTON_STRING, acceptPendingIntent)
                    .addAction(DECLINE_ICON, DECLINE_BUTTON_STRING, declinePendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* notification ID */, notificationBuilder.build());
    }

    private static void loadExchangeIntentData(Intent exchangeIntent,
                                               Map<String, String> sender,
                                               Map<String, String> receiver,
                                               Map<String, String> book) {
        // Sender info
        exchangeIntent.putExtra("senderId", sender.get("id"));
        exchangeIntent.putExtra("senderUsername", sender.get("username"));
        exchangeIntent.putExtra("senderImage", sender.get("image"));
        exchangeIntent.putExtra("senderToken", sender.get("token"));

        // Receiver info
        exchangeIntent.putExtra("receiverId", receiver.get("id"));
        exchangeIntent.putExtra("receiverUsername", receiver.get("username"));
        exchangeIntent.putExtra("receiverImage", receiver.get("image"));
        exchangeIntent.putExtra("receiverToken", receiver.get("token"));

        // Book info
        exchangeIntent.putExtra("book", book.get("title"));
    }
}