package it.polito.mad.koko.kokolab2.books;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by Francesco on 13/04/2018.
 */

public class BookManager {

    private static DatabaseReference ref;
    private static Map<String,Book> books;

    public BookManager(){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        ref = database.getReference().child("books");
        populateBookList();
    }

    public void populateBookList(){

        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        books=new HashMap<>();
                        books.clear();
                        books.putAll((HashMap<String,Book>)dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    public static void printBooks(){
        Log.d("debug",books.size()+" "+books.toString());
    }

    public static Map<String,Book> getBooks(){

        return books;
    }

    public static void insertBook(Book book){

        DatabaseReference booksRef = ref.child("books");
        booksRef.push().setValue(book);

    }
}
