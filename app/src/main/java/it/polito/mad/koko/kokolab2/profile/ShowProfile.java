package it.polito.mad.koko.kokolab2.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab2.R;

public class ShowProfile extends AppCompatActivity {

    /**
     * Profile profile data.
     */
    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_location;
    private TextView tv_bio;
    private ImageView user_photo;
    private Intent i;

    /**
     * Profile profile data is stored in a firebase database.
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
     *
     * Setting the edit profile button in the title bar.
     * @param menu      menu object to be instantiated.
     * @return true, whether the menu object has been inserted or not.
     * @return false, Auth user != user from intent
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mFirebaseUser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
            return true;
        }
        return false;
    }

    /**
     * Handling the menu item selection.
     * @param item      item that has been selected.
     * @return          whether the menu item has been handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Profile editing
            case R.id.edit:
                //Only if Auth user is equal to user from intent, we can use this menu
                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(i);

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

        // Filling UI elements
        DatabaseReference nameReference = mDatabase.child("users").child(mFirebaseUser);
        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tv_name.setText(dataSnapshot.child("name").getValue(String.class));
                tv_email.setText(dataSnapshot.child("email").getValue(String.class));
                tv_location.setText(dataSnapshot.child("location").getValue(String.class));
                tv_bio.setText(dataSnapshot.child("bio").getValue(String.class));
                Picasso.get().load(dataSnapshot.child("image").getValue(String.class)).fit().centerCrop().into(user_photo);

            }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                };

            });
    }
}

