package in.co.nerdoo.chittichat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import retrofit2.Retrofit;

/**
 * Created by shubham on 10/12/16.
 */
public class GroupAddRequestDialog extends DialogFragment {
    @Inject
    Retrofit retrofit;
    ChittichatServices chittichatServices;
    TextView knockKnockQuestion;
    EditText knockKnockAnswer;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ChittichatApp) getActivity().getApplication()).getMainAppComponent().inject(this);AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        chittichatServices = retrofit.create(ChittichatServices.class);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View promptView = inflater.inflate(R.layout.group_request_dialog_request,null);
        knockKnockQuestion = (TextView) promptView.findViewById(R.id.knockKnockAnswer_request);
        knockKnockQuestion.setText("ali alibaba  aliii alibaba");
        builder.setTitle("Knock Knock");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(promptView)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        knockKnockAnswer = (EditText) promptView.findViewById(R.id.knockKnockAnswer_request);
//                        Log.i("abcd",heading.getText().toString()+details.getText().toString());
                    }
                });

        return builder.create();

    }
}
