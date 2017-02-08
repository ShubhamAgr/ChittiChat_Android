package in.co.nerdoo.chittichat;

import android.app.DialogFragment;
import android.content.Context;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import in.co.nerdoo.chittichat.R;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class showUsersOrGroupsActivity extends AppCompatActivity {
    private  String groupId,groupName,groupAbout,question;
    TextView knock_knock_Question;
    ImageView groupImage;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;
    private  static ChittichatServices chittichatServices;
    private  static Subscription subscription,s2;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users_or_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupImage = (ImageView) findViewById(R.id.groupsImg);
        toolbar.showOverflowMenu();
        toolbar.setTitle("Group");
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        Picasso.with(getApplicationContext()).load(ChittichatApp.getBaseUrl()+"/images/"+groupId).fit().into(groupImage);
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
        subscription = getTopics.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mytopics->{
            TopicAdapter adapter = new TopicAdapter(mytopics,false);
            recyclerView.setAdapter(adapter);
            subscription.unsubscribe();
        },throwable -> {
            if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                Log.e("error",((HttpException) throwable).response().errorBody().toString());

            }
            if (throwable instanceof IOException) {
                // A network or conversion error happened
            }
            subscription.unsubscribe();
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(this,FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    public void followGroups(){
        Observable<ResponseMessage> followRequest = chittichatServices.getResponseOnFollowingGroup(sharedPreferences.getString("ChittiChat_token",
                null),groupId);
        s2 = followRequest.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
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
    public void onClickfab(View view){
        DialogFragment dialogFragment = new GroupAddRequestDialog();
        dialogFragment.show(getFragmentManager(),"knock_knock");
    }

}
