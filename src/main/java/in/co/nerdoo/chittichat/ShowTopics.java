package in.co.nerdoo.chittichat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShowTopics extends AppCompatActivity {

    @Inject
    Socket socket;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    private String groupId;
    private static ChittichatServices chittichatServices;
    private  static Subscription s1,s2,s3,s4;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private  static TopicAdapter topicAdapter;
    private static Boolean ShowEdittext;
    public  static boolean isadmin,isInit,isFollowed;
    private ImageButton notification;
    private static TextView notificationCount;
    private static List<Topics> topicsList;
    private static ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_topics);
        isFollowed = true;
        notification = (ImageButton) findViewById(R.id.mynotificationbutton_showtopics);
        notificationCount = (TextView) findViewById(R.id.notificationCount);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_show_topics);
        toolbar.setTitle("Topics");
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_showTopics);
        progressBar.setVisibility(View.VISIBLE);
        chittichatServices  = retrofit.create(ChittichatServices.class);
        recyclerView = (RecyclerView) findViewById(R.id.topics_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
//        recyclerView.addItemDecoration(
//                new DividerItemDecoration(getApplicationContext(), R.drawable.divider));

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            groupId = null;
            isadmin = false;
        }else{
            groupId = extras.getString("groupId");
            ShowEdittext = extras.getBoolean("ShowEdittext");
            isadmin = extras.getBoolean("isadmin");
            SharedPreferences.Editor  editor = sharedPreferences.edit();
            editor.putString("currentGroupId",groupId);
            editor.apply();
//            callTopics(sharedPreferences.getString("ChittiChat_token",null),groupId);
        }
        if(!isInit){
            socket.on("newtopic",onNewTopic);
            isInit = true;
        }

        if(!socket.connected()){
            socket.connect();
        }
        JSONObject joinRoom = new JSONObject();
        try{
            joinRoom.put("token",sharedPreferences.getString("ChittiChat_token",null));
            joinRoom.put("room_id",groupId);
            socket.emit("joinRoom",joinRoom);
        }catch (JSONException e){
            Log.e("problem",e.getMessage());
        }
        notification.setVisibility(View.GONE);
        notificationCount.setVisibility(View.GONE);
        if(isadmin){
            notification.setVisibility(View.VISIBLE);
            notificationCount.setVisibility(View.VISIBLE);
            callNotificationCount(groupId);
        }
        callTopics(sharedPreferences.getString("ChittiChat_token","null"),groupId);

    }

    @Override
    public void onResume(){
        super.onResume();
//        socket.connect();
        JSONObject joinRoom = new JSONObject();
        try{
            joinRoom.put("token",sharedPreferences.getString("ChittiChat_token",null));
            joinRoom.put("room_id",groupId);
            socket.emit("joinRoom",joinRoom);
        }catch (JSONException e){
            Log.e("problem",e.getMessage());
        }
        if(socket.connected()){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("ChittiChat_token","null"));
                socket.emit("authorize",jsonObject);
            }catch (JSONException je){
                Log.e("Exception_Authorization",je.getMessage());
            }
        }
    }
    @Override
    public void onPause(){
        super.onPause();
//        socket.connect();
        JSONObject joinRoomrequest = new JSONObject();
        try {
            joinRoomrequest.put("room_id", groupId);
            joinRoomrequest.put("token",sharedPreferences.getString("ChittiChat_token","null"));
            socket.emit("leaveRoom", joinRoomrequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(socket.connected()){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("ChittiChat_token","null"));
                socket.emit("app_close",jsonObject);
            }catch (JSONException je){
                Log.e("Exception_Authorization",je.getMessage());
            }
        }
    }
    @Override
    public void onStop(){
        super.onStop();

    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        JSONObject joinRoomrequest = new JSONObject();
        try {
            joinRoomrequest.put("room_id", groupId);
            joinRoomrequest.put("token",sharedPreferences.getString("ChittiChat_token","null"));
            socket.emit("leaveRoom", joinRoomrequest);
            Intent intent  = new Intent(this,FirstActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_topics, menu);
        if(!ShowEdittext){
            menu.getItem(0).setVisible(false);
        }
        if(isFollowed){
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
        }else{
            menu.getItem(2).setVisible(true);
            menu.getItem(1).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
              return true;
            case R.id.new_topic:
                DialogFragment dialogFragment = new newTopicDialog();
                dialogFragment.show(getFragmentManager(),"abcd");
                return true;
            case R.id.action_unfollow:
                unfollow();
                return  true;
            case R.id.action_follows:
                followGroups();
                return true;
            default:
                Toast.makeText(getApplicationContext(),"Does not match any options",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    //button clicks
    public void onClickNotify(View view){
        Log.d("Clicked","notify");
        Intent intent = new Intent(getApplicationContext(),NotificationActivity.class);
        intent.putExtra("groupId",groupId);
        startActivity(intent);

    }

    //network call methods

    private static void callTopics(final String token,final String groupId){

            Observable<List<Topics>> getTopics = chittichatServices.getResponseOnAllTopics(token,groupId);
            s1=  getTopics.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mytopics->{
                topicsList = mytopics;
                topicAdapter= new TopicAdapter(topicsList,ShowEdittext);
                recyclerView.setAdapter(topicAdapter);
                progressBar.setVisibility(View.GONE);
                s1.unsubscribe();
            },throwable -> {
                if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                    Log.e("error",((HttpException) throwable).response().errorBody().toString());

                }
                if (throwable instanceof IOException) {
                    // A network or conversion error happened
                }
                progressBar.setVisibility(View.GONE);
                Log.e("error",throwable.getMessage());
            });


    }

    private static void callNotificationCount(String groupId){
        Observable<ResponseMessage> getCount = chittichatServices.getResponseOnNotficationCount(groupId);
        s2 = getCount.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(count->{
            notificationCount.setText(count.getMessage());
        },throwable -> {
            if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                Log.e("error",((HttpException) throwable).response().errorBody().toString());

            }
            if (throwable instanceof IOException) {
                // A network or conversion error happened
            }
            Log.e("error",throwable.getMessage());
        });
    }
/*
    private static void callTopicsWithArticles(final String token,final String groupId) {
        Observable<List<TopicsWithArticle>> getTopicsWithArticle = chittichatServices.getResponseOnTopicsWithArticle(token,groupId);
            s2 = getTopicsWithArticle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                          Observer<List<TopicsWithArticle>>() {
            @Override
            public void onCompleted() {
                s2.unsubscribe();
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
*/
    public void unfollow(){
        Observable<ResponseMessage> unfollow = chittichatServices.getResponseOnUnFollowingGroup(sharedPreferences.getString("ChittiChat_token",
                null),groupId);
        s3 = unfollow.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
           isFollowed = false;
            invalidateOptionsMenu();
           s3.unsubscribe();
        },throwable -> {
            Log.e("error",throwable.getMessage());
        });
    }
    public void followGroups(){
        Observable<ResponseMessage> followRequest = chittichatServices.getResponseOnFollowingGroup(sharedPreferences.getString("ChittiChat_token",
                null),groupId);
         followRequest.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
            isFollowed = true;
            invalidateOptionsMenu();
//            s3.unsubscribe();
        },throwable -> {
            if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                Log.e("error",((HttpException) throwable).response().errorBody().toString());

            }
            if (throwable instanceof IOException) {
                // A network or conversion error happened
            }
//            s3.unsubscribe();
        });
    }

    private Emitter.Listener onNewTopic = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try{
                        getTopicByTopicId(data.getString("topic_id"));
                    }catch (JSONException e){

                    }
                }
//            });
//        }
    };

    private  void getTopicByTopicId(String topicId){
            Observable<List<Topics>> getNewTopic = chittichatServices.getReponseOnNewTopic(sharedPreferences.getString("ChittiChat_token",null),
                    topicId);
        s4 = getNewTopic.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(topicses->{
            topicsList.add(topicses.get(0));
            topicAdapter.notifyDataSetChanged();
            s4.unsubscribe();
        });
    }
    //Decorative methods
    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = verticalSpaceHeight;
            }
        }

    }
    public  class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private  final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable divider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }
}
//Classes
class Topics{
    private  String _id;
    private String topic_title;
    private String topic_detail;

    public String get_id() {
        return _id;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }

    public String getTopic_detail() {
        return topic_detail;
    }

    public void setTopic_detail(String topic_detail) {
        this.topic_detail = topic_detail;
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

