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
import android.widget.Toast;

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
public class SignupDialog extends DialogFragment {
    @Inject
    Retrofit retrofit;
    ChittichatServices chittichatServices;
    Subscription subscription;
    private EditText username,email,password,confirm_password;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ((ChittichatApp) getActivity().getApplication()).getMainAppComponent().inject(this);
        chittichatServices = retrofit.create(ChittichatServices.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View a = inflater.inflate(R.layout.signup_dialog,null);
        username = (EditText) a.findViewById(R.id.signup_username);
        email = (EditText) a.findViewById(R.id.signup_email);
        password = (EditText) a.findViewById(R.id.signup_password);
        confirm_password = (EditText) a.findViewById(R.id.signup_confirm_password);
        builder.setTitle("New Account");
        builder.setView(inflater.inflate(R.layout.signup_dialog, null))
                // Add action buttons
                .setPositiveButton("Signup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tusername = username.getText().toString();
                        String temail = username.getText().toString();
                        String tpassword = username.getText().toString();
                        String tconfirm_password = username.getText().toString();
                        if(!(tusername.equals("") ||temail.equals("")||tpassword.equals("")||tconfirm_password.equals(""))){
                            SignupWithChittichatInformation signupWithChittichatInformation = new SignupWithChittichatInformation(tusername,temail,
                                    tpassword);
                            signupWithChittichat(signupWithChittichatInformation);

                        }else if(!(tpassword.equals(tconfirm_password))){
                            Toast.makeText(getActivity().getApplicationContext(),"Password Does Not Matched",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"All Fields Required",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        return builder.create();
    }
    private  void signupWithChittichat(SignupWithChittichatInformation signupWithChittichatInformation){
        Observable<ResponseMessage> signupWithChittichat = chittichatServices.getResponseOnSignupWithChittiChat(signupWithChittichatInformation);
        subscription = signupWithChittichat.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                Log.d("Signup","Completed");
                subscription.unsubscribe();
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
class SignupWithChittichatInformation{
    String username;
    String email;
    String password;
    SignupWithChittichatInformation(String username,String email,String password){
        this.username =username;
        this.email = email;
        this.password = password;
    }
}
