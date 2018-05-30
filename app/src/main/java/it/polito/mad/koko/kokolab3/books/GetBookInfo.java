package it.polito.mad.koko.kokolab3.books;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Francesco on 18/04/2018.
 */

public class GetBookInfo extends AsyncTask<String, Void, Map<String, String>> {
    //fetch book info

    private static final String TAG = "GetBookInfo";

    @Override
    protected Map<String, String> doInBackground(String... bookURLs) {
        //request book info
        Map<String, String> bookInfo = new HashMap<>();
        StringBuilder bookBuilder = new StringBuilder();
        String isbn = null;
        for (String bookSearchURL : bookURLs) {
            //search urls
            try {
                isbn = bookSearchURL.substring(bookSearchURL.lastIndexOf(':') + 1);
                URL url = new URL(bookSearchURL);

                HttpURLConnection bookConnection = (HttpURLConnection) url.openConnection();

                Log.d(TAG, bookConnection.getResponseMessage());
                InputStream bookContent = new BufferedInputStream(bookConnection.getInputStream());
                InputStreamReader bookInput = new InputStreamReader(bookContent);
                BufferedReader bookReader = new BufferedReader(bookInput);
                String lineIn;
                while ((lineIn = bookReader.readLine()) != null) {
                    bookBuilder.append(lineIn);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        JsonObject bookJson = new JsonParser().parse(bookBuilder.toString()).getAsJsonObject();

        Log.d(TAG, bookJson.toString());
        Object totalItems = bookJson.get("totalItems");
        Log.d(TAG, totalItems.toString());

        if (totalItems.toString().equalsIgnoreCase("0")) {
            return null;
        }

        JsonObject volumeInfo = bookJson.getAsJsonArray("items").get(0).getAsJsonObject().get("volumeInfo").getAsJsonObject();

        //Log.d(TAG, volumeInfo.toString());

        String title = volumeInfo.get("title").toString().replace("\"", "");

        JsonArray authorsArray = volumeInfo.getAsJsonArray("authors");

        StringBuilder authors = new StringBuilder();

        if (authorsArray!=null&&authorsArray.size() != 0) {
            for (JsonElement author : authorsArray) {
                //Log.d(TAG, author.toString());
                authors.append(author.toString().replace("\"", "") + ",");
            }
        }

        /*Log.d(TAG, "authors: "+authors.toString().substring(0, authors.length() - 1));
        Log.d(TAG, "isbn: "+isbn+"\ntitle: "+title);*/

        String publisher = null;

        if (volumeInfo.get("publisher") != null) {
            publisher = volumeInfo.get("publisher").toString().replace("\"", "");
        }

        /*if(publisher!=null){Log.d(TAG, "publisher: "+publisher);}
        else{Log.d(TAG, "publisher not found");}*/

        String editionYear = volumeInfo.get("publishedDate").toString().replace("\"", "");

        JsonObject imagesObject = volumeInfo.getAsJsonObject("imageLinks");



        bookInfo.put("isbn", isbn);
        bookInfo.put("title", title);
        bookInfo.put("authors", authors.toString().substring(0, authors.length() - 1));
        bookInfo.put("publisher", publisher);
        bookInfo.put("editionYear", editionYear);
        if(imagesObject!=null){
            //Log.d(TAG,imagesObject.get("smallThumbnail").toString());
            String bookThumbnail= imagesObject.get("smallThumbnail").toString().replace("\"", "");
            URL url = null;
            try {
                url = new URL(bookThumbnail);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Log.d(TAG,bmp.toString());
                bookInfo.put("bookThumbnail",bmp.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //onPostExecute(bookInfo);

        return bookInfo;
    }

    /*protected void onPostExecute(Map<String,String> bookInfo) {
        // TODO: check this.exception
        // TODO: do something with the feed


        Log.d(TAG, "onPostExecute");

        BookManager.setBookInfo(bookInfo);
    }*/
}
