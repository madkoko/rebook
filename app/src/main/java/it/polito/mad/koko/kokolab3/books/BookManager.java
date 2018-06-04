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
import java.util.regex.Pattern;

import it.polito.mad.koko.kokolab3.firebase.DatabaseManager;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

/**
 * Created by Francesco on 13/04/2018.
 */

public class BookManager {

    private static final String TAG = "BookManager";

    /**
     * Firebase instance
     */
    private static DatabaseReference booksDatabaseRef;
    private static StorageReference booksStorageRef;

    /**
     * Books managing
     */
    private static String downloadUrl;

    /**
     * book informations from ISBN scan
     */
    private static Map<String, String> bookInfo;

    /**
     * Arraylist with all the books in Firebase
     */
    private static Map<String, Book> allBooks;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        booksDatabaseRef = DatabaseManager.get("books");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        booksStorageRef = storage.getReference().child("books");
    }

    /**
     * Returns true when {@code source} contains at least a case-insensitive
     * occurrence of {@code pattern}
     *
     * @param source  the string that has to be tested.
     * @param pattern the pattern that is looked for.
     * @return true when an occurrence is found.
     */
    public static boolean containsCaseInsensitive(String source, String pattern) {
        return
                Pattern.compile(
                        Pattern.quote(pattern),
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
                )
                        .matcher(source)
                        .find()
                ;
    }

    /**
     * @param scanBookInfo Get Book info after scanning or inserting the ISBN code
     */
    public static void setBookInfo(Map<String, String> scanBookInfo) {
        Log.d(TAG, "setBookInfo");
        bookInfo = scanBookInfo;
    }

    /**
     * @return Book informations from the isbn scan
     */
    public static Map<String, String> getBookInfo() {
        Log.d(TAG, "getBookInfo");
        //Log.d("debug",bookInfo.toString());
        return bookInfo;
    }

    /**
     * retrieve all the book informations from the google API
     *
     * @param bookSearchString query to the google API in which we append the ISBN
     */
    public static void retrieveBookInfo(String bookSearchString) {

        Log.d(TAG, "retrieveBookInfo");
        try {
            bookInfo = new GetBookInfo().execute(bookSearchString).get();
        } catch (Exception e) {
        }
    }

    /**
     * @param book
     * @param data Insert new Book into Firebase
     */
    public static void insertBook(final Book book, byte[] data) {

        final String bookKey = booksDatabaseRef.push().getKey();

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

    /**
     * Create and attach the listener to the child "books" in firebase to retrieve all the books
     */
    public static void populateAllBooks() {

        booksDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allBooks = new HashMap<>();
                if (dataSnapshot.exists()) {

                    // Retrieve all the books from Firebase
                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        Book book = bookSnapshot.getValue(Book.class);
                        allBooks.put(bookSnapshot.getKey(), book);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * @return Arraylist with all the books in Firebase
     */
    public static Map<String, Book> getAllBooks() {

        return allBooks;
    }

    /**
     * Method to remove a book from Firebase
     *
     * @param bookId id of the book to be removed
     */
    public static void removeBook(String bookId) {
        booksDatabaseRef.child(bookId).removeValue();
    }

    /**
     * Method to update in Firebase when is edited by the owner
     *
     * @param bookId     Id of the book to be updated
     * @param bookValues Map with all the values to be updated in Firebase
     */
    public static void updateBook(String bookId, Map<String, Object> bookValues, byte[] data) {

        Map<String, Object> bookUpdates = new HashMap<>();


        UploadTask uploadTask = booksStorageRef.child(bookId).putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                //Log.d(TAG,downloadUrl);
                bookValues.put("image", downloadUrl);
                bookUpdates.put(bookId, bookValues);
                //ref.child(bookKey).child("image").setValue(downloadUrl);
                booksDatabaseRef.updateChildren(bookUpdates);
            }
        });

    }
}
