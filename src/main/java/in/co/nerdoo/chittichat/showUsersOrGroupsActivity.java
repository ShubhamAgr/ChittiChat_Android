package in.co.nerdoo.chittichat;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.util.List;

import javax.inject.Inject;

import in.co.nerdoo.chittichat.R;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class showUsersOrGroupsActivity extends AppCompatActivity {
    private  String groupId,groupName,groupAbout,question;
    TextView knock_knock_Question;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;
    private  static ChittichatServices chittichatServices;
    private  static Subscription subscription;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users_or_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        toolbar.setTitle("Group");
        setSupportActionBar(toolbar);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);
        chittichatServices = retrofit.create(ChittichatServices.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.searched_results_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            groupId = null;
            groupName = "---";
            groupAbout = "---";
            question = "---";
        }else{
            groupId = extras.getString("groupId");
            SharedPreferences.Editor  editor = sharedPreferences.edit();
            editor.putString("currentGroupId",groupId);
            editor.apply();
            groupName = extras.getString("groupName");
            toolbar.setTitle(groupName);
            groupAbout = extras.getString("groupAbout");
            question = extras.getString("question");
        }
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString("group_question",question);
        editor.apply();
        callTopics(sharedPreferences.getString("ChittiChat_token",null),groupId);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_show_users_or_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_follow:
                followGroups();
                return true;

            default:
                     Toast.makeText(getApplicationContext(),"Does not match any options",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    private static void callTopics(final String token,final String groupId){
        Observable<List<Topics>> getTopics = chittichatServices.getResponseOnAllTopics(token,groupId);
        subscription = getTopics.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Topics>>() {
            @Override
            public void onCompleted() {
                Log.d("Chittichat service:","request completed");
                subscription.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Topics> mytopics) {
                TopicAdapter adapter = new TopicAdapter(mytopics,false);
                recyclerView.setAdapter(adapter);

            }
        });
    }




    public void followGroups(){
        Observable<ResponseMessage> followRequest = chittichatServices.getResponseOnFollowingGroup(sharedPreferences.getString("ChittiChat_token",
                null),groupId);
        followRequest.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
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
    public void onClickfab(View view){
        DialogFragment dialogFragment = new GroupAddRequestDialog();
        dialogFragment.show(getFragmentManager(),"knock_knock");
    }

}
