package in.co.nerdoo.chittichat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by shubham on 22/11/16.
 */
public class newTopicDialog extends DialogFragment {
    @Inject
    SharedPreferences sharedPreferences;
    EditText heading,details;
    @Inject
    Retrofit retrofit;
    private static ChittichatServices chittichatServices;
    private  static Subscription subscription;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ChittichatApp) getActivity().getApplication()).getMainAppComponent().inject(this);AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        chittichatServices = retrofit.create(ChittichatServices.class);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View promptView = inflater.inflate(R.layout.new_topic_dialog,null);

        builder.setTitle("New Topic");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(promptView)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        heading = (EditText) promptView.findViewById(R.id.topic_heading_dialog);
                        details = (EditText) promptView.findViewById(R.id.topic_detail_dialog);
                        Log.i("abcd",heading.getText().toString()+details.getText().toString());
                        NewTopicInformation newTopicInformation = new NewTopicInformation(sharedPreferences.getString("ChittiChat_token",""),
                                sharedPreferences.getString("currentGroupId",""),heading.getText().toString(),details.getText().toString());
                        Log.d("abc",sharedPreferences.getString("currentGroupId","adfas"));
                        createNewTopic(newTopicInformation);
                    }
                });

        return builder.create();

    }
    private static void createNewTopic(NewTopicInformation newTopicInformation) {
        Observable<ResponseMessage> getOnNewTopicResponse = chittichatServices.getResponseOnNewTopic(newTopicInformation);
        subscription = getOnNewTopicResponse.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                Log.d("Task","Completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {
                    Log.d("message",responseMessage.getMessage());
            }
        });
    }

}
class NewTopicInformation{
    String token,group_id,topic_title,topic_description;

    public NewTopicInformation(String token, String group_id, String topic_title, String topic_description) {
        this.token = token;
        this.group_id = group_id;
        this.topic_title = topic_title;
        this.topic_description = topic_description;
    }
}
