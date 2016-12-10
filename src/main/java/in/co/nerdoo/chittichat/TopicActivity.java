package in.co.nerdoo.chittichat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TopicActivity extends AppCompatActivity {
    @Inject
    Socket socket;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    private static String token;
    private static String topicId;
    private  static ChittichatServices chittichatServices;
    private EditText articleContent;
    private int PICK_IMAGE_REQUEST = 1;
    private List<Articles> articlesList;
    private static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        articleContent = (EditText) findViewById(R.id.articleText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_articles);
        toolbar.setTitle("Articles");
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            topicId = null;
        }else{
            topicId = extras.getString("TopicId");
            token = sharedPreferences.getString("ChittiChat_token",null);
        }
        socket.on("newarticle",onNewArticle);
//        if(socket.connected()){
//            socket.disconnect();
//
//            socket.connect();
//        }else {
//            socket.on("newarticle",onNewArticle);
//            socket.connect();
//        }

        chittichatServices = retrofit.create(ChittichatServices.class);
        recyclerView = (RecyclerView) findViewById(R.id.articles_recycler_view);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        getArticle(token,topicId,"0_5");


    }

    private void getArticle(String token,String topicId,String range){
        Observable<List<Articles>> getArticles = chittichatServices.getResponseOnArticles(token,topicId,range);
        getArticles.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Articles>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Articles> articles) {
                try {
                    ArticleAdapter adapter = new ArticleAdapter(articles);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(0);
                }catch (Exception e){
                    Log.e("ArticleEx:",e.getMessage());
                }

//                Log.d("Chittichat_Articles",articles.get(0).get_id());
            }
        });

    }
    public void onaddImage(View view){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try{
            startActivityForResult(intent,PICK_IMAGE_REQUEST);//Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }catch (Exception e){
            Log.d("e",e.getMessage());
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPathFromURI(Uri contentUri) {
        //Will work only in kitkat......... and above...
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.i("ImageURI",uri.toString());
            postImage(uri);
        }
    }

    private void postArticle(ArticleInformation articleInformation ){
        Observable<ResponseMessage> getResponseOnArticleUpload = chittichatServices.getResponseOnPostArticle(articleInformation);
        getResponseOnArticleUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                Log.d("PostArticle","completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {
                    try {
                        Log.d("Article sent",responseMessage.getMessage());
                    }catch (Exception e){
                        Log.e("Article sent Ex:",e.getMessage());
                    }



            }
        });

    }


    private void postImage(Uri imageUri){
        String path = getPathFromURI(imageUri);
        if(path.equals("")){
                Log.d("imageUri",imageUri.toString());
                Log.d("imageUri.path",imageUri.getPath());
                Log.d("file path:","is null");
        }else{
            Log.d("file path",path);
            File file = new File(path);
            RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody mytoken = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody mtopicId = RequestBody.create(MediaType.parse("text/plain"), topicId);

            Observable<ResponseMessage> getResponseOnImageUpload = chittichatServices.getResponseOnPostImage(fbody,mytoken,mtopicId);
             getResponseOnImageUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
                 @Override
                 public void onCompleted() {
                     Log.d("PostImage","completed");
                 }

                 @Override
                 public void onError(Throwable e) {
                    Log.e("PostImage",e.getMessage());
                 }

                 @Override
                 public void onNext(ResponseMessage responseMessage) {
                     Log.d("Image",responseMessage.getMessage());
                 }
             });

        }
    }

    private void postVideo(Uri videoUri){
        File file = new File(videoUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("video",file.getName(),requestFile);
        String descriptionString = "This is video uploaded by user and user Id is......";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),descriptionString);
        Observable<ResponseMessage> getResponseOnVideoUpload = chittichatServices.getResponseOnPostVideo(body,description);
        getResponseOnVideoUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
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
    private  void postAudio(Uri audioUri){
        File file = new File(audioUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio",file.getName(),requestFile);
        String descriptionString = "This is audio uploaded by user and user Id is......";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),descriptionString);
        Observable<ResponseMessage> getResponseOnAudioUpload = chittichatServices.getResponseOnPostAudio(body,description);
        getResponseOnAudioUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
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


    private  Emitter.Listener onNewArticle  =  new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {

//                        Toast.makeText(getApplicationContext(),"kuch to hua",Toast.LENGTH_SHORT).show();
                        ;
                        Log.i("aabccccdsfds",data.getString("articleId"));
                        getArticle(token,topicId,"0_5");
                    }catch (Exception e) {
                        Log.e("Socket Exception",e.getMessage());
                    }
                }
            });
        }
    };


    public void onClickSend(View view){
        //upload your articles....
        String text = articleContent.getText().toString();
        articleContent.setText("");
        if(!text.equals("")){
            Log.d("Article",text);
            Log.d("TopicId",topicId+"abbb");
            ArticleInformation articleInformation = new ArticleInformation(token,topicId,text);
            postArticle(articleInformation);


        }

    }

}

class  ArticleInformation{
    String token,topic_id,article_content;

    public ArticleInformation(String token, String topicId, String article_content) {
        this.token = token;
        this.topic_id = topicId;
        this.article_content = article_content;
    }
}
class  MediaInformation{
    String token,topicId,mediatype;

    public MediaInformation(String token, String topicId, String mediatype) {
        this.token = token;
        this.topicId = topicId;
        this.mediatype = mediatype;
    }
}
class Articles{
    private String _id,createdOn,publishedBy,content_type,article_content;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getArticle_content() {
        return article_content;
    }

    public void setArticle_content(String article_content) {
        this.article_content = article_content;
    }
}