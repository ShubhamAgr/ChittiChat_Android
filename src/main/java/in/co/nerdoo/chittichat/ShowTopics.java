package in.co.nerdoo.chittichat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShowTopics extends AppCompatActivity {
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    private String groupId;
    private static ChittichatServices chittichatServices;
    private  static Subscription subscription_first,subscription_second,subscription_third;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_topics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_show_topics);
        toolbar.setTitle("Topics");
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);


       chittichatServices  = retrofit.create(ChittichatServices.class);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            groupId = null;
        }else{
            groupId = extras.getString("groupId");
            SharedPreferences.Editor  editor = sharedPreferences.edit();
            editor.putString("currentGroupId",groupId);
            editor.apply();
//            callTopics(sharedPreferences.getString("ChittiChat_token",null),groupId);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_topics, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
              return true;
            case R.id.new_topic:
                Toast.makeText(getApplicationContext(),"abcde",Toast.LENGTH_SHORT).show();
                DialogFragment dialogFragment = new newTopicDialog();
                dialogFragment.show(getFragmentManager(),"abcd");


                return true;
            default:
                Toast.makeText(getApplicationContext(),"Does not match any options",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void callTopics(final String token,final String groupId) {
        Observable<List<TopicsWithArticle>> getTopicsWithArticle = chittichatServices.getResponseOnTopicsWithArticle(token,groupId);
            subscription_first = getTopicsWithArticle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                          Observer<List<TopicsWithArticle>>() {
            @Override
            public void onCompleted() {
                subscription_first.unsubscribe();
                Log.d("ChittiChat Server:","request completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("ChittiChat Server:",e.getMessage());
            }

            @Override
            public void onNext(List<TopicsWithArticle> articles) {
                    //send data to the recycler view of the
            }
        });
    }
}
class Topics{
    private  String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
class TopicsWithArticle{
    private  String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}

