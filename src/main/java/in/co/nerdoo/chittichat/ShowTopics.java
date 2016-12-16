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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import io.socket.client.Socket;
import retrofit2.Retrofit;
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
    private static Boolean ShowEdittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_topics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_show_topics);
        toolbar.setTitle("Topics");
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);


        chittichatServices  = retrofit.create(ChittichatServices.class);
        recyclerView = (RecyclerView) findViewById(R.id.topics_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), R.drawable.divider));

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            groupId = null;
        }else{
            groupId = extras.getString("groupId");
            ShowEdittext = extras.getBoolean("ShowEdittext");
            SharedPreferences.Editor  editor = sharedPreferences.edit();
            editor.putString("currentGroupId",groupId);
            editor.apply();
//            callTopics(sharedPreferences.getString("ChittiChat_token",null),groupId);
        }
        JSONObject joinRoom = new JSONObject();
        try{
            joinRoom.put("token",sharedPreferences.getString("ChittiChat_token",null));
            joinRoom.put("room_id",groupId);
            socket.emit("joinRoom",joinRoom);
        }catch (JSONException e){
            Log.e("problem",e.getMessage());
        }
        callTopics(sharedPreferences.getString("ChittiChat_token","null"),groupId);

    }

    @Override
    public void onResume(){
        super.onResume();
        socket.connect();
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
        socket.connect();
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
    public void onBackPressed()
    {
        JSONObject joinRoomrequest = new JSONObject();
        try {
            joinRoomrequest.put("room_id", groupId);
            joinRoomrequest.put("token",sharedPreferences.getString("ChittiChat_token","null"));
            socket.emit("leaveRoom", joinRoomrequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_topics, menu);
        if(!ShowEdittext){
            menu.getItem(0).setVisible(false);
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
            default:
                Toast.makeText(getApplicationContext(),"Does not match any options",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void callTopics(final String token,final String groupId){
        Observable<List<Topics>> getTopics = chittichatServices.getResponseOnAllTopics(token,groupId);
        s1 = getTopics.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Topics>>() {
            @Override
            public void onCompleted() {
                Log.d("Chittichat service:","request completed");
                s1.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Topics> mytopics) {
                TopicAdapter adapter = new TopicAdapter(mytopics,ShowEdittext);

                recyclerView.setAdapter(adapter);

            }
        });
    }

    private static void callTopicsWithArticles(final String token,final String groupId) {
        Observable<List<TopicsWithArticle>> getTopicsWithArticle = chittichatServices.getResponseOnTopicsWithArticle(token,groupId);
            s2 = getTopicsWithArticle.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
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
    public void unfollow(){
        Observable<ResponseMessage> unfollow = chittichatServices.getResponseOnUnFollowingGroup(sharedPreferences.getString("ChittiChat_token",
                null),groupId);
        s3 = unfollow.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                s3.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {

            }
        });
    }
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

