package it.polito.mad.koko.kokolab3.messaging;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polito.mad.koko.kokolab3.firebase.DatabaseManager;
import it.polito.mad.koko.kokolab3.util.JsonUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageManager {

    private static String TAG = "MessageManager";

    /*
     * All the messages of the current user
     */
    private static ArrayList<Message> userMessages;

    /*
     * All the chats' ID of the current user
     */
    private static Map<String, Map<String,String>> userChatIDs;

    /*
     * All the messages corresponding to a chat ID

    private static Map<String,ArrayList<Message>> chatsMessages;
    */

    /*
     * All the chats of the current user
     */

    private static ArrayList<Chat> userChats;

    /*
     * Listener to all the current user's chats ID
     */

    private static ValueEventListener userChatIDsListener;

    /*
     * Listener to all the current user's chats
     */
    private static ChildEventListener userChatsMessagesListener;


    /**
     * HTTP client
     */
    static OkHttpClient mClient = new OkHttpClient();

    /**
     * MediaType of the RequestBody.
     * If null, UTF-8 will be used.
     */
    private static final MediaType CONTENT_TYPE = null;

    /**
     * Messages placeholders
     */
    public static final String
        // Sender's username placeholder
        SENDER_USERNAME_PLACEHOLDER = "%SENDER_USER%",

        // Book name placeholder
        BOOK_NAME_PLACEHOLDER = "%BOOK_NAME%";

    /**
     * Message strings
     */
    private static final String
        // Message request target URL
        FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send",

        // Book request messages
        BOOK_REQUEST_MESSAGE_TITLE =    "Book exchange request from " +
                                        SENDER_USERNAME_PLACEHOLDER + "!",
        BOOK_REQUEST_MESSAGE_TEXT =     "You can accept/deny immediately or check " +
                                        SENDER_USERNAME_PLACEHOLDER +
                                        "'s profile by clicking this notification",

        // Book positive response messages
        BOOK_POSITIVE_RESPONSE_MESSAGE_TITLE =  SENDER_USERNAME_PLACEHOLDER +
                                                " has accepted your request!",
        BOOK_POSITIVE_RESPONSE_MESSAGE_TEXT =   "You can now exchange " +
                                                BOOK_NAME_PLACEHOLDER +
                                                ". Tap here to open a chat.",

        // Book positive response messages
        BOOK_NEGATIVE_RESPONSE_MESSAGE_TITLE =  SENDER_USERNAME_PLACEHOLDER +
                                                " has declined your request!",
        BOOK_NEGATIVE_RESPONSE_MESSAGE_TEXT =   "You cannot exchange " +
                                                BOOK_NAME_PLACEHOLDER + " anymore.";

    /**
     * First chat message displayed as an intro.
     */
    public static final String FIRST_CHAT_MESSAGE = "You can now start the book exchange negotiation.\n" +
                                                    "Do not send any personal data such as password and credit card numbers.";

    /**
     * Firebase server's key access
     */
    private static String SERVER_KEY =
            "AAAAsT0hg7k:APA91bEfqnxkD9J_FkI1MBqo3NqBDgaYD1A1n9uRsrsR0HQScs1v4DddJ" +
                    "KTsUh0muPmgHcgJFSjA-0zULkf-40Gurj4absEFz7AgKi_W6CRyVm2zQYIn3AcksIELpMuejGCb4QkgG4fD";
    private static String messageID;

    /**
     * It sends a book exchange request notification to a specific user.
     *
     * @param senderId
     * @param senderUsername
     * @param senderImage
     * @param receiverId
     * @param receiverUsername
     * @param receiverImage
     * @param receiverToken
     * @param bookTitle
     */
    public static void sendRequestNotification(     // Sender info
                                                    final String senderId,
                                                    final String senderUsername,
                                                    final String senderImage,
                                                    final String senderToken,

                                                    // Receiver info
                                                    final String receiverId,
                                                    final String receiverUsername,
                                                    final String receiverImage,
                                                    final String receiverToken,

                                                    // Book info
                                                    final String bookTitle) {
        String notificationTitle = BOOK_REQUEST_MESSAGE_TITLE.replaceAll(SENDER_USERNAME_PLACEHOLDER, senderUsername);
        String notificationText = BOOK_REQUEST_MESSAGE_TEXT.replaceAll(SENDER_USERNAME_PLACEHOLDER, senderUsername);

        sendNotification(
                notificationTitle,
                notificationText,
                senderId,
                senderUsername,
                senderImage,
                senderToken,
                receiverId,
                receiverUsername,
                receiverImage,
                receiverToken,
                bookTitle,
                "request"
        );
    }

    /**
     * It sends a book exchange response notification.
     * @param responseIntent    response intent data.
     * @param accepted          whether the exchange response was positive or not.
     */
    public static void sendResponseNotification(Intent responseIntent, boolean accepted) {
        // Sender data
        String receiverId = responseIntent.getStringExtra("senderId");
        String receiverUsername = responseIntent.getStringExtra("senderUsername");
        String receiverImage = responseIntent.getStringExtra("senderImage");
        String receiverToken = responseIntent.getStringExtra("senderToken");

        // Receiver data
        String senderId = responseIntent.getStringExtra("receiverId");
        String senderUsername = responseIntent.getStringExtra("receiverUsername");
        String senderImage = responseIntent.getStringExtra("receiverImage");
        String senderToken = responseIntent.getStringExtra("receiverToken");

        // Book data
        String bookTitle = responseIntent.getStringExtra("book");

        // Notification title
        String notificationTitle = ((accepted) ? BOOK_POSITIVE_RESPONSE_MESSAGE_TITLE : BOOK_NEGATIVE_RESPONSE_MESSAGE_TITLE)
                .replaceAll(SENDER_USERNAME_PLACEHOLDER, senderUsername).replaceAll(BOOK_NAME_PLACEHOLDER, bookTitle);

        // Notification text
        String notificationText = ((accepted) ? BOOK_POSITIVE_RESPONSE_MESSAGE_TEXT : BOOK_NEGATIVE_RESPONSE_MESSAGE_TEXT)
                .replaceAll(SENDER_USERNAME_PLACEHOLDER, senderUsername).replaceAll(BOOK_NAME_PLACEHOLDER, bookTitle);

        sendNotification(
                notificationTitle,
                notificationText,
                senderId,
                senderUsername,
                senderImage,
                senderToken,
                receiverId,
                receiverUsername,
                receiverImage,
                receiverToken,
                bookTitle,
                accepted ? "accept" : "decline"
        );
    }

    /**
     * It sends
     */
    public static void sendMessageNotification(// Sender info
                                               final String senderId,
                                               final String senderUsername,
                                               final String senderImage,
                                               final String senderToken,

                                               // Receiver info
                                               final String receiverId,
                                               final String receiverUsername,
                                               final String receiverImage,
                                               final String receiverToken,

                                               // Book info
                                               final String bookTitle,

                                               // Message info
                                               final String messageText) {
        sendNotification(
                senderUsername,
                messageText,
                senderId,
                senderUsername,
                senderImage,
                senderToken,
                receiverId,
                receiverUsername,
                receiverImage,
                receiverToken,
                bookTitle,
                "message"
        );
    }

    /**
     * It sends a general notification to a specific user.
     * The JSON message structure is defined by Firebase:
     * https://firebase.google.com/docs/cloud-messaging/send-message#http_post_request
     *
     * A JSON exmaple is shown below:
     *
     * {    "notification": {
     *          "title": title,
     *          "body": body
     *      },
     *
     *      "priority": "high",
     *
     *      "to": receiver_token
     *
     *      "data": {
     *          "type": "request" | "accept" | "decline" | "message"
     *
     *          "sender": {
     *              "id": "kE3ErSqw...",
     *              "username": sender_username,
     *              "image": "https://firebasestorage..."
     *              "token": sender_token
     *          }
     *
     *          "receiver": {
     *              "id": "f3j1lw...",
     *              "username": receiver_username,
     *              "image": "https://firebasestorage..."
     *              "token: receiver_token,
     *          }
     *
     *          "book": {
     *              "title": book_title
     *          }
     *      }
     * }
     *
     * @param notificationTitle the notification title.
     * @param notificationText  the notification text.
     * @param senderId
     * @param senderUsername
     * @param senderImage
     * @param receiverId
     * @param receiverUsername
     * @param receiverImage
     * @param bookTitle
     */
    private static void sendNotification(    // Notification title and text
                                             final String notificationTitle,
                                             final String notificationText,

                                             // Sender info
                                             final String senderId,
                                             final String senderUsername,
                                             final String senderImage,
                                             final String senderToken,

                                             // Receiver info
                                             final String receiverId,
                                             final String receiverUsername,
                                             final String receiverImage,
                                             final String receiverToken,

                                             // Book info
                                             final String bookTitle,

                                             // Notification info
                                             final String notificationType) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    // Notification
                    JSONObject notification = new JSONObject();

                    notification.put("title", notificationTitle);
                    notification.put("text", notificationText);

                    // Sender
                    JSONObject sender = new JSONObject();
                    sender.put("id", senderId);
                    sender.put("username", senderUsername);
                    sender.put("image", senderImage);
                    sender.put("token", senderToken);

                    // Receiver
                    JSONObject receiver = new JSONObject();
                    receiver.put("id", receiverId);
                    receiver.put("username", receiverUsername);
                    receiver.put("image", receiverImage);
                    receiver.put("token", receiverToken);

                    // Book
                    JSONObject book = new JSONObject();
                    book.put("title", bookTitle);

                    // Data
                    JSONObject data = new JSONObject();
                    data.put("type", notificationType);
                    data.put("sender", sender);
                    data.put("receiver", receiver);
                    data.put("book", book);

                    // Root
                    JSONObject root = new JSONObject();
                    root.put("notification", notification);
                    root.put("priority", "high");
                    root.put("to", receiverToken);
                    root.put("data", data);
                    Log.d(TAG, "JSON message: " + JsonUtil.formatJson(root.toString()));

                    // Sending the JSON packet to FCM
                    String result = postToFCM(root.toString());

                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            /**
             *  In this method we have the result of AsyncTasck in form of log
             * @param result of the AsyncTasck
             */

            @Override
            protected void onPostExecute(String result) {
                //Log.d(TAG, result);

            }
        }.execute();
    }

    /**
     * create http request with OkHttpClient
     *
     * @param bodyString is Json with data
     * @return response of request
     * @throws IOException
     */
    private static String postToFCM(String bodyString) throws IOException {
        // Create a request body with json create in sendNotification or sendMessage
        RequestBody body = RequestBody.create(CONTENT_TYPE, bodyString);
        //Log.d(TAG, "body: " + String.valueOf(body));
        // Create a http request
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .build();
        //Log.d(TAG, "request: " + String.valueOf(request));
        // Use post http method to make a request
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Create the listener to populate the chat list with all the current user's chat from Firebase
     */
    public static void setUserChatsIDListener() {
        userChatIDs = new HashMap<>();
        userChatIDsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    userChatIDs.putAll((Map<String, Map<String,String>>) dataSnapshot.getValue());
                    Log.d(TAG,userChatIDs.toString());
                    MessageManager.populateUserMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Attach the listener to the chats of the current user
     */
    public static void populateUserChatsID() {
        DatabaseManager.get("users", FirebaseAuth.getInstance().getCurrentUser().getUid(), "chats").addValueEventListener(userChatIDsListener);
    }

    /**
     * It sets the listener to retrieve all the messages of a chat
     *
     * @param userChat chat class in which we put all the messages corresponding to the chatID
     */
    public static void setUserMessagesListener(Chat userChat) {
        userChatsMessagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {

                    String datasnapshot = dataSnapshot.toString();
                    //Log.d(TAG, datasnapshot);
                    Message message = new Message();
                    message.setSender((String) dataSnapshot.child("sender").getValue());
                    message.setText((String) dataSnapshot.child("text").getValue());
                    message.setTimestamp((String) dataSnapshot.child("timestamp").getValue());
                    //Log.d(TAG, message.toString());

                    /**
                     * populate the Map with key: chatID and value:message
                     */
                    for (Chat chat : userChats)
                        if (chat.equals(userChat))
                            chat.getChatMessages().add(message);

                }

                //Log.d(TAG, userChat.toString());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /**
     * For each chatID in the current user creates and attaches the Child listener to retrieve all the chat messages
     *
     * ArrayList<Chat>
     */
    public static void populateUserMessages() {
        userChats = new ArrayList<>();

        for (String chatID : userChatIDs.keySet()) {

            userMessages = new ArrayList<>();
            Chat userChat = new Chat(chatID, userMessages);

            userChats.add(userChat);

            MessageManager.setUserMessagesListener(userChat);
            DatabaseManager.get("chats", chatID, "messages").addChildEventListener(userChatsMessagesListener);
        }
    }


    public static void removeUserChatsMessagesListener(){
        userChats=new ArrayList<>();

        for(String chatID: userChatIDs.keySet()){

            userMessages=new ArrayList<>();
            Chat userChat=new Chat(chatID,userMessages);

            userChats.add(userChat);

            MessageManager.setUserMessagesListener(userChat);
            DatabaseManager.get("chats",chatID,"messages").removeEventListener(userChatsMessagesListener);
        }

    }


    /**
     * It creates a chat entry in Firebase and a reference in both users involved.
     * @param intent    the intent containing chat information.
     */
    public static void createChat(Intent intent) {
        // Retrieving sender data
        String senderId = intent.getStringExtra("senderId");
        String senderUsername = intent.getStringExtra("senderUsername");
        String senderImage = intent.getStringExtra("senderImage");
        String senderToken = intent.getStringExtra("senderToken");

        // Retrieving Receiver data
        String receiverId = intent.getStringExtra("receiverId");
        String receiverUsername = intent.getStringExtra("receiverUsername");
        String receiverImage = intent.getStringExtra("receiverImage");
        String receiverToken = intent.getStringExtra("receiverToken");

        // Creating the 'chats' child
        DatabaseReference messagesRef = DatabaseManager.get("chats");
        String chatID = messagesRef.push().getKey();

        // Creating the 'chats/chat_id/requester' child
        messagesRef.child(chatID).child("requester").setValue(senderId);

        // Creating the 'chats/chat_id/bookOwner' child
        messagesRef.child(chatID).child("bookOwner").setValue(receiverId);

        // Creating the 'chats/chat_id/messages' child
        messagesRef.child(chatID).child("messages");

        // Creating a chat child under the sender one
        DatabaseReference usersRefSender = DatabaseManager.get("users", senderId);
        usersRefSender.child("chats").child(chatID).child("secondPartyUsername").setValue(receiverUsername);
        usersRefSender.child("chats").child(chatID).child("secondPartyId").setValue(receiverId);
        usersRefSender.child("chats").child(chatID).child("secondPartyImage").setValue(receiverImage);
        usersRefSender.child("chats").child(chatID).child("secondPartyToken").setValue(receiverToken);

        // Creating a chat child under the receiver one
        DatabaseReference usersRefReceiver = DatabaseManager.get("users").child(receiverId);
        usersRefReceiver.child("chats").child(chatID).child("secondPartyUsername").setValue(senderUsername);
        usersRefReceiver.child("chats").child(chatID).child("secondPartyId").setValue(senderId);
        usersRefReceiver.child("chats").child(chatID).child("secondPartyImage").setValue(senderImage);
        usersRefReceiver.child("chats").child(chatID).child("secondPartyToken").setValue(senderToken);

        createMessage(chatID, senderId,receiverId, FIRST_CHAT_MESSAGE);
    }


    /**
     * Creates a message entry in Firebase
     *
     * @param chatId      id of the chat which the message belongs to
     * @param sender      id of the sender of the message
     * @param receiver    id of the receiver of the message
     * @param messageText content of the message
     */

    public static void createMessage(String chatId, String sender, String receiver, String messageText) {
        // Creating a message entry
        DatabaseReference messagesRef = DatabaseManager.get("chats", chatId, "messages");
        messageID = messagesRef.push().getKey();
        Message message = new Message();
        message.setSender(sender);
        message.setText(messageText);
        message.setCheck("false");
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
        message.setTimestamp(timeStamp);
        messagesRef.child(messageID).setValue(message);

        // Creating the last message entry on both receiver and sender
        DatabaseManager.set(messageText, "users/"+sender+"chats/" + chatId + "/lastMessage");
        DatabaseManager.set(messageText, "users/"+receiver+"chats/" + chatId + "/lastMessage");
    }

    /**
     * @return  all user's messages from Firebase
     */
    public static ArrayList<Chat> getUserChats() {
        return userChats;
    }

    /**
     * @return  a Java Map having chatID as key and a receiver info Map
     *          as value.
     */
    public static Map<String, Map<String,String>> getUserChatIDs() {
        return userChatIDs;
    }

    /**
     * @return  messageID when we create a new chat with notification
     */
    public static String getMessageID() {
        return messageID;
    }

    public static void setFirebaseCheck(String chatID, String messageID) {
        // Creating the 'chats' child
        DatabaseReference messagesRef = DatabaseManager.get("chats", chatID, "messages",messageID);

        // Setting if the message has been checked child
        messagesRef.child("check").setValue("true");
    }
}
