package in.co.nerdoo.chittichat;


import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityReceiverListener {
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

        chittichatServices = retrofit.create(ChittichatServices.class);
        editor = sharedPreferences.edit();
        timer = new Timer();
        checkConnection();
//        if (isLogin()) {
////            Intent intent = new Intent(LoginActivity.this,DashBoard.class);
//            Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            finish();
//            startActivity(intent);
//        }

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

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/"+AccessToken.getCurrentAccessToken().getUserId()+"/likes",
                        null,
                        HttpMethod.GET,response -> {


                        Log.d("likes response",response.getRawResponse());
                }).executeAsync();

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

    /**
     * Check Network Connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d("Network connection","Changed");
        if(isConnected){
            checkConnection();
        }else {
            Toast.makeText(getApplicationContext(), String.valueOf("isnotConnected"), Toast.LENGTH_LONG).show();
        }
    }

    private void checkConnection(){
        boolean isConnected = ConnectivityReciever.isConnected();
        if(isConnected){
            try {
                PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(),0);
                int versioncode = pinfo.versionCode;
                String versionName = pinfo.versionName;
                Log.i("vCode&Name",String.valueOf(versioncode)+"\t"+versionName);
                preLoginCall(String.valueOf(versioncode));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(),String.valueOf("Please Check your Internet Connection"),Toast.LENGTH_LONG).show();
        }

        Log.d("IsConnected",String.valueOf(isConnected));
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

    private void preLoginCall(String version_code){
        Observable<PreLogin> getPreLoginInfo = chittichatServices.getVersionCode(version_code);
        subscription_fourth = getPreLoginInfo.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(preLoginInfo ->{
            Log.d("forceupdate",String.valueOf(preLoginInfo.force_update));
            if(preLoginInfo.force_update){
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
             Toast.makeText(getApplicationContext(),"Please update to new version else application will not work",Toast.LENGTH_LONG).show();
            }else if(Integer.parseInt(preLoginInfo.getCurrent_version_code())-Integer.parseInt(version_code)>0){
                Toast.makeText(getApplicationContext(),"New Version Available",Toast.LENGTH_SHORT).show();
                if (isLogin()) {
//            Intent intent = new Intent(LoginActivity.this,DashBoard.class);
                    Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            }else{
                if (isLogin()) {
//            Intent intent = new Intent(LoginActivity.this,DashBoard.class);
                    Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            }
        },throwable -> {
            if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                Log.e("error",((HttpException) throwable).response().errorBody().toString());

            }
            if (throwable instanceof IOException) {
                // A network or conversion error happened
            }
            subscription_fourth.unsubscribe();
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

class PreLogin{
    String current_version_code;
    Boolean force_update;

    public String getCurrent_version_code() {
        return current_version_code;
    }

    public void setCurrent_version_code(String current_version_code) {
        this.current_version_code = current_version_code;
    }

    public Boolean getForce_update() {
        return force_update;
    }

    public void setForce_update(Boolean force_update) {
        this.force_update = force_update;
    }
}

