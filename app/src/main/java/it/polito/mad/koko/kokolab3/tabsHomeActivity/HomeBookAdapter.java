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
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.books.Book;
import it.polito.mad.koko.kokolab3.books.ShowBook;
import it.polito.mad.koko.kokolab3.messaging.MessageManager;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class HomeBookAdapter extends FirebaseRecyclerAdapter<Book, HomeBookAdapter.BookHolder>{


    private static final String TAG = "HomeBookAdapter";
    private final Activity activity;
    private int flag=0;
    private ProfileManager pm;


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
            Boolean chatFlag = false;
            Intent i = getChatInfo(model);
            MessageManager.createChat(i, model.getTitle(), chatFlag);
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

    private Intent getChatInfo(Book book) {

        Intent i = new Intent();
        pm = ProfileManager.getInstance();

        // Sender Info
        String senderId = FirebaseAuth.getInstance().getUid();
        Profile senderProfile = pm.getProfile(senderId);
        String senderUsername = senderProfile.getName();
        String senderImage = senderProfile.getImage();
        String senderToken = senderProfile.getTokenMessage();

        // Receiver Info
        String receiverId = book.getUid();
        Profile receiverProfile = pm.getProfile(receiverId);
        String receiverUsername = receiverProfile.getName();
        String receiverImage = receiverProfile.getImage();
        String receiverToken = receiverProfile.getTokenMessage();


        // 2. Put Sender & Receiver info into Intent
        i.putExtra("senderId", senderId);
        i.putExtra("senderUsername", senderUsername);
        i.putExtra("senderImage", senderImage);
        i.putExtra("senderToken", senderToken);
        i.putExtra("receiverId", receiverId);
        i.putExtra("receiverUsername", receiverUsername);
        i.putExtra("receiverImage", receiverImage);
        i.putExtra("receiverToken", receiverToken);

        return i;
    }


}
