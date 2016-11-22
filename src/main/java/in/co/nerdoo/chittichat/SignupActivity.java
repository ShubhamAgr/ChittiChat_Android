package in.co.nerdoo.chittichat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignupActivity extends AppCompatActivity {
    @Inject
    Retrofit retrofit;
    ChittichatServices chittichatServices;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        username = (EditText) findViewById(R.id.signup_username);
        email =  (EditText) findViewById(R.id.signup_email);
        password = (EditText) findViewById(R.id.signup_password);
        confirm_password = (EditText) findViewById(R.id.signup_confirm_password);


        chittichatServices = retrofit.create(ChittichatServices.class);
    }



    public void onClickSignup(View view){
        String usernametext  = username.getText().toString();
        String emailtext = email.getText().toString();
        String passwordtext = password.getText().toString();
        String confirmPasswordText = confirm_password.getText().toString();
        if(passwordtext.equals(confirmPasswordText)){
//            signupWithChittichat(usernametext,emailtext,passwordtext);
        }else {
            Toast.makeText(getApplicationContext(),"password does not matched",Toast.LENGTH_SHORT).show();
        }
    }
}


