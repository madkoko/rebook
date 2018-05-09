package it.polito.mad.koko.kokolab3.messaging;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendToAnotherUser {

    private static String TAG = "SendToAnotherUser";

    // Iniziate Library OkHttpClient
    static OkHttpClient mClient = new OkHttpClient();
    //HTTP POST request url
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    //key to access to Firebase as server
    private static String SERVER_KEY = "AAAAsT0hg7k:APA91bEfqnxkD9J_FkI1MBqo3NqBDgaYD1A1n9uRsrsR0HQScs1v4DddJKTsUh0muPmgHcgJFSjA-0zULkf-40Gurj4absEFz7AgKi_W6CRyVm2zQYIn3AcksIELpMuejGCb4QkgG4fD";
    //JSON for send http request to Firebase
    private static final MediaType JSON = null;


    /**
     * AsyncTask that send a message from AuthUser to another user
     *
     *
     * @param recipients Token of user that recive the message
     * @param title      of message
     * @param body       of message
     * @param message    is text of message
     *                   The JSON form of message is
     *                   {"notification": {
     *                      "title": title,
     *                      "body": body},
     *                   "data":{
     *                      "message":message},
     *                   "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1..."
     *                   }
     *  I do not sure if the right way for send messages check on:
     *  https://firebase.google.com/docs/cloud-messaging/server
     */
    @SuppressLint("StaticFieldLeak")
    public static void sendMessage(final String recipients, final String title, final String body, final String message) {

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
                    root.put("to", recipients);
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
                    Log.d("SendToAnotherUser", "succes is: " + String.valueOf(success));
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

    public static void sendNotification(final String recipient) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();

                    JSONObject data = new JSONObject();
                    data.put("title", "ciao cacca");
                    data.put("text", "sei davvero una cacca");
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
                    Log.d("SendToAnotherUser", result);

            }
        }.execute();
    }

    /**
     * create http request with OkHttpClient
     * @param bodyString is Json with data
     * @return response of request
     * @throws IOException
     */
    static String postToFCM(String bodyString) throws IOException {
        // Create a request body with json create in sendNotification or sendMessage
        RequestBody body = RequestBody.create(JSON, bodyString);
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


}
