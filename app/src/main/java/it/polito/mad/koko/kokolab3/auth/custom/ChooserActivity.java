/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polito.mad.koko.kokolab3.auth.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.ui.BlurBuilder;
import it.polito.mad.koko.kokolab3.auth.provider.EmailPasswordActivity;
import it.polito.mad.koko.kokolab3.auth.provider.GoogleSignInActivity;
import it.polito.mad.koko.kokolab3.auth.provider.PhoneAuthActivity;

/**
 * Simple list-based Activity to redirect to one of the other Activities. This Activity does not
 * contain any useful code related to Firebase Authentication. You may want to start with
 * one of the following Files:
 *     {@link GoogleSignInActivity}
 *     {@link EmailPasswordActivity}
 *     {@link PhoneAuthActivity}
 */
public class ChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "ChooserActivity";

    /**
     * Authentication code needed from provider classes.
     */
    private static final int    AUTH_SUCCESS = 0,
                                AUTH_FAIL = -1;

    /**
     * Authentication code needed to {@link it.polito.mad.koko.kokolab3.HomeActivity}
     */
    private static final int    AUTH = 10;

    private static final Class[] CLASSES = new Class[]{
            GoogleSignInActivity.class,
            EmailPasswordActivity.class/*,
            PhoneAuthActivity.class*/
    };

    private static final int[] DESCRIPTION_IDS = new int[] {
            R.string.desc_google_sign_in,
            R.string.desc_emailpassword/*,
            R.string.desc_phone_auth*/
    };
    private View linearLayout;

    /**
     * Disabling the back button.
     */
    @Override
    public void onBackPressed() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_chooser);


        // Set up ListView and Adapter
        ListView listView = findViewById(R.id.list_view);

        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ///

        Bitmap resultBmp = BlurBuilder.blur(this, BitmapFactory.decodeResource(getResources(), R.mipmap.koko));
        Drawable drawable = new BitmapDrawable(getResources(), resultBmp);
        linearLayout =findViewById(R.id.lay);
        linearLayout.setBackground(drawable);


        ImageView imageView = findViewById(R.id.koko_logo);
        Picasso.get().load(R.mipmap.logo).into(imageView);
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class clicked = CLASSES[position];
        startActivityForResult(new Intent(this, clicked), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == AUTH_SUCCESS) {
            setResult(AUTH);
            finish();
        }
    }

    public class MyArrayAdapter extends ArrayAdapter<Class> {

        private Context mContext;
        private Class[] mClasses;
        private int[] mDescriptionIds;

        public MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);

            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chooser_adapter_layout, null);
            }
            Button chooser_activity = view.findViewById(R.id.chooser_button);
            //chooser_activity.setText(mClasses[position].getSimpleName());

            ImageView imageView = view.findViewById(R.id.chooser_imageView);
            if(position==0){
                imageView.setImageResource(R.drawable.fui_ic_googleg_color_24dp);
                chooser_activity.setText(R.string.label_google_sign_in);
            }else if(position==1){
                imageView.setImageResource(R.drawable.fui_ic_mail_white_24dp);
                chooser_activity.setText(R.string.label_emailpassword);
            }
            chooser_activity.setOnClickListener(v -> ChooserAc(position));

            //((TextView) view.findViewById(android.R.id.text1)).setText(mClasses[position].getSimpleName());
            //((TextView) view.findViewById(android.R.id.text2)).setText(mDescriptionIds[position]);

            return view;
        }


        private void ChooserAc(int position) {
            Class clicked = CLASSES[position];
            startActivityForResult(new Intent(getApplicationContext(), clicked), 1);
        }


        public void setDescriptionIds(int[] descriptionIds) {
            mDescriptionIds = descriptionIds;
        }
    }
}
