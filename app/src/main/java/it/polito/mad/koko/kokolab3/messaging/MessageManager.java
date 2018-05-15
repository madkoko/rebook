package it.polito.mad.koko.kokolab3.messaging;

import android.annotation.SuppressLint;
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
    * All the chats of the current user
    */

    private static ArrayList<Chat> userChats;

    /*
     * All the chats' ID of the current user
     */
    private static Map<String,String> userChatIDs;

    /*
     * All the messages corresponding to a chat ID
     */
    private static Map<String,ArrayList<Message>> chatsMessages;

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
     * Book requests strings
     */
    public static final String
        // Message request target URL
        FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send",

        SENDER_USERNAME_PLACEHOLDER = "%SENDER_USER%",

        BOOK_EXCHANGE_MESSAGE_TITLE =   "Book exchange request from " +
                                        SENDER_USERNAME_PLACEHOLDER + "!",

        BOOK_EXCHANGE_MESSAGE_TEXT =    "You can accept/deny immediately or check " +
                                        SENDER_USERNAME_PLACEHOLDER +
                                        "'s profile by clicking this notification"
    ;

    /**
     * Firebase server's key access
     */
    private static String SERVER_KEY =
        "AAAAsT0hg7k:APA91bEfqnxkD9J_FkI1MBqo3NqBDgaYD1A1n9uRsrsR0HQScs1v4DddJ" +
        "KTsUh0muPmgHcgJFSjA-0zULkf-40Gurj4absEFz7AgKi_W6CRyVm2zQYIn3AcksIELpMuejGCb4QkgG4fD"
    ;

    /**
     * AsyncTask that sends a message from AuthUser to another user.
     * I am not sure if this is the right way to send messages, please check the following:
     *      https://firebase.google.com/docs/cloud-messaging/server
     *
     * @param recipient    Token of user that receive the message
     * @param title         of the message
     * @param body          of the message
     * @param message       is text of message
     *                      The JSON form of message is
     *                      {
     *                          "notification": {
     *                              "title": title,
     *                              "body": body
     *                          },
     *
     *                          "data": {
     *                              "message": message
     *                          },
     *
     *                          "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1..."
     *                      }
     */
    @SuppressLint("StaticFieldLeak")
    public static void sendMessage(final String recipient, final String title, final String body, final String message) {

        new AsyncTask<String, String, String>() {
            /**
             * AsyncTask for send message
             * @param params
             * @return result of sending message
             */
            @Override
            protected String doInBackground(String... params) {
                try {
                    //Create a JSON
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("to", recipient);
                    // String that returns the result of HTTP request
                    String result = postToFCM(root.toString());
                    Log.d(TAG, "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            /**
             *  In this method we have the result of AsyncTasck
             * @param result of the AsyncTasck
             */

            @Override
            protected void onPostExecute(String result) {
                int success, failure = 0;
                try {
                    JSONObject resultJson = new JSONObject(result);
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    Log.d("MessageManager", "success is: " + String.valueOf(success));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "failure is: " + String.valueOf(failure));

                }
            }
        }.execute();
    }

    /**
     * Similar to sendMessage but to send first notifications
     * @param recipient is string with token id
     */
    public static void sendNotification(final String recipient,
                                        final String senderName,
                                        final String bookName) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();

                    String title = BOOK_EXCHANGE_MESSAGE_TITLE.replaceAll(SENDER_USERNAME_PLACEHOLDER, senderName);
                    String text = BOOK_EXCHANGE_MESSAGE_TEXT.replaceAll(SENDER_USERNAME_PLACEHOLDER, senderName);

                    JSONObject data = new JSONObject();
                    data.put("title", title);
                    data.put("text", text);
                    root.put("priority","high");
                    root.put("notification", data);
                    root.put("to", recipient);
                    Log.d(TAG, "root: "+ root.toString());
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
                    Log.d("MessageManager", result);

            }
        }.execute();
    }

    /**
     * create http request with OkHttpClient
     * @param bodyString is Json with data
     * @return response of request
     * @throws IOException
     */
    private static String postToFCM(String bodyString) throws IOException {
        // Create a request body with json create in sendNotification or sendMessage
        RequestBody body = RequestBody.create(CONTENT_TYPE, bodyString);
        Log.d(TAG, "body: "+String.valueOf(body));
        // Create a http request
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key="+ SERVER_KEY)
                .build();
        Log.d(TAG, "request: "+String.valueOf(request));
        // Use post http method to make a request
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    /*
     * Create the listener to populate the chat list with all the current user's chat from Firebase
     */

    public static void setUserChatsIDListener(){
        userChatIDs =new HashMap<>();
        userChatIDsListener =new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userChatIDs.putAll((Map<String,String>)dataSnapshot.getValue());
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

    public static void populateUserChatsID(){
        DatabaseManager.get("users",FirebaseAuth.getInstance().getCurrentUser().getUid(),"chats").addValueEventListener(userChatIDsListener);

    }

    /**
     * set the listener to retrieve all the messages of a chat
     * @param chatID ID of the chat which the listener is attached to
     */

    public static void setUserMessagesListener(String chatID){

        userChatsMessagesListener =new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userMessages=new ArrayList<>();
                if(dataSnapshot.exists()){

                    String datasnapshot=dataSnapshot.toString();
                    Log.d(TAG,datasnapshot);
                    Message message= new Message();
                    message.setSender((String)dataSnapshot.child("sender").getValue());
                    message.setText((String)dataSnapshot.child("text").getValue());
                    message.setTimestamp((String)dataSnapshot.child("timestamp").getValue());
                    Log.d(TAG,message.toString());

                    /**
                     * populate the Map with key: chatID and value:message
                     */
                    userMessages.add(message);

                }
                chatsMessages.put(chatID,userMessages);

                Log.d(TAG,chatsMessages.toString());

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
     */
    public static void populateUserMessages(){
        chatsMessages=new HashMap<>();
        for(String chatID: userChatIDs.keySet()){
            MessageManager.setUserMessagesListener(chatID);
            DatabaseManager.get("chats",chatID,"messages").addChildEventListener(userChatsMessagesListener);
        }
    }

    /**
     * It creates a chat entry in Firebase and a reference in both users involved.
     *  @param sender: sender side of the chat (current logged user)
     *         receiver: receiver side of the chat (value of "chatID")
     */
    public static void createChat(String sender, String receiver) {


        DatabaseReference messagesRef= DatabaseManager.get("chats");

        String chatID=messagesRef.push().getKey();
        messagesRef.child(chatID).child("messages");

        DatabaseReference usersRef=DatabaseManager.get("users").child(sender);

        usersRef.child("chats").child(chatID).setValue(receiver);

        createMessage(chatID,sender,"ciao");

    }


    /**
     * Creates a message entry in Firebase
     * @param chatID id of the chat which the message belongs to
     * @param sender id of the sender of the message
     * @param messageText content of the message
     */

    public static void createMessage(String chatID,String sender,String messageText){

        DatabaseReference messagesRef=DatabaseManager.get("chats").child(chatID).child("messages");

        String messageID=messagesRef.push().getKey();

        Message message=new Message();
        message.setSender(sender);
        message.setText(messageText);
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
        message.setTimestamp(timeStamp);

        messagesRef.child(messageID).setValue(message);

    }

    /*
     * Return all user's messages from Firebase
     */

    public static ArrayList<Message> getUserMessages(){

        return userMessages;
    }

    /*
     * Return all user's chat from Firebase
     */
    public static Map<String,ArrayList<Message>> getUserChats(){

        return chatsMessages;
    }

    /**
     * Return all user's chatID as Key and receiverID as Value
     */
    public static Map<String,String> getUserChatIDs(){
        return userChatIDs;
    }

}
