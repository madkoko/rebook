package it.polito.mad.koko.kokolab3.util;

import android.app.AlertDialog;
import android.content.Context;

import it.polito.mad.koko.kokolab3.R;

public class AlertManager {
    /**
     * It shows an alert dialog concerning lack of permissions
     * or full storage.
     * @param context   in which context the dialog should be shown.
     */
    public static final void permissionDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.image_file_creation_error_message)
                .setTitle(R.string.image_file_creation_error_title)
                .setIcon(android.R.drawable.ic_dialog_alert);

        // Showing the dialog to the screen
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}
