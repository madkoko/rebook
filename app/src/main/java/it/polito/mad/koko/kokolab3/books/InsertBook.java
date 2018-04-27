package it.polito.mad.koko.kokolab3.books;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import it.polito.mad.koko.kokolab3.R;

public class InsertBook extends AppCompatActivity {

    private static final String TAG = "InsertBook";

    private EditText bookIsbn;
    private EditText bookTitle;
    private EditText bookAuthor;
    private EditText bookPublisher;
    private EditText bookEditionYear;
    private EditText bookConditions;
    private ImageView bookPhoto;
    private static final int CAMERA_REQUEST = 0;
    private static final int SCAN_BOOK_INFO = 1;

    private Uri imageRef;
    private Bitmap imageBitmap;
    private boolean flagCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);

        bookIsbn=findViewById(R.id.edit_book_ISBN);
        bookTitle=findViewById(R.id.edit_book_title);
        bookAuthor=findViewById(R.id.edit_book_author);
        bookPublisher=findViewById(R.id.edit_book_publisher);
        bookEditionYear=findViewById(R.id.edit_book_edition_year);
        bookConditions=findViewById(R.id.edit_book_conditions);
        bookPhoto=findViewById(R.id.insert_book_photo);


        ImageButton photoButton=findViewById(R.id.book_photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    // Launching the camera app
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }
        });

        Button addButton=findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBook(getIntent().getStringExtra("uid"));
            }
        });

        Button scanButton=findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanIntent= new Intent(getApplicationContext(),ScanISBNActivity.class);

                Log.d(TAG, "scan button pressed");
                startActivityForResult(scanIntent,SCAN_BOOK_INFO);
            }
        });

        Button searchButton=findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(bookIsbn.getText().length()));
                if(bookIsbn.getText().length()!=0) {
                    Log.d(TAG, "isbn: "+bookIsbn.getText().toString());
                    String bookSearchString = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+bookIsbn.getText().toString();
                    BookManager.retrieveBookInfo(bookSearchString);

                    Map<String,String> bookInfo=BookManager.getBookInfo();

                    if(bookInfo==null){
                        Toast.makeText(getApplicationContext(),"Insert a valid ISBN",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //bookIsbn.setText(bookInfo.get("isbn"));
                        bookTitle.setText(bookInfo.get("title"));
                        bookAuthor.setText(bookInfo.get("authors"));
                        bookPublisher.setText((bookInfo.get("publisher")));
                        bookEditionYear.setText(bookInfo.get("editionYear"));
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Insert a valid ISBN",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void createBook(String uid){

        String isbn=bookIsbn.getText().toString();
        String title=bookTitle.getText().toString();
        String author=bookAuthor.getText().toString();
        String publisher=bookPublisher.getText().toString();
        String editionYear=bookEditionYear.getText().toString();
        String conditions=bookConditions.getText().toString();

        bookPhoto.setDrawingCacheEnabled(true);
        bookPhoto.buildDrawingCache();
        Bitmap bitmap = bookPhoto.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] shown_image = baos.toByteArray();

        Book book=new Book(isbn,title,author,publisher,editionYear,conditions,uid,null);

        BookManager.insertBook(book,shown_image);

        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO debugging
        Log.d(TAG, "onActivityResult");

        // If the photo has been taken with the camera
        if(requestCode == CAMERA_REQUEST && resultCode != RESULT_CANCELED) {
            //Return uri from intent
            Bundle extras = data.getExtras();
            // TODO debugging
            Log.d(TAG, "camera");
            //create a new BitMap
            createImageFile(extras);
            // set flags for future state
            flagCamera=true;

            Bitmap tmp = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"temp");
            bookPhoto.setImageBitmap(tmp);

        }

        if(requestCode==SCAN_BOOK_INFO&&resultCode==RESULT_OK){

            Log.d(TAG, "onActivityResult: scan_book_info");

            String bookSearchString=data.getStringExtra("bookSearchString");

            BookManager.retrieveBookInfo(bookSearchString);

            Map<String,String> bookInfo=BookManager.getBookInfo();
            if(bookInfo!=null) {
                bookIsbn.setText(bookInfo.get("isbn"));
                bookTitle.setText(bookInfo.get("title"));
                bookAuthor.setText(bookInfo.get("authors"));
                bookPublisher.setText((bookInfo.get("publisher")));
                bookEditionYear.setText(bookInfo.get("editionYear"));
            }
            else{
                Toast.makeText(getApplicationContext(),"Insert a valid ISBN",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createImageFile(Bundle extras) {
        imageBitmap = (Bitmap) extras.get("data");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "temp");
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        }// The image cannot be created
        catch (IOException e) {
            // Creating an alert dialog indicating all possible causes
            AlertDialog.Builder builder = new AlertDialog.Builder(InsertBook.this);
            builder.setMessage(R.string.image_file_creation_error_message)
                    .setTitle(R.string.image_file_creation_error_title)
                    .setIcon(android.R.drawable.ic_dialog_alert);

            // Showing the dialog to the screen
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
