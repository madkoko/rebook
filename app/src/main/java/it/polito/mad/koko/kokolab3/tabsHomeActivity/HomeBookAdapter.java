package it.polito.mad.koko.kokolab3.tabsHomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.ShowBook;

public class HomeBookAdapter extends FirebaseRecyclerAdapter<Book, HomeBookAdapter.BookHolder>{


    private static final String TAG = "HomeBookAdapter";
    private final Activity activity;
    private int flag=0;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param activity
     */
    public HomeBookAdapter(@NonNull FirebaseRecyclerOptions options, int flag, Activity activity) {
        super(options);
        this.flag=flag;
        this.activity=activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull BookHolder holder, int position, @NonNull Book model) {
        if (flag == 0) {
            Picasso.get().load(model.getImage()).into(holder.coverBook);
        }else
            Picasso.get().load(model.getImage()).into(holder.coverBook);

        holder.coverBook.setOnClickListener((View v) ->{
            Log.d(TAG, String.valueOf(getRef(position).getKey()));
            Intent showBook = new Intent(activity, ShowBook.class);
            showBook.putExtra("book", model);
            activity.startActivity(showBook);
            //getRef(position).getKey()
        });
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_book_fragment, parent, false);
        return new BookHolder(view);
    }

    public class BookHolder extends RecyclerView.ViewHolder {
        public ImageView coverBook;

        public BookHolder(View view) {
            super(view);
            coverBook =  view.findViewById(R.id.book_cover);
        }
    }

}
