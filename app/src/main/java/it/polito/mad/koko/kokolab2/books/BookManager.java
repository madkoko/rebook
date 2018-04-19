package it.polito.mad.koko.kokolab2.books;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by Francesco on 13/04/2018.
 */

public class BookManager {

    private static DatabaseReference ref;
    private static Map<String,Book> books=null;
    private static StorageReference storageRef;
    private static String downloadUrl;
    private static Map<String,String> bookInfo;


    public BookManager(){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        ref = database.getReference().child("books");
        FirebaseStorage storage=FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("books");
        populateBookList();
    }

    public static void setBookInfo(Map<String,String> scanBookInfo){
        Log.d("debug","setBookInfo");
        bookInfo=scanBookInfo;
    }
    public static Map<String,String> getBookInfo(){
        Log.d("debug","getBookInfo");
        Log.d("debug",bookInfo.toString());
        return bookInfo;
    }

    public void populateBookList(){

        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount()!=0) {
                            books = new HashMap<>();
                            books.clear();
                            Log.d("debug",dataSnapshot.getValue().toString());

                            // add all the books to an HashMap that will be passed to the activity "ShowBooks"

                            books.putAll((HashMap<String, Book>) dataSnapshot.getValue());

                            //delete all the books from the HashMap that doesn't belong to the current logged user

                            Iterator<String> bookIterator=books.keySet().iterator();

                            while (bookIterator.hasNext()) {
                                String key = bookIterator.next();
                                HashMap<String,String> book=(HashMap<String,String>)dataSnapshot.child(key).getValue();
                                if(!book.get("uid").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    bookIterator.remove();
                            }
                        }
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

    public static void insertBook(Book book,byte[] data){

        final String bookKey=ref.push().getKey();
        /*StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", bookKey)
                .build();*/
        UploadTask uploadTask = storageRef.child(bookKey).putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                ref.child(bookKey).child("image").setValue(downloadUrl);
            }
        });

        ref.child(bookKey).setValue(book);

        Log.d("book",book.toString());




    }

    public static void retrieveBookInfo(String bookSearchString){

        Log.d("debug","retrieveBookInfo");
        try{
        bookInfo=new GetBookInfo().execute(bookSearchString).get();
        }
        catch (Exception e){}

    }

}
