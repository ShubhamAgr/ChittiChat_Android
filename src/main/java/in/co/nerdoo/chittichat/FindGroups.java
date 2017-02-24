package in.co.nerdoo.chittichat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FindGroups extends AppCompatActivity {
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    private static RecyclerView recyclerView;
    private static LinearLayoutManager manager;
    private  static FindGroupAdapter findGroupAdapter;
    private  static ChittichatServices chittichatServices;
    private  static boolean isLoading,isEmpty;
    private  static int initialItem,finalItem;
    private static List<Groups> groupsList;
    public static Button follow;
    private static Subscription s1,s2,s3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_groups);
        follow = (Button) findViewById(R.id.follow_button);
//        follow.setAlpha(0.4f);
        follow.setEnabled(false);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);
        chittichatServices = retrofit.create(ChittichatServices.class);
        recyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(recyclerviewOnScrollListener);
        isLoading =   true;
        isEmpty = false;
        initialItem = 0;
        finalItem = 12;
        getInitialGroup(initialItem+"_"+finalItem);
    }


    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(this,FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    public  void onClickfollow(View view){
        HashSet<Integer> positions = FindGroupAdapter.positionList;
        Iterator<Integer> iterator = positions.iterator();
        while (iterator.hasNext()){
            int position = iterator.next();
            followGroups(FindGroups.groupsList.get(position).get_id());
        }
        Intent intent  = new Intent(this,FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    public static void getInitialGroup(String range){
        Observable<List<Groups>> getGroups = chittichatServices.getGroups(range);
       s1= getGroups.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(groupsList->{
            if(groupsList.isEmpty() || groupsList.size()%12 != 0){
                isEmpty = true;
            }
            FindGroups.groupsList = groupsList;
            findGroupAdapter = new FindGroupAdapter(FindGroups.groupsList);
            recyclerView.setAdapter(findGroupAdapter);
           initialItem = finalItem+1;
           finalItem +=12;
           isLoading = false;
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

//
    }
    public static void getGroups(String range){
        Observable<List<Groups>> getGroups = chittichatServices.getGroups(range);
       s2 = getGroups.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(groupsList->{
            if(groupsList.isEmpty() || groupsList.size()%12 != 0){
                isEmpty = true;
            }
            FindGroups.groupsList.addAll(groupsList);
           findGroupAdapter.notifyItemRangeInserted(initialItem,groupsList.size());
           initialItem = finalItem+1;
           finalItem +=12;
           isLoading = false;
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
    public void followGroups(String groupId){
        Observable<ResponseMessage> followRequest = chittichatServices.getResponseOnFollowingGroup(sharedPreferences.getString("ChittiChat_token",
                null),groupId);
        s3 = followRequest.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
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

    private RecyclerView.OnScrollListener recyclerviewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = manager.getChildCount();
            int totalItemCount = manager.getItemCount();
            int firstVisibleItem = manager.findFirstVisibleItemPosition();
            int lastVisibleItem = manager.findLastVisibleItemPosition();
            if(!isLoading && !isEmpty){
                if ((visibleItemCount + firstVisibleItem) >= totalItemCount && firstVisibleItem >=0 && lastVisibleItem >= initialItem-5 )//0)//
                {
                    FindGroups.isLoading = true;

                    getGroups(initialItem+"_"+finalItem);
                }
            }
        }
    };
}

class Groups{
    String _id,group_name,knock_knock_question,group_category,group_about;
    private boolean isSelected;
    public  void setSelected(boolean selected){
        isSelected = selected;
    }
    public boolean isSelected(){
        return isSelected;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getKnock_knock_question() {
        return knock_knock_question;
    }

    public void setKnock_knock_question(String knock_knock_question) {
        this.knock_knock_question = knock_knock_question;
    }

    public String getGroup_category() {
        return group_category;
    }

    public void setGroup_category(String group_category) {
        this.group_category = group_category;
    }

    public String getGroup_about() {
        return group_about;
    }

    public void setGroup_about(String group_about) {
        this.group_about = group_about;
    }
}

