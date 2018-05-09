package it.polito.mad.koko.kokolab3.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;

import it.polito.mad.koko.kokolab3.ui.chat.common.data.fixtures.DialogsFixtures;
import it.polito.mad.koko.kokolab3.ui.chat.common.data.model.Dialog;
import it.polito.mad.koko.kokolab3.ui.chat.common.data.model.Message;
import it.polito.mad.koko.kokolab3.R;

public class DefaultDialogsActivity extends DemoDialogsActivity {

    private ArrayList<Dialog> dialogs;

    public static void open(Context context) {
        context.startActivity(new Intent(context, DefaultDialogsActivity.class));
    }

    private DialogsList dialogsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_dialogs);

        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        initAdapter();
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        DefaultMessagesActivity.open(this);
    }

    private void initAdapter() {
        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
        super.dialogsAdapter.setItems(DialogsFixtures.getDialogs());

        super.dialogsAdapter.setOnDialogClickListener(this);
        super.dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(super.dialogsAdapter);
    }

    //for example
    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsAdapter.addItem(dialog);
    }
}
