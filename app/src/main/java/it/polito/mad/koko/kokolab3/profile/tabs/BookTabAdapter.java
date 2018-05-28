package it.polito.mad.koko.kokolab3.profile.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.ShowBook;

public class BookTabAdapter extends BaseAdapter {


    private final ArrayList<Book> myBooks;
    private Context context;

    public BookTabAdapter(ArrayList<Book> myBooks, Context context){
        this.myBooks=myBooks;
        this.context=context;

    }
    @Override
    public int getCount() {
        return myBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return myBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.my_books_adapter_layout, parent, false);

        TextView title =  convertView.findViewById(R.id.my_book_title);
        ImageView photo =  convertView.findViewById(R.id.my_book_photo);
        title.setText(myBooks.get(position).getTitle());
        Picasso.get().load(myBooks.get(position).getImage()).fit().centerCrop().into(photo);
        convertView.setOnClickListener(v -> {
            Intent showBook = new Intent(context, ShowBook.class);
            showBook.putExtra("book", myBooks.get(position));
            context.startActivity(showBook);
        });
        return convertView;
    }
}
