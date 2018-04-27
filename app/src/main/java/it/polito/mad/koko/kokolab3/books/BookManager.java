package it.polito.mad.koko.kokolab3.books;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by Francesco on 13/04/2018.
 */

public class BookManager {

    private static final String TAG = "BookManager";

    private static DatabaseReference ref;
    private static Map<String,Book> books=null;
    private static StorageReference storageRef;
    /**
     * Firebase instance
     */
    private static DatabaseReference booksDatabaseRef;
    private static StorageReference booksStorageRef;

    /**
     * Books managing
     */
    private static String downloadUrl;
    private static Map<String,String> bookInfo;
    private static ArrayList<Book> userBooks;
    private static ArrayList<Book> searchBooks;
    private static ChildEventListener userBooksEventListener;
    private static ChildEventListener searchBooksEventListener;


    public BookManager(){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        booksDatabaseRef = database.getReference().child("books");
        FirebaseStorage storage=FirebaseStorage.getInstance();
        booksStorageRef = storage.getReference().child("books");

        setUserBooksEventListener();
        setSearchBooksEventListener();
    }

    /**
     * Methods to manage the current user books
     */

    private void setUserBooksEventListener(){
        userBooksEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + s);
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "datasnapshot: " + dataSnapshot.getValue().toString());

                    Book newBook = new Book();
                    newBook.setTitle(((HashMap<String, String>) dataSnapshot.getValue()).get("title"));
                    newBook.setEditionYear(((HashMap<String, String>) dataSnapshot.getValue()).get("editionYear"));
                    newBook.setBookConditions(((HashMap<String, String>) dataSnapshot.getValue()).get("bookConditions"));
                    newBook.setUid(((HashMap<String, String>) dataSnapshot.getValue()).get("uid"));
                    newBook.setISBN(((HashMap<String, String>) dataSnapshot.getValue()).get("isbn"));
                    newBook.setPublisher(((HashMap<String, String>) dataSnapshot.getValue()).get("publisher"));
                    newBook.setAuthor(((HashMap<String, String>) dataSnapshot.getValue()).get("author"));
                    newBook.setImage(((HashMap<String, String>) dataSnapshot.getValue()).get("image"));

                    userBooks.add(newBook);

                    Log.d(TAG, "My books are: " + userBooks.toString());
                }
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

    public static void removeUserBooksEventListener(){

        booksDatabaseRef.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(userBooksEventListener);

    }

    public static void populateUserBookList(){
        userBooks=new ArrayList<>();
        userBooks.clear();
        booksDatabaseRef.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(userBooksEventListener);

    }

    public static ArrayList<Book> getUserBooks(){return userBooks;}


    /**
     * Methods to manage the book searching
     */
    private void setSearchBooksEventListener(){
        searchBooksEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + s);
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "datasnapshot: " + dataSnapshot.getValue().toString());

                    Book newBook = new Book();
                    newBook.setTitle(((HashMap<String, String>) dataSnapshot.getValue()).get("title"));
                    newBook.setEditionYear(((HashMap<String, String>) dataSnapshot.getValue()).get("editionYear"));
                    newBook.setBookConditions(((HashMap<String, String>) dataSnapshot.getValue()).get("bookConditions"));
                    newBook.setUid(((HashMap<String, String>) dataSnapshot.getValue()).get("uid"));
                    newBook.setISBN(((HashMap<String, String>) dataSnapshot.getValue()).get("isbn"));
                    newBook.setPublisher(((HashMap<String, String>) dataSnapshot.getValue()).get("publisher"));
                    newBook.setAuthor(((HashMap<String, String>) dataSnapshot.getValue()).get("author"));
                    newBook.setImage(((HashMap<String, String>) dataSnapshot.getValue()).get("image"));

                    searchBooks.add(newBook);

                    Log.d(TAG, "My search books are: " + searchBooks.toString());
                }
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

    public static void populateSearchBooks(Book book){

        searchBooks=new ArrayList<>();
        searchBooks.clear();
        String title,author,publisher,editionYear,conditions;
        title=book.getTitle();
        author=book.getAuthor();
        publisher=book.getPublisher();
        editionYear=book.getEditionYear();
        conditions=book.getBookConditions();

        Query searchBooksDatabaseRef=booksDatabaseRef;



        if(title.length()!=0)
            searchBooksDatabaseRef=searchBooksDatabaseRef.orderByChild("title").equalTo(title);
        if(author.length()!=0)
            searchBooksDatabaseRef=searchBooksDatabaseRef.orderByChild("author").equalTo(author);
        if(publisher.length()!=0)
            searchBooksDatabaseRef=searchBooksDatabaseRef.orderByChild("publisher").equalTo(publisher);
        if(editionYear.length()!=0)
            searchBooksDatabaseRef=searchBooksDatabaseRef.orderByChild("editionYear").equalTo(editionYear);
        if(conditions.length()!=0)
            searchBooksDatabaseRef=searchBooksDatabaseRef.orderByChild("conditions").equalTo(conditions);

        searchBooksDatabaseRef.addChildEventListener(searchBooksEventListener);
    }

    public static void removeSearchBooksEventListener(){
        booksDatabaseRef.removeEventListener(searchBooksEventListener);
    }

    public static ArrayList<Book> getSearchBooks(){ return searchBooks; }


    /**
     *
     * @param scanBookInfo
     *
     * Get Book info after scanning or inserting the ISBN code
     */

    public static void setBookInfo(Map<String,String> scanBookInfo){
        Log.d(TAG,"setBookInfo");
        bookInfo=scanBookInfo;
    }
    public static Map<String,String> getBookInfo(){
        Log.d(TAG,"getBookInfo");
        //Log.d("debug",bookInfo.toString());
        return bookInfo;
    }

    public static void retrieveBookInfo(String bookSearchString){

        Log.d(TAG,"retrieveBookInfo");
        try{
        bookInfo=new GetBookInfo().execute(bookSearchString).get();
        }
        catch (Exception e){}

    }

    /**
     *
     * @param book
     * @param data
     *
     * Insert new Book into Firebase
     */

    public static void insertBook(final Book book,byte[] data){

        final String bookKey=booksDatabaseRef.push().getKey();
        /*StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", bookKey)
                .build();*/
        UploadTask uploadTask = booksStorageRef.child(bookKey).putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                //Log.d(TAG,downloadUrl);
                book.setImage(downloadUrl);
                //ref.child(bookKey).child("image").setValue(downloadUrl);
                booksDatabaseRef.child(bookKey).setValue(book);

            }
        });

        Log.d(TAG, book.toString());


    }


     /*public static void printBooks(){
        Log.d(TAG,books.size()+" "+books.toString());
    }

    public static Map<String,String> getBooks(){
        return books;
    }*/

}
