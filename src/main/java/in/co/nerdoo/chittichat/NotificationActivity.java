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
import rx.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;


    private static RecyclerView recyclerView;
    private  static NotificationAdapter notificationAdapter;
    private static LinearLayoutManager manager;
    String groupId;
    Subscription s1;
    ChittichatServices chittichatServices;
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

    }
    public static void onAcceptRequest(int position){
        Log.d("onAcceptRequest","accepted\t"+position);
    }
    public static void onDenyRequest(int position){
        Log.d("onDenyRequest","denied\t"+position);
    }
    public void onClickX(View view){
        Toast.makeText(getApplicationContext(),"lalala",Toast.LENGTH_SHORT).show();
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
             NotificationAdapter adapter = new NotificationAdapter(groupRequestsNotifications);
                recyclerView.setAdapter(adapter);
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