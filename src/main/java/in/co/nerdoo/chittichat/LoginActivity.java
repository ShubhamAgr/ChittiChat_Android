    package in.co.nerdoo.chittichat;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Handler;
    import android.os.Looper;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.TextView;

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

    import java.sql.Time;
    import java.util.Arrays;
    import java.util.Objects;
    import java.util.Timer;
    import java.util.TimerTask;

    import javax.inject.Inject;

    import retrofit2.Retrofit;
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

        private static Intent intent;
        final private String TAG1 = "ChittChat_Server";
        final private String TAG2 = "Facebook_Server";
        private static Timer timer;
        private static  SharedPreferences.Editor editor;
        private static Subscription subscription_first,subscription_second,subscription_third,subscription_fourth;
        private static ChittichatServices chittichatServices;
        private LoginButton loginWithFacebook;
        private TextView temp;
        private CallbackManager facebookCallbackManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(getApplicationContext());
            facebookCallbackManager = CallbackManager.Factory.create();
            setContentView(R.layout.activity_login);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);

            temp = (TextView) findViewById(R.id.textView);
            chittichatServices = retrofit.create(ChittichatServices.class);
            editor = sharedPreferences.edit();
            timer = new Timer();



            loginWithFacebook=(LoginButton) findViewById(R.id.facebook_login_button);
            loginWithFacebook.setReadPermissions(Arrays.asList("public_profile","user_likes"));
            loginWithFacebook.registerCallback(facebookCallbackManager,new FacebookCallback<LoginResult>(){


                @Override
                public void onSuccess(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, final GraphResponse response) {
                           try{
                               editor.putString("fb_token",loginResult.getAccessToken().toString());
                               editor.putString("fb_userId",loginResult.getAccessToken().getUserId());
                               editor.putString("first_name",object.getString("first_name"));
                               editor.putString("profile_pic_url",object.getJSONObject("picture").getJSONObject("data").getString("url"));
                               editor.commit();
                               logincall(loginResult.getAccessToken().getUserId(),loginResult.getAccessToken().toString(),object.getString("first_name"),object.getString("last_name"));
                           }catch (Exception e){
                                Log.e(TAG2,e.getMessage());
                           }

                        }

                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,birthday,first_name,last_name,verified,age_range,locale,timezone,updated_time,gender," +
                            "picture.type(large),cover,link");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {
                    temp.setText("login canceled");
                }

                @Override
                public void onError(FacebookException error) {
                    temp.setText("Login Failed");
                }
            });
        }



        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }


        public void onClickLogin(View view){
            int isUsername = 1234;
            switch (isUsername){
                case 0:{

                    break;
                }
                case 1:{

                    break;
                }
                default: {

                }
            }
            Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
            startActivity(intent);
        }


        private  void loginWithUsername(final  String username, final  String password){
            Observable<ResponseMessage> getResponseOnLoginWithUsername = chittichatServices.getResponseOnLoginWithUsername(username,password);
           subscription_third =  getResponseOnLoginWithUsername.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                     Observer<ResponseMessage>() {
                @Override
                public void onCompleted() {
                    subscription_third.unsubscribe();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ResponseMessage responseMessage) {

                }
            });

        }

        private void loginWithEmail(final String email,final  String password){
            Observable<ResponseMessage> getResponseOnLoginWithEmail = chittichatServices.getResponseOnLoginWithEmail(email,password);
            subscription_fourth = getResponseOnLoginWithEmail.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                  Observer<ResponseMessage>() {
                @Override
                public void onCompleted() {
                    subscription_fourth.unsubscribe();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ResponseMessage responseMessage) {

                }
            });
        }


        private void logincall(final String userId, final String accessToken, final String firstName, final String lastName){
            final Observable<ResponseMessage> getloginToken = chittichatServices.getResponseOnLoginWithFacebook(userId,accessToken);
            subscription_first = getloginToken.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                          Observer<ResponseMessage>() {
                @Override
                public void onCompleted() {
                    subscription_first.unsubscribe();
                    Log.d(TAG1,"operation completed");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG1,e.getMessage());
                }

                @Override
                public void onNext(ResponseMessage responseMessage) {

                    if(responseMessage.getMessage().equals("user not found")){
                            signupWithFacebookCall(userId,accessToken,firstName,lastName);

                    }else if(responseMessage.getMessage().equals("something went wrong")){
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                logincall(userId,accessToken,firstName,lastName);
                                            }
                                        });
                                    }catch (Exception e){
                                      Log.d("timer",e.getMessage());
                                    }


                                }
                            },0,900000);
                    }else{
                        editor.putString("ChittiChat_token",responseMessage.getMessage());
                        Intent intent = new Intent(LoginActivity.this,FirstActivity.class);

                    }
                }
            });

        }

        private void signupWithFacebookCall(final String userId, final String accessToken, final String firstName, final String lastName){
            final Observable<ResponseMessage> signupUsingFacebookId =  chittichatServices.getResponseOnSignupWithFacebook(userId,firstName,
                    lastName,accessToken);
            subscription_second = signupUsingFacebookId.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                                  Observer<ResponseMessage>() {
                @Override
                public void onCompleted() {
                    subscription_second.unsubscribe();
                    Log.d(TAG1,"operation completed");
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ResponseMessage responseMessage) {
                        if(responseMessage.getMessage().equals("something went wrong")){
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                signupWithFacebookCall(userId,accessToken,firstName,lastName);
                                            }
                                        });
                                    }catch (Exception e){
                                        Log.d("timer",e.getMessage());
                                    }


                                }
                            },0,900000);
                        }else{
                            editor.putString("ChittiChat_token",responseMessage.getMessage());
                            Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
                            startActivity(intent);
                        }
                }
            });
        }
        public void onClickSignup(View view){
            startActivity(new Intent(LoginActivity.this,SignupActivity.class));
        }
    }
