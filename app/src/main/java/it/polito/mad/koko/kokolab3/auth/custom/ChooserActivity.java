/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import it.polito.mad.koko.kokolab3.auth.provider.AnonymousAuthActivity;
import it.polito.mad.koko.kokolab3.auth.provider.CustomAuthActivity;
import it.polito.mad.koko.kokolab3.auth.provider.FacebookLoginActivity;
import it.polito.mad.koko.kokolab3.auth.provider.FirebaseUIActivity;
// import it.polito.mad.koko.kokolab3.auth.provider.PasswordlessActivity;
import it.polito.mad.koko.kokolab3.auth.provider.TwitterLoginActivity;
import it.polito.mad.koko.kokolab3.ui.BlurBuilder;
import it.polito.mad.koko.kokolab3.auth.provider.EmailPasswordActivity;
import it.polito.mad.koko.kokolab3.auth.provider.GoogleSignInActivity;
import it.polito.mad.koko.kokolab3.auth.provider.PhoneAuthActivity;

/**
 * Simple list-based Activity to redirect to one of the other Activities. This Activity does not
 * contain any useful code related to Firebase Authentication. You may want to start with
 * one of the following Files:
 * {@link GoogleSignInActivity}
 * {@link EmailPasswordActivity}
 */
public class ChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "ChooserActivity";

    /**
     * Authentication code needed from provider classes.
     */
    private static final int AUTH_SUCCESS = 0,
            AUTH_FAIL = -1;

    /**
     * Authentication code needed to {@link it.polito.mad.koko.kokolab3.HomeActivity}
     */
    private static final int AUTH = 10;

    private static final Class[] CLASSES = new Class[]{
            // Basic
            GoogleSignInActivity.class,
            EmailPasswordActivity.class,

            // Social
            FacebookLoginActivity.class,
            //TwitterLoginActivity.class,

            // Not included in the free Spark plan
            // PhoneAuthActivity.class,

            // Useless
            /*AnonymousAuthActivity.class,
            FirebaseUIActivity.class,
            CustomAuthActivity.class,*/

            // Does not work with firebase-auth:15.0.0
            // PasswordlessActivity.class
    };

    private static final int[] LABEL_IDS = new int[]{
            // Basic
            R.string.label_google_sign_in,
            R.string.label_emailpassword,

            // Social
            R.string.label_facebook_login,
            R.string.label_twitter_login,

            // Not included in the free Spark plan
            R.string.label_phone_auth,

            // Useless
            R.string.label_anonymous_auth,
            R.string.label_firebase_ui,
            R.string.label_custom_auth,

            // Does not work with firebase-auth:15.0.0
            R.string.label_passwordless
    };

    private static final int[] DESCRIPTION_IDS = new int[]{
            // Basic
            R.string.desc_google_sign_in,
            R.string.desc_emailpassword,

            // Social
            R.string.desc_facebook_login,
            R.string.desc_twitter_login,

            // Not included in the free Spark plan
            R.string.desc_phone_auth,

            // Useless
            R.string.desc_anonymous_auth,
            R.string.desc_firebase_ui,
            R.string.desc_custom_auth,

            // Does not work with firebase-auth:15.0.0
            R.string.desc_passwordless
    };

    private static final int[] ICON_IDS = new int[]{
            // Basic
            R.drawable.fui_ic_googleg_color_24dp,
            R.drawable.fui_ic_mail_white_24dp,

            // Social
            R.drawable.fui_ic_facebook_white_22dp,
            R.drawable.fui_ic_twitter_bird_white_24dp,

            // Not included in the free Spark plan
            R.drawable.fui_ic_phone_white_24dp,

            // Useless
            R.drawable.fui_ic_phone_white_24dp,
            R.drawable.fui_ic_mail_white_24dp,
            R.drawable.fui_ic_mail_white_24dp,

            // Does not work with firebase-auth:15.0.0
            R.drawable.fui_ic_phone_white_24dp
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
        linearLayout = findViewById(R.id.lay);
        linearLayout.setBackground(drawable);


        ImageView imageView = findViewById(R.id.koko_logo);
        Picasso.get().load(R.mipmap.logo).into(imageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class clicked = CLASSES[position];
        startActivityForResult(new Intent(this, clicked), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AUTH_SUCCESS) {
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

            imageView.setImageResource(ICON_IDS[position]);
            chooser_activity.setText(LABEL_IDS[position]);

            chooser_activity.setOnClickListener(v -> ChooserAc(position));

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
