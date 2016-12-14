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
import android.widget.TextView;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by shubham on 10/12/16.
 */
public class GroupAddRequestDialog extends DialogFragment {
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    ChittichatServices chittichatServices;
    TextView knockKnockQuestion;
    EditText knockKnockAnswer;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ChittichatApp) getActivity().getApplication()).getMainAppComponent().inject(this);AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        chittichatServices = retrofit.create(ChittichatServices.class);


        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View promptView = inflater.inflate(R.layout.group_request_dialog_request,null);

        builder.setMessage(sharedPreferences.getString("group_question","----"));
        builder.setView(promptView)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        knockKnockAnswer = (EditText) promptView.findViewById(R.id.knockKnockAnswer_request);
                        NewRequestInformation newRequestInformation = new NewRequestInformation(sharedPreferences.getString("ChittiChat_token",
                                null),sharedPreferences.getString("currentGroupId",null),knockKnockAnswer.getText().toString());
                        postNewRequest(newRequestInformation);
                    }
                });

        return builder.create();

    }
    public void postNewRequest(NewRequestInformation newRequestInformation){
        Observable<ResponseMessage> newRequest = chittichatServices.getResponseOnNewRequest(newRequestInformation);
        newRequest.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {

            }
        });
    }
}
class NewRequestInformation{
    private String token,group_id,answer;

    public NewRequestInformation(String token, String group_id, String answer) {
        this.token = token;
        this.group_id = group_id;
        this.answer = answer;
    }
}
