/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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
import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.ShowProfile;
import it.polito.mad.koko.kokolab3.ui.chat.DefaultDialogsActivity;
import it.polito.mad.koko.kokolab3.ui.chat.DefaultMessagesActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Notification properties
     */
    private static final Class NOTIFICATION_ACTION = ShowProfile.class;
    private static final int    NOTIFICATION_ICON = R.mipmap.icon,
                                NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_MAX;
    /**
     * Accepting a book exchange request actions and properties
     */
    private static final Class ACCEPT_ACTION = DefaultMessagesActivity.class;
    private static final String ACCEPT_BUTTON_STRING = "Accept";
    private static final int ACCEPT_ICON = R.mipmap.icon;

    /**
     * Denying a book exchange request actions and properties
     */
    private static final Class DECLINE_ACTION = DefaultMessagesActivity.class;
    private static final String DECLINE_BUTTON_STRING = "Decline";
    private static final int DECLINE_ICON = R.mipmap.icon;

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
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            sendNotification(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody()
            );
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
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
     * @param title     FCM message title received.
     * @param body      FCM message body received.
     */
    private void sendNotification(String title, String body) {
        // Tapping the notification
        Intent showSenderProfileIntent = new Intent(this, NOTIFICATION_ACTION);
        showSenderProfileIntent.putExtra("UserID", /*TODO put sender UID here*/ FirebaseAuth.getInstance().getUid());
        showSenderProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent showSenderProfilePendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, showSenderProfileIntent,
                PendingIntent.FLAG_ONE_SHOT);

        // Accepting the book exchange request
        Intent acceptIntent = new Intent(this, ACCEPT_ACTION);
        showSenderProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, acceptIntent,
                PendingIntent.FLAG_ONE_SHOT);

        // Declining the book exchange request


        // Creating the notification
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        // Notification's icon
                        .setSmallIcon(NOTIFICATION_ICON)

                        // Title and expandable subtitle
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        // .setSubText() // TODO cumulative number of requests

                        // Tapping the notification
                        .setContentIntent(showSenderProfilePendingIntent)

                        // Notification action buttons
                        .addAction(ACCEPT_ICON, ACCEPT_BUTTON_STRING, acceptPendingIntent)

                        // Priorities, sound and style
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NOTIFICATION_PRIORITY)
                        .setOnlyAlertOnce(false)
                        .setColorized(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}