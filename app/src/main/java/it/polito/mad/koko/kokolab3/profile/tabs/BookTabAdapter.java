package it.polito.mad.koko.kokolab3.profile.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.ShowBook;

public class BookTabAdapter extends FirebaseListAdapter<Book> {

    private Context context;

    public BookTabAdapter(@NonNull FirebaseListOptions<Book> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void populateView(View view, Book model, int position) {

        ImageView coverBook = (ImageView) view.findViewById(R.id.my_book_photo);
        TextView titleBook = (TextView) view.findViewById(R.id.my_book_title);

        titleBook.setText(model.getTitle());
        Picasso.get().load(model.getImage()).into(coverBook);

        view.setOnClickListener(v -> {
            Intent showBook = new Intent(context, ShowBook.class);
            showBook.putExtra("book", model);
            context.startActivity(showBook);
        });
    }
}