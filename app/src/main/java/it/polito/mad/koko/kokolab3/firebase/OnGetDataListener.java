package it.polito.mad.koko.kokolab3.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface OnGetDataListener {
    void onStart();
    void onSuccess(DataSnapshot data);
    void onFailed(DatabaseError databaseError);
}
