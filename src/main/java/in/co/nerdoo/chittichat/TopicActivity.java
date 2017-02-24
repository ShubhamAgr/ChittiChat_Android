package in.co.nerdoo.chittichat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TopicActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
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
    private static List<Articles> articlesList;
    private static RecyclerView recyclerView;
    private static Boolean ShowEdittext;
    public static Boolean isAdmin;
    private  static LinearLayout sendTextLayout;
    private  static ArticleAdapter articleAdapter;
    private static LinearLayoutManager manager;
    private static boolean isLoading,isEmpty,initEmit;
    private static int initialItem;
    private static int finalItem;
    private static final int PAGE_SIZE = 8;
    Subscription s1,s2,s3,s4,s5,s6,s7,s8;
    private static ImageButton addPhoto,deleteButton;
    private static ArrayList<String> id;
    public static HashSet<String>  deleteArticleIds;
    public static HashSet<Integer>  deletePositions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        articleContent = (EditText) findViewById(R.id.articleText);
        sendTextLayout = (LinearLayout)findViewById(R.id.linearLayout2) ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_articles);
        addPhoto = (ImageButton) findViewById(R.id.myaddphotbutton);
        deleteButton = (ImageButton) findViewById(R.id.deletebutton);
        toolbar.setTitle("Conversation");
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);
        id = new ArrayList<>();
        deleteArticleIds = new HashSet<>();
        deletePositions = new HashSet<>();
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            topicId = null;
        }else{
            topicId = extras.getString("TopicId");
            ShowEdittext = extras.getBoolean("ShowEdittext");
            isAdmin = extras.getBoolean("isAdmin");
            token = sharedPreferences.getString("ChittiChat_token",null);
        }
        if(!ShowEdittext){
            sendTextLayout.setVisibility(View.GONE);
            addPhoto.setVisibility(View.GONE);
        }
        if(!initEmit){
            socket.on("newarticle",onNewArticle);
            initEmit = true;
        }

        if(!socket.connected()){
            socket.connect();
        }

        JSONObject joinRoom = new JSONObject();
        try{
            joinRoom.put("token",sharedPreferences.getString("ChittiChat_token",null));
            joinRoom.put("room_id",topicId);
            socket.emit("joinRoom",joinRoom);
        }catch (JSONException e){
            Log.e("problem",e.getMessage());
        }

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("ChittiChat_token","null"));
                socket.emit("authorize",jsonObject);
            }catch (JSONException je){
                Log.e("Exception_Authorization",je.getMessage());
            }
        chittichatServices = retrofit.create(ChittichatServices.class);
        recyclerView = (RecyclerView) findViewById(R.id.articles_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(null);
        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(recyclerviewOnScrollListener);
        isLoading =   true;
        isEmpty = false;
        initialItem = 0;
        finalItem = 12;
        getInitialArticle(token,topicId,initialItem+"_"+finalItem);

    }
    public static void onLongPressedArticle(){
        deleteButton.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void askpermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        }

    }


    @Override
    public  void onPause(){
        super.onPause();
//        socket.connect();
        JSONObject joinRoomrequest = new JSONObject();
        try {
            joinRoomrequest.put("room_id", topicId);
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
    public void  onStop(){
        super.onStop();

    }
    @Override
    public void onResume(){
        super.onResume();
//        socket.connect();
        JSONObject joinRoom = new JSONObject();
        try{
            joinRoom.put("token",sharedPreferences.getString("ChittiChat_token",null));
            joinRoom.put("room_id",topicId);
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
    public  void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onBackPressed()
    {
        JSONObject joinRoomrequest = new JSONObject();
        try {
            joinRoomrequest.put("room_id", topicId);
            joinRoomrequest.put("token",sharedPreferences.getString("ChittiChat_token","null"));
            socket.emit("leaveRoom", joinRoomrequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }
    private void getInitialArticle(String token,String topicId,String range){

            Observable<List<Articles>> getInitialArticles = chittichatServices.getResponseOnArticles(token,topicId,range);
            s1 = getInitialArticles.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(articles->{

                if(articles.isEmpty()){
                    isEmpty = true;
                }
                articlesList = articles;
                Log.d("articleSize",String.valueOf(articlesList.size()));
                articleAdapter = new ArticleAdapter(articlesList);
                recyclerView.setAdapter(articleAdapter);
                recyclerView.scrollToPosition(0);
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


        }

    private void getArticle(String token,String topicId,String range){

            Observable<List<Articles>> getArticles = chittichatServices.getResponseOnArticles(token,topicId,range);
            s2 = getArticles.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(articles->{
                Log.d("articleSize",String.valueOf(articles.size())+String.valueOf(articles.size()%10 != 0));
                if(articles.isEmpty() || articles.size()%12 != 0){
                    isEmpty = true;
                }
                Parcelable recyclerViewState;
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
//                articlesList = articles;
                articlesList.addAll(articles);

                articleAdapter.notifyItemRangeInserted(initialItem,articles.size());
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
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
    private  void getArticleByArticleId(String articleId){

            Observable<List<Articles>> getArticle = chittichatServices.getArticles(articleId);
            s3 =  getArticle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(articles->{
                if(articlesList != null){
                    Log.d("newArticle",articles.get(0).get_id());
//                getUsernameByUserId(articles.get(0));
                    articlesList.add(0,articles.get(0));
                }else {
                    articlesList = articles;
                }
                articleAdapter.notifyItemInserted(0);
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
    public void onaddImage(View view){
        try{
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            startActivityForResult(pickIntent, PICK_IMAGE_REQUEST);

        }catch (Exception e){
            Log.d("e",e.getMessage());
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Log.d("path",Environment.getExternalStorageDirectory() + "/" + split[1]);
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }


            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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
        s5 = getResponseOnArticleUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
            Log.d("message",responseMessage.getMessage());
            s5.unsubscribe();
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

    private void deleteArticle(String article_id){
        Observable<ResponseMessage> deleteArticle = chittichatServices.deleteArticle(article_id);
        deleteArticle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage -> {
          Log.i("responseOnDelete",responseMessage.getMessage());
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

    private void postImage(Uri imageUri){
        String path = getPathFromURI(this,imageUri);
        if(path.equals("")){
                Log.d("imageUri",imageUri.toString());
                Log.d("imageUri.path",imageUri.getPath());
                Log.d("file path:","is null");
        }else{
            Log.d("file path",path);
            File file = new File(path);


           File CompressedImageFile = new Compressor.Builder(this)
                    .setMaxWidth(2048)
                    .setMaxHeight(1024)
                    .setQuality(60)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToFile(file);
//            File CompressedImageFile = Compressor.getDefault(this).compressToFile(file);

            RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), CompressedImageFile);
            RequestBody mytoken = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody mtopicId = RequestBody.create(MediaType.parse("text/plain"), topicId);
            RequestBody musername = RequestBody.create(MediaType.parse("text/plain"),sharedPreferences.getString("first_name","null"));

            Observable<ResponseMessage> getResponseOnImageUpload = chittichatServices.getResponseOnPostImage(fbody,mytoken,mtopicId,musername);
          s6 = getResponseOnImageUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
              Log.d("Image",responseMessage.getMessage());
              s6.unsubscribe();
          },throwable -> {

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
        s7 = getResponseOnVideoUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{s7.unsubscribe();});

    }
    private  void postAudio(Uri audioUri){
        File file = new File(audioUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio",file.getName(),requestFile);
        String descriptionString = "This is audio uploaded by user and user Id is......";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),descriptionString);
        Observable<ResponseMessage> getResponseOnAudioUpload = chittichatServices.getResponseOnPostAudio(body,description);
        s8 = getResponseOnAudioUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                                  Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                s8.unsubscribe();
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
//            JSONObject data = (JSONObject) args[0];
//            try {
//                getArticleByArticleId(data.getString("articleId"));
//                socket.emit("",new JSONObject("abc","abc"));
//            }catch (Exception e) {
//                Log.e("Socket Exception",e.getMessage());
//            }
//           runOnUiThread(new Runnable() {
//               @Override
//               public void run() {
                   JSONObject data = (JSONObject) args[0];
                   try {
                       getArticleByArticleId(data.getString("articleId"));
                   }catch (Exception e) {
                       Log.e("Socket Exception",e.getMessage());
                   }
               }
//           });

//        }
    };

    public void onClickDelete(View view){
        Iterator<String> iterator = deleteArticleIds.iterator();
        Iterator<Integer> iterator1 = deletePositions.iterator();
        TreeSet<Integer> treeSet = new TreeSet<>(Collections.reverseOrder());
        treeSet.addAll(deletePositions);
        Iterator<Integer> iterator2 = treeSet.iterator();
        while (iterator.hasNext()){
            deleteArticle(iterator.next());
            int position = iterator2.next();
            articlesList.remove(position);
            articleAdapter.notifyItemRemoved(position);
        }
        deleteArticleIds.clear();
        deletePositions.clear();
        treeSet.clear();
        articleAdapter.notifyDataSetChanged();
        ArticleAdapter.isAlreadyLongPressed = false;
        deleteButton.setVisibility(View.GONE);
    }

    public void onClickSend(View view){
        //upload your articles....
        String text = articleContent.getText().toString();
        articleContent.setText("");
        if(!text.equals("")){
            Log.d("Article",text);
            Log.d("TopicId",topicId+"abbb");
            Log.d("texts",text);
            ArticleInformation articleInformation = new ArticleInformation(token,topicId,sharedPreferences.getString("first_name","unknown"),text);
            postArticle(articleInformation);
            recyclerView.scrollToPosition(0);
        }

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
                    TopicActivity.isLoading = true;

                    getArticle(token,topicId,initialItem+"_"+finalItem);
                }
            }
        }
    };

}

class  ArticleInformation{
    String token,topic_id,marticle,username;

    public ArticleInformation(String token, String topicId, String username,String article_content) {
        this.token = token;
        this.topic_id = topicId;
        this.marticle= article_content;
        this.username = username;
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
    private String _id,username,created_on,published_by,publisher_name,content_type,article_content;
    private boolean isSelected;

    public  void setSelected(boolean selected){
        isSelected = selected;
    }
    public boolean isSelected(){
        return isSelected;
    }
    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedOn() {
        return created_on;
    }

    public void setCreatedOn(String createdOn) {
        this.created_on = created_on;
    }

    public String getPublishedBy() {
        return published_by;
    }

    public void setPublishedBy(String publishedBy) {
        this.published_by = published_by;
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

class Username{
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
}