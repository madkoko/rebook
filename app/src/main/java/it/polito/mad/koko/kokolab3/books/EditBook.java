package it.polito.mad.koko.kokolab3.books;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.util.AlertManager;

public class EditBook extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 0;

    private static final String TAG = "EditBook";

    private ImageView editBookPhoto;
    private EditText editBookIsbn;
    private EditText editBookTitle;
    private EditText editBookAuthor;
    private EditText editBookPublisher;
    private EditText editBookEditionYear;
    private EditText editBookConditions;

    private Uri imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        Book updatingBook = (Book) getIntent().getExtras().get("updatingBook");
        final String bookKey = getIntent().getStringExtra("bookKey");

        editBookPhoto = (ImageView) findViewById(R.id.edit_book_photo);

        editBookIsbn = (EditText) findViewById(R.id.edit_book_isbn);
        editBookTitle = (EditText) findViewById(R.id.edit_book_title);
        editBookAuthor = (EditText) findViewById(R.id.edit_book_author);
        editBookPublisher = (EditText) findViewById(R.id.edit_book_publisher);
        editBookEditionYear = (EditText) findViewById(R.id.edit_book_edition_year);
        editBookConditions = (EditText) findViewById(R.id.edit_book_conditions);

        Button saveEditButton = (Button) findViewById(R.id.save_edit_book);
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBook(bookKey,updatingBook);
            }
        });
        Picasso.get().load(updatingBook.getImage()).fit().centerCrop().into(editBookPhoto);

        editBookIsbn.setText(updatingBook.getISBN());
        editBookTitle.setText(updatingBook.getTitle());
        editBookAuthor.setText(updatingBook.getAuthor());
        editBookPublisher.setText(updatingBook.getPublisher());
        editBookEditionYear.setText(updatingBook.getEditionYear());
        editBookConditions.setText(updatingBook.getBookConditions());

        ImageButton editBookPhotoButton = (ImageButton) findViewById(R.id.edit_book_photo_button);
        editBookPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Requested from Android 7.0 Nougat
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    try {
                        // Launching the camera app
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    } catch (Exception e) {
                        // Creating an alert dialog indicating all possible causes
                        AlertManager.permissionDialog(EditBook.this);
                    }
                }
            }
        });


    }

    /**
     * Called as a result of the new profile picture activity from camera action
     *
     * @param requestCode where the profile pic has been acquired.
     * @param resultCode  whether the operation has been performed successfully or not.
     * @param data        an Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO debugging
        Log.d(TAG, "onActivityResult");

        // If the photo has been taken with the camera
        if (requestCode == CAMERA_REQUEST && resultCode != RESULT_CANCELED) {
            //Return uri from intent
            Bundle extras = data.getExtras();
            // TODO debugging
            Log.d(TAG, "camera");
            //create a new BitMap
            createImageFile(extras);

            Bitmap tmp = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/book_temp");
            editBookPhoto.setImageBitmap(tmp);

        }

    }

    /**
     * Upon taking a picture with the camera, it saves the Bitmap image.
     *
     * @param extras bundle with data from startDialog intent
     */
    private void createImageFile(Bundle extras) {
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/book_temp");
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        }// The image cannot be created
        catch (IOException e) {
            // Creating an alert dialog indicating all possible causes
            AlertDialog.Builder builder = new AlertDialog.Builder(EditBook.this);
            builder.setMessage(R.string.image_file_creation_error_message)
                    .setTitle(R.string.image_file_creation_error_title)
                    .setIcon(android.R.drawable.ic_dialog_alert);

            // Showing the dialog to the screen
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * Method to update the book in Firebase
     *
     * @param bookId Id of the book to be updated
     */
    private void updateBook(String bookId,Book updatingBook) {

        editBookPhoto.setDrawingCacheEnabled(true);
        editBookPhoto.buildDrawingCache();
        Bitmap bitmap = editBookPhoto.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] shown_image = baos.toByteArray();

        Map<String, Object> bookValues = new HashMap<>();
        bookValues.put("isbn", editBookIsbn.getText().toString());
        bookValues.put("title", editBookTitle.getText().toString());
        bookValues.put("author", editBookAuthor.getText().toString());
        bookValues.put("publisher", editBookPublisher.getText().toString());
        bookValues.put("editionYear", editBookEditionYear.getText().toString());
        bookValues.put("bookConditions", editBookConditions.getText().toString());
        bookValues.put("uid",updatingBook.getUid());
        bookValues.put("sharable",updatingBook.getSharable());
        bookValues.put("bookOwner",updatingBook.getBookOwner());

        BookManager.updateBook(bookId, bookValues, shown_image);

        finish();
    }

}
