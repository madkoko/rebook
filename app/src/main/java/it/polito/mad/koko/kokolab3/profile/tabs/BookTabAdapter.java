package it.polito.mad.koko.kokolab3.profile.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.BookManager;
import it.polito.mad.koko.kokolab3.books.EditBook;
import it.polito.mad.koko.kokolab3.books.ShowBook;
import it.polito.mad.koko.kokolab3.books.ShowBooks;
import it.polito.mad.koko.kokolab3.firebase.OnGetDataListener;

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

        Button deleteBookButton = (Button) view.findViewById(R.id.delete_my_book);

        // set the listener to the delete button with an alert dialog
        deleteBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //check if the book is borrowed checking the child "sharable"
                //if is set to "no" the book is borrowed and it can't be deleted
                BookManager.isSharable(getRef(position).getKey(), new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot data) {
                        if (data.exists()) {
                            if (data.getValue().toString().compareToIgnoreCase("no") == 0) {
                                Toast.makeText(context, "You can't delete a book that has been shared!", Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(context);
                                }
                                builder.setTitle("Delete Book")
                                        .setMessage("Are you sure you want to delete this book?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                BookManager.removeBook(String.valueOf(getRef(position).getKey()));
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onFailed(DatabaseError databaseError) {

                    }
                });
            }
        });

        view.setOnClickListener(v -> {
            Intent editBook = new Intent(context, EditBook.class);

            editBook.putExtra("updatingBook", model);
            editBook.putExtra("bookKey", getRef(position).getKey());
            //showBook.putExtra("bookPhoto",bookVals.get("image"));
            context.startActivity(editBook);

        });
    }
}