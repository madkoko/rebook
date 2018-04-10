package it.polito.mad.koko.kokolab2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShowProfile extends AppCompatActivity {

    /**
     * User profile data.
     */
    private static String user_photo_uri = null;
    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_location;
    private TextView tv_bio;
    private ImageView user_photo;
    private Intent i;

    /**
     * User profile data is stored in a firebase database.
     */
    private DatabaseReference mDatabase;
    private String mFirebaseUser;


    /**
     * Instantiating the activity for the first time.
     * @param savedInstanceState    activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loading the XML layout file
        setContentView(R.layout.activity_show_profile);


        //Loading UserID from intent
        i = getIntent();
        mFirebaseUser= i.getExtras().getString("UserID");




        // Restoring UI fields containing user info
        tv_name=findViewById(R.id.user_name);
        tv_email=findViewById(R.id.user_email);
        tv_location=findViewById(R.id.user_location);
        tv_bio=findViewById(R.id.user_bio);
        user_photo=findViewById(R.id.user_photo);
    }

    /**
     * Setting the edit profile button in the title bar.
     * @param menu      menu object to be instantiated.
     * @return          whether the menu object has been inserted or not.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * Handling the menu item selection.
     * @param item      item that has been selected.
     * @return          whether the menu item has been handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            // Profile editing
            case R.id.edit:
                //Intent i = new Intent(getApplicationContext(), EditProfile.class);

                //SharedPreferences.Editor editor = sharedPreferences.edit();

                // If there previously was a profile pic
                if(user_photo_uri != null){}
                    // Restore it
                    //editor.putString("user_photo_temp", user_photo_uri);
                else{}
                    // Default profile pic
                    //editor.putString("user_photo_temp", String.valueOf(R.mipmap.ic_launcher_round));

                // Writing in the sharedPreferences data structure containing user info
                //editor.apply();

                // Launching the editing profile activity
                //startActivity(i);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Filling all the UI fields retrieving all the needed information from
     * firebase.
     */
    @Override
    protected void onResume() {
        super.onResume();


        //Loading firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        User u=new User(mFirebaseUser,mDatabase);

        // Updating the sharedPreferences data structure containing user info
        //sharedPreferences = getApplicationContext().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        // Filling UI elements

        DatabaseReference nameReference = mDatabase.child("users").child(mFirebaseUser);
        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tv_name.setText(dataSnapshot.child("name").getValue(String.class));
                //Log.d("TAG",dataSnapshot.getValue(String.class));
            }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                };

            });
        /*

        // If there was not a profile pic previously
        if(sharedPreferences.getString("user_photo",null) == null)
            // Default profile pic
            Picasso.get().load(R.mipmap.ic_launcher_round).fit().centerCrop().into(user_photo);
        else {
            // Retrieving the previous profile pic URI
            user_photo_uri = sharedPreferences.getString("user_photo", null);

            // Displaying it again
            Picasso.get().load(sharedPreferences.getString("user_photo", null)).fit().centerCrop().into(user_photo);
        }
        */

    }
}

