package it.polito.mad.koko.kokolab2.books;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.polito.mad.koko.kokolab2.R;

public class ShowBooks extends AppCompatActivity {

    private ListView bookListview;
    /*private Book book1=new Book(null,"ciao",null,null,null,null);
    private Book[] books={book1};*/

    private String[] books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                books=new String[(int)dataSnapshot.child("books").getChildrenCount()];
                int i=0;
                for(DataSnapshot bookSnapshot:dataSnapshot.child("books").getChildren()){
                    Book book = new Book();
                    book=bookSnapshot.getValue(Book.class);
                    books[i]=book.getTitle();
                    i++;

                }

                bookListview =(ListView)findViewById(R.id.books_listview);

                bookListview.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return books.length;
                    }

                    @Override
                    public Object getItem(int i) {
                        return books[i];
                    }

                    @Override
                    public long getItemId(int i) {
                        return 0;
                    }

                    @Override
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        if(view==null)
                            view=getLayoutInflater().inflate(R.layout.books_adapter_layout,viewGroup,false);
                        final int position=i;
                        TextView title=(TextView) view.findViewById(R.id.book_title);
                        Button show=(Button)view.findViewById(R.id.show_book_button);
                        title.setText(books[i]);
                        show.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("show", "Show button #"+position+"invoked");
                            }
                        });

                        return view;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}
