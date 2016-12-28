package in.co.nerdoo.chittichat;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;


    final private String TAG1 = "ChittChat_Server";
    final private String TAG2 = "Facebook_Server";
    private static Timer timer;
    private static SharedPreferences.Editor editor;
    private static Subscription subscription_first, subscription_second,subscription_fourth;
    private static ChittichatServices chittichatServices;
    private LoginButton loginWithFacebook;
    private CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallbackManager = CallbackManager.Factory.create();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);

//                    temp = (TextView) findViewById(R.id.textView);
        chittichatServices = retrofit.create(ChittichatServices.class);
        editor = sharedPreferences.edit();
        timer = new Timer();

        if (isLogin()) {
            Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }

        loginWithFacebook = (LoginButton) findViewById(R.id.facebook_login_button);
        loginWithFacebook.setReadPermissions(Arrays.asList("public_profile", "user_likes"));

        loginWithFacebook.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, final GraphResponse response) {
                        try {
//                                        editor.putBoolean("LoginWithfb",true);
                            editor.putString("fb_token", loginResult.getAccessToken().toString());
                            editor.putString("fb_userId", loginResult.getAccessToken().getUserId());
                            editor.putString("first_name", object.getString("first_name"));
                            editor.putString("last_name", object.getString("last_name"));
                            editor.putString("profile_pic_url", object.getJSONObject("picture").getJSONObject("data").getString("url"));
                            editor.apply();
                            LoginInformation loginInformation = new LoginInformation(loginResult.getAccessToken().getUserId(), loginResult
                                    .getAccessToken().toString());
                            logincall(loginInformation);
                            Toast.makeText(getApplicationContext(),"login",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG2, e.getMessage());
                        }

                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,birthday,first_name,last_name,verified,age_range,locale,timezone,updated_time,gender," +
                        "picture.width(800).height(800),cover,link");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Login Canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void onClickLogin(View view) {
        int isUsername = 1234;
        switch (isUsername) {
            case 0: {

                break;
            }
            case 1: {

                break;
            }
            default: {

            }
        }
        Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
        startActivity(intent);
    }


    private void loginWithEmail(final String email, final String password) {
        LoginWithEmailInformation loginWithEmailInformation = new LoginWithEmailInformation(email, password);
        Observable<ResponseMessage> getResponseOnLoginWithEmail = chittichatServices.getResponseOnLoginWithEmail(loginWithEmailInformation);
        subscription_fourth = getResponseOnLoginWithEmail.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (responseMessage->{

                });
    }


    private void logincall(final LoginInformation loginInformation) {
        final Observable<ResponseMessage> getloginToken = chittichatServices.getResponseOnLoginWithFacebook(loginInformation);
        subscription_first = getloginToken.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (responseMessage->{
                    if (responseMessage.getMessage().equals("user not found")) {
                        SignupWithFacebookInformation signupWithFacebookInformation = new SignupWithFacebookInformation(sharedPreferences
                                .getString("fb_userId", null), sharedPreferences.getString("first_name", null), sharedPreferences.getString
                                ("last_name", null), sharedPreferences.getString("fb_token", null));
                        signupWithFacebookCall(signupWithFacebookInformation);

                    } else if (responseMessage.getMessage().equals("something went wrong")) {
                        Log.d("Err","something went wrong");

                    } else {
                        editor.putString("ChittiChat_token", responseMessage.getMessage());
                        editor.apply();
                        Log.d("token", sharedPreferences.getString("ChittiChat_token", null));
                        Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                    subscription_first.unsubscribe();
                    },throwable -> {
                    if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                        Log.e("error",((HttpException) throwable).response().errorBody().toString());

                    }
                    if (throwable instanceof IOException) {
                        // A network or conversion error happened
                    }
                    subscription_first.unsubscribe();
                });

    }

    private void signupWithFacebookCall(final SignupWithFacebookInformation signupWithFacebookInformation) {
        final Observable<ResponseMessage> signupUsingFacebookId = chittichatServices.getResponseOnSignupWithFacebook(signupWithFacebookInformation);
        subscription_second = signupUsingFacebookId.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (responseMessage->{
                    Log.d("signup message",responseMessage.getMessage());
                    if (responseMessage.getMessage().equals("something went wrong")) {
                        Log.d("Signup err","Something went wrong");
                        subscription_second.unsubscribe();
//
                    } else {
                        editor.putString("ChittiChat_token", responseMessage.getMessage());
                        editor.apply();
                        Log.d("token", sharedPreferences.getString("ChittiChat_token", null));
                        Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);

                    }
                },throwable -> {
                    if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                        Log.e("error",((HttpException) throwable).response().errorBody().toString());

                    }
                    if (throwable instanceof IOException) {
                        // A network or conversion error happened
//                        Log.e("error",((IOException) throwable).toString());
                    }

                    subscription_second.unsubscribe();
                });
    }

    public void onClickSignup(View view) {

        DialogFragment dialogFragment = new SignupDialog();
        dialogFragment.show(getFragmentManager(), "show");
    }

    public Boolean isLogin() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        //check for chittichat token also...
        if (token == null) {
            return false;
        } else {
            return true;
        }
    }
}
class SignupWithFacebookInformation {

    String facebook_id,first_name,last_name,fb_token;

    public SignupWithFacebookInformation(String facebook_id, String firstName, String lastName, String fb_token) {
        this.facebook_id = facebook_id;
        this.first_name = firstName;
        this.last_name = lastName;
        this.fb_token = fb_token;
    }
}
class LoginInformation {
    String facebook_id;
    String fb_token;
    LoginInformation(String facebook_id,String fb_token){
        this.facebook_id = facebook_id;
        this.fb_token = fb_token;
    }
}
class LoginWithEmailInformation{
    String email;
    String password;

    public LoginWithEmailInformation(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
class LoginWithUserNameInformation{
    String userName;
    String password;

    public LoginWithUserNameInformation(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}

