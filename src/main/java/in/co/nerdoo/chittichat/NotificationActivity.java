package in.co.nerdoo.chittichat;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.operators.OnSubscribePublishMulticast;
import rx.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;


    private static RecyclerView recyclerView;
    private  static NotificationAdapter notificationAdapter;
    private static LinearLayoutManager manager;
    private static String groupId;
    private  static Subscription s1,s2,s3,s4;
    private static String token;
    private static List<groupRequestsNotification> mygroupRequestInfo;
    private  static ChittichatServices chittichatServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            groupId = extras.getString("groupId");
        }
        recyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        chittichatServices = retrofit.create(ChittichatServices.class);
        fetchPendingRequests(groupId);
        token = sharedPreferences.getString("ChittiChat_token",null);
//        Log.d("token",token);

    }
    public static void onAcceptRequest(int position){
        Log.d("onAcceptRequest","accepted\t"+position);
        ResponseRequestInformation responseRequestInformation = new ResponseRequestInformation(token,mygroupRequestInfo.get(position).getBy(),groupId);
        onAccept(responseRequestInformation,position);


    }
    public static void onDenyRequest(int position){
        Log.d("onDenyRequest","denied\t"+position);
        ResponseRequestInformation responseRequestInformation = new ResponseRequestInformation(token,mygroupRequestInfo.get(position).getBy(),groupId);
        onDeny(responseRequestInformation,position);

    }


    public void fetchPendingRequests(String groupid){
        Observable<List<groupRequestsNotification>> responseOngroupRequestNotification = chittichatServices.getGroupRequests(groupid);
        s1 = responseOngroupRequestNotification.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (groupRequestsNotifications->{
                    Log.d("_id",groupRequestsNotifications.get(0).getBy());
                    mygroupRequestInfo = groupRequestsNotifications;
                    notificationAdapter = new NotificationAdapter(groupRequestsNotifications);
                    recyclerView.setAdapter(notificationAdapter);
//                    for(groupRequestsNotification notification:groupRequestsNotifications){
//                        getUsernameByUserId(notification);
//                    }
                    s1.unsubscribe();
                },throwable -> {
                    if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                        Log.e("error",((HttpException) throwable).response().errorBody().toString());

                    }
                    if (throwable instanceof IOException) {
                        // A network or conversion error happened
                    }
                    s1.unsubscribe();
                });
    }
    private void getUsernameByUserId(final groupRequestsNotification notification){
        Observable<Username> getUsername = chittichatServices.getUsername(notification.getBy());
         s4=getUsername.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(username->{
             Log.d("username",username.getUsername());
             notification.setUsername(username.getUsername());
             notificationAdapter.notifyDataSetChanged();
            s4.unsubscribe();
         },throwable -> {
             if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                 Log.e("error",((HttpException) throwable).response().errorBody().toString());

             }
             if (throwable instanceof IOException) {
                 // A network or conversion error happened
             }
             s4.unsubscribe();
         });

    }
    public static void onAccept(ResponseRequestInformation responseRequestInformation,final int position){
        Observable<ResponseMessage> accept = chittichatServices.getResponseOnAcceptRequest(responseRequestInformation);
        s2 = accept.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
            Log.d("response",responseMessage.getMessage());
            mygroupRequestInfo.remove(position);
            notificationAdapter.notifyDataSetChanged();
            s2.unsubscribe();
        },throwable -> {
            if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                Log.e("error",((HttpException) throwable).response().errorBody().toString());

            }
            if (throwable instanceof IOException) {
                // A network or conversion error happened
            }
            s2.unsubscribe();
        });
    }
    public static void onDeny(ResponseRequestInformation responseRequestInformation,final  int position){
        Observable<ResponseMessage> deny = chittichatServices.getResponseOnDenyRequest(responseRequestInformation);
        s3 = deny.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
            Log.d("response",responseMessage.getMessage());
            mygroupRequestInfo.remove(position);
            notificationAdapter.notifyDataSetChanged();
            s3.unsubscribe();
        },throwable -> {
            if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                Log.e("error",((HttpException) throwable).response().errorBody().toString());

            }
            if (throwable instanceof IOException) {
                // A network or conversion error happened
            }
            s3.unsubscribe();
        });
    }
}


class groupRequestsNotification{
    private String by,knock_knock_answer,username;

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getKnock_knock_answer() {
        return knock_knock_answer;
    }

    public void setKnock_knock_answer(String knock_knock_answer) {
        this.knock_knock_answer = knock_knock_answer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
class ResponseRequestInformation{
    String token,requested_by,group_id;

    public ResponseRequestInformation(String token, String requested_by,String group_id) {
        this.token = token;
        this.requested_by = requested_by;
        this.group_id = group_id;
    }
}