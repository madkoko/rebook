package it.polito.mad.koko.kokolab3.util;

import android.app.AlertDialog;
import android.content.Context;

import it.polito.mad.koko.kokolab3.R;

public class AlertManager {

    private static final int
            // Lack of permissions
            PERMISSION_TITLE = R.string.image_file_creation_error_title,
            PERMISSION_MESSAGE = R.string.image_file_creation_error_message,

            // User no longer exists
            NO_USER_TITLE = R.string.no_user_error_title,
            NO_USER_MESSAGE = R.string.no_user_error_message;

    /**
     * It shows an alert dialog concerning lack of permissions
     * or full storage.
     * @param context   context in which context the dialog should be shown.
     */
    public static final void permissionDialog(Context context) {
        dialog(context, PERMISSION_TITLE, PERMISSION_MESSAGE);
    }

    /**
     * It shows an alert dialog concerning the absence of a user.
     * @param context   context in which context the dialog should be shown.
     */
    public static final void noUserDialog(Context context) {
        dialog(context, NO_USER_TITLE, NO_USER_MESSAGE);
    }

    /**
     * It shows a generic alert dialog.
     * @param context   context in which context the dialog should be shown.
     * @param title     alert dialog title.
     * @param message   alert dialog messsage.
     */
    private static final void dialog(Context context, int title, int message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert);

        // Showing the dialog to the screen
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}
