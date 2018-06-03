package it.polito.mad.koko.kokolab3.profile.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.polito.mad.koko.kokolab3.profile.Profile;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String uid;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String uid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.uid=uid;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TabProfile(uid);
            case 1:
                return new TabBooks(uid);
            case 2:
                return new TabComments(uid);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}