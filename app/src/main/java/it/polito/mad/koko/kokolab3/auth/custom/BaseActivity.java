package it.polito.mad.koko.kokolab3.auth.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import it.polito.mad.koko.kokolab3.R;

public class BaseActivity extends AppCompatActivity {

    /**
     * Authentication code needed to {@link it.polito.mad.koko.kokolab3.auth.custom.ChooserActivity}
     */
    protected static final int  AUTH_SUCCESS = 0,
                                AUTH_FAIL = -1;

    private static final String TAG = "BaseActivity";

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    /**
     * Pressing the back button makes the authentication fail.
     */
    @Override
    public void onBackPressed() {
        setResult(AUTH_FAIL);
        finish();
    }
}
