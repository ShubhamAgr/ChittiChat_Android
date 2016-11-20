package in.co.nerdoo.chittichat;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import retrofit2.Retrofit;
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

        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);


       chittichatServices  = retrofit.create(ChittichatServices.class);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            groupId = null;
        }else{
            groupId = extras.getString("groupId");
            callTopics(sharedPreferences.getString("ChittiChat_token",null),groupId);
        }

    }

    private static void callTopics(final String token,final String groupId) {
        Observable<ResponseMessage> getTopicsWithArticle = chittichatServices.getResponseOnTopicsWithArticle(token,groupId);
            subscription_first = getTopicsWithArticle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                          Observer<ResponseMessage>() {
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
            public void onNext(ResponseMessage responseMessage) {
                    //send data to the recycler view of the
            }
        });
    }
    private static void createNewTopic(final  String token,final String groupId,final String topicTitle,final String topicDescription) {
        Observable<ResponseMessage> getOnNewTopicResponse = chittichatServices.getResponseOnNewTopic(token,groupId,topicTitle,topicDescription);
        subscription_second = getOnNewTopicResponse.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                              Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                subscription_second.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {

            }
        });

    }
}
