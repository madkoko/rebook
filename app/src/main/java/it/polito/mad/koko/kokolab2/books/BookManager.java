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
import java.util.Arrays;

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
                        Log.d("debug", "ondatachange");
                        //books.clear();
                        //booksTitle=new String[(int)dataSnapshot.getChildrenCount()];
                        //int booksNum=(int)dataSnapshot.getChildrenCount();
                        //Log.d("debug",String.valueOf(dataSnapshot.getChildrenCount())+String.valueOf(bookNum));

                        books=new HashMap<>();

                        int i=0;
                        for(DataSnapshot bookSnapshot: dataSnapshot.getChildren()){
                            Book book=bookSnapshot.getValue(Book.class);
                            Log.d("datasnapshot",book.toString());
                            //books.add(book);
                            books.put(book.getISBN(),book);
                            Log.d("debug",i+" "+books.values());
                            //printBooks();
                            i++;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    /*public static int getBooksNumber(){
        return booksNum;
    }*/

    public static void printBooks(){

        Log.d("debug",books.values().toArray().length+" "+books.values().toArray().toString());

    }

    public static Map<String,Book> getBooks(){

        return books;
    }

    public static void insertBook(Book book){

        DatabaseReference booksRef = ref.child("books");
        booksRef.push().setValue(book);

    }
}
