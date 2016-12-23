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

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
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
    private  static Subscription s1,s2,s3;
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
        onAccept(responseRequestInformation);

    }
    public static void onDenyRequest(int position){
        Log.d("onDenyRequest","denied\t"+position);
        ResponseRequestInformation responseRequestInformation = new ResponseRequestInformation(token,mygroupRequestInfo.get(position).getBy(),groupId);
        onDeny(responseRequestInformation);
    }


    public void fetchPendingRequests(String groupid){
        Observable<List<groupRequestsNotification>> responseOngroupRequestNotification = chittichatServices.getGroupRequests(groupid);
        s1 = responseOngroupRequestNotification.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<groupRequestsNotification>>() {
            @Override
            public void onCompleted() {
                s1.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("notificationErr",e.getMessage());
            }

            @Override
            public void onNext(List<groupRequestsNotification> groupRequestsNotifications) {
                Log.d("_id",groupRequestsNotifications.get(0).getBy());
                mygroupRequestInfo = groupRequestsNotifications;
             notificationAdapter = new NotificationAdapter(groupRequestsNotifications);
                recyclerView.setAdapter(notificationAdapter);
                for(groupRequestsNotification notification:groupRequestsNotifications){
                    getUsernameByUserId(notification);
                }
            }
        });

    }
    private void getUsernameByUserId(final groupRequestsNotification notification){
        Observable<Username> getUsername = chittichatServices.getUsername(notification.getBy());
         getUsername.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Username>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("err",e.getMessage());

            }

            @Override
            public void onNext(Username username) {
                Log.d("username",username.getUsername());
                notification.setUsername(username.getUsername());
                notificationAdapter.notifyDataSetChanged();

            }
        });
    }
    public static void onAccept(ResponseRequestInformation responseRequestInformation){
        Observable<ResponseMessage> accept = chittichatServices.getResponseOnAcceptRequest(responseRequestInformation);
        s2 = accept.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                s2.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("error",e.getMessage());
            }

            @Override
            public void onNext(ResponseMessage responseMessage) {
                Log.d("response",responseMessage.getMessage());
            }
        });
    }
    public static void onDeny(ResponseRequestInformation responseRequestInformation){
        Observable<ResponseMessage> deny = chittichatServices.getResponseOnDenyRequest(responseRequestInformation);
        s3 = deny.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                s3.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("error",e.getMessage());
            }

            @Override
            public void onNext(ResponseMessage responseMessage) {
                Log.d("response",responseMessage.getMessage());
            }
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