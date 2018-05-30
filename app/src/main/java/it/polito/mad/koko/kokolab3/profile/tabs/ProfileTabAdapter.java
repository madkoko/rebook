package it.polito.mad.koko.kokolab3.profile.tabs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import it.polito.mad.koko.kokolab3.R;
import it.polito.mad.koko.kokolab3.profile.Profile;
import it.polito.mad.koko.kokolab3.profile.ProfileManager;

public class ProfileTabAdapter extends BaseAdapter {

    private final Context context;
    private final Profile profile;
    private TextView value;
    private TextView key;
    private ImageView imageView;

    public ProfileTabAdapter(Context context){
        this.context=context;
        profile = ProfileManager.getProfile();


    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return position;
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
            convertView = mInflater.inflate(R.layout.adapter_tab_profile, parent, false);

        value = convertView.findViewById(R.id.value);
        key = convertView.findViewById(R.id.key);
        imageView = convertView.findViewById(R.id.imageViewProfileAd);

        if(position==0){
            value.setText(profile.getEmail());
            key.setText(R.string.user_email);
            imageView.setImageResource(R.drawable.ic_menu_send);
        }else if(position==1){
            value.setText(profile.getLocation());
            key.setText(R.string.user_location);
            imageView.setImageResource(R.mipmap.position);
        }if(position==2){
            value.setText(profile.getBio());
            key.setText(R.string.user_bio);
            imageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
        }
        return convertView;
    }
}
