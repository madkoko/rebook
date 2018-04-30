package it.polito.mad.koko.kokolab3.books;

import android.content.Intent;
import android.service.autofill.SaveCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class ShowBooks extends AppCompatActivity {

    private static final String TAG = "ShowBooks";

    private int USER_BOOKS =0,SEARCH_BOOKS=2,requestCode;
    private ArrayList<Book> myBooks;
    private ProfileManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);
        pm=ProfileManager.getInstance();


        requestCode=getIntent().getIntExtra("request_code",-1);

        if(requestCode==USER_BOOKS)
            myBooks=BookManager.getUserBooks();
        else if(requestCode==SEARCH_BOOKS) {
            myBooks = BookManager.getSearchBooks();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ListView bookListView = findViewById(R.id.books_listview);

        // set the list view to show all the books

        if(myBooks!=null) {

            Log.d(TAG,"myBooks onStart ShowBooks"+myBooks.toString());

            bookListView.setAdapter(new BaseAdapter() {

                @Override
                public int getCount() {
                    return myBooks.size();
                }

                @Override
                public Object getItem(int i) {
                    return myBooks.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(final int i, View view, ViewGroup viewGroup) {
                    if (view == null)
                        view = getLayoutInflater().inflate(R.layout.books_adapter_layout, viewGroup, false);

                    TextView title = (TextView) view.findViewById(R.id.book_title);
                    ImageView photo=(ImageView) view.findViewById(R.id.book_photo);
                    title.setText(myBooks.get(i).getTitle());
                    Picasso.get().load(myBooks.get(i).getImage()).fit().centerCrop().into(photo);

                   if(requestCode==SEARCH_BOOKS){

                        TextView sharingUser =(TextView) view.findViewById(R.id.sharing_user);
                        String uid= myBooks.get(i).getUid();
                        sharingUser.setText("Shared by: "+pm.getProfile(uid).getName());

                    }

                    // start the activity "Show Book" passing the current book in the Intent

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent showBook = new Intent(getApplicationContext(), ShowBook.class);

                            showBook.putExtra("book", myBooks.get(i));
                            //showBook.putExtra("bookPhoto",bookVals.get("image"));
                            startActivity(showBook);



                        }
                    });
                    return view;
                }
            });
        }


    }

}
