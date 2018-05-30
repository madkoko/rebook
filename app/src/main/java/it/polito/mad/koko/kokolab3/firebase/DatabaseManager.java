package it.polito.mad.koko.kokolab3.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    /**
     * Firebase database reference
     */
    private static DatabaseReference database;

    /**
     * Initializing Firebase variables
     */
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * It updates an entry value in Firebase.
     * The first N-1 arguments represent the tree path key to be modified: if it
     * does not exist, it will be created.
     * The very last argument represents the value to be set.
     * @param value     value to be set.
     * @param path      entry path.
     */
    public static void set(Object value, String path) {
        if(path == null || path.isEmpty() || value == null)
            throw new IllegalArgumentException();

        get(path).setValue(value);
    }

    public static void set(Object value, String... path) {
        set(value, createPath(path));
    }

    /**
     * It deletes the Firebase entry whose path is specified by
     * the arguments.
     * @param path  path of the Firebase entry to be deleted.
     */
    public static void delete(String path) {
        get(path).removeValue();
    }

    public static void delete(String... path) {
        delete(createPath(path));
    }

    /**
     * It returns a Firebase child given its path.
     * @param path  the child's path.
     * @return      the requested Firebase child.
     */
    public static DatabaseReference get(String path) {
        return database.child(path);
    }

    public static DatabaseReference get(String... path) {
        return get(createPath(path));
    }

    /**
     * It returns a Firebase key path using the right format.
     * @param path              a list of Strings representing the path.
     * @return                  the complete key path in a single String.
     */
    private static String createPath(String... path) {
        if(path == null || path.length == 0)
            throw new IllegalArgumentException();

        String result = "";

        for(int i = 0; i <= path.length - 1; ++i)
            result += path[i] + ((i != path.length - 1) ? "/" : "");

        // TODO debugging
        Log.d(TAG, "Path: " + result);

        return result;
    }
}
