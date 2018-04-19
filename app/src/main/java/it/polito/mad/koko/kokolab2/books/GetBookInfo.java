package it.polito.mad.koko.kokolab2.books;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Francesco on 18/04/2018.
 */

public class GetBookInfo extends AsyncTask<String, Void, Map<String,String>> {
    //fetch book info

    @Override
    protected Map<String,String> doInBackground(String... bookURLs) {
        //request book info
        Map<String,String> bookInfo=new HashMap<>();
        StringBuilder bookBuilder = new StringBuilder();
        String isbn=null;
        for (String bookSearchURL : bookURLs) {
            //search urls
            try {
                isbn=bookSearchURL.substring(bookSearchURL.lastIndexOf(':')+1);
                URL url=new URL(bookSearchURL);

                HttpURLConnection bookConnection = (HttpURLConnection)url.openConnection();

                Log.d("conn",bookConnection.getResponseMessage());
                InputStream bookContent = new BufferedInputStream(bookConnection.getInputStream());
                InputStreamReader bookInput = new InputStreamReader(bookContent);
                BufferedReader bookReader = new BufferedReader(bookInput);
                String lineIn;
                while ((lineIn=bookReader.readLine())!=null) {
                    bookBuilder.append(lineIn);
                }

            }

            catch(Exception e){ e.printStackTrace(); }


        }

        JsonObject bookJson = new JsonParser().parse(bookBuilder.toString()).getAsJsonObject();

        JsonObject volumeInfo=bookJson.getAsJsonArray("items").get(0).getAsJsonObject().get("volumeInfo").getAsJsonObject();

        //Log.d("book",volumeInfo.toString());

        String title=volumeInfo.get("title").toString().replace("\"", "");

        JsonArray authorsArray=volumeInfo.getAsJsonArray("authors");

        StringBuilder authors=new StringBuilder();

        for(JsonElement author:authorsArray){
            //Log.d("book",author.toString());
            authors.append(author.toString().replace("\"", "")+",");
        }

        /*Log.d("book","authors: "+authors.toString().substring(0, authors.length() - 1));
        Log.d("book","isbn: "+isbn+"\ntitle: "+title);*/

        String publisher=null;

        if(volumeInfo.get("publisher")!=null){
            publisher=volumeInfo.get("publisher").toString().replace("\"", "");
        }

        /*if(publisher!=null){Log.d("book","publisher: "+publisher);}
        else{Log.d("book","publisher not found");}*/

        String editionYear=volumeInfo.get("publishedDate").toString().replace("\"", "");

        bookInfo.put("isbn",isbn);
        bookInfo.put("title",title);
        bookInfo.put("authors",authors.toString().substring(0, authors.length() - 1));
        bookInfo.put("publisher",publisher);
        bookInfo.put("editionYear",editionYear);

        //onPostExecute(bookInfo);

        return bookInfo;
    }

    /*protected void onPostExecute(Map<String,String> bookInfo) {
        // TODO: check this.exception
        // TODO: do something with the feed


        Log.d("debug","onPostExecute");

        BookManager.setBookInfo(bookInfo);
    }*/
}
