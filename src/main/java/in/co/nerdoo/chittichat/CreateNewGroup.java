package in.co.nerdoo.chittichat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateNewGroup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;
    Subscription subscription_first,s2,s3;
    private static ChittichatServices chittichatServices;
    Spinner spinner;
    private ImageButton selectGroupImage;
    private  String selectedCategory;
    private int PICK_IMAGE_REQUEST = 1;
    private int PIC_CROP = 2;
    Uri uploadImageUri;
    private  static File file;
    private EditText group_name, group_introduction,knockKnockQuestion;
    private static CallbackManager callbackManager;
    private static ShareDialog shareDialog;
    private  static ResponseOnNewGroup myresponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        uploadImageUri=null;
        file = null;
        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);
        group_name=(EditText) findViewById(R.id.group_name);
        group_introduction = (EditText) findViewById(R.id.group_introduction);
        knockKnockQuestion = (EditText) findViewById(R.id.knockKnockQuestion);

        spinner =  (Spinner) findViewById(R.id.group_category_spinner);
        selectGroupImage = (ImageButton) findViewById(R.id.selectGroupImage);
        spinner.setOnItemSelectedListener(this);
        List<String> categories =  new ArrayList<>();
        categories.add("miscellaneous");
        categories.add("Entertaintment");
        categories.add("Social");
        categories.add("Education");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        askpermission();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(),"post shared",Toast.LENGTH_SHORT);
                postGroupImage(myresponse.getGroupId());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"post shared canceled",Toast.LENGTH_SHORT);
                postGroupImage(myresponse.getGroupId());
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_SHORT);
                postGroupImage(myresponse.getGroupId());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = parent.getItemAtPosition(position).toString();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedCategory = "miscellaneous";
    }
    private void requestCreateNewGroup(NewGroupInformation newGroupInformation){

        chittichatServices = retrofit.create(ChittichatServices.class);

        Observable<ResponseOnNewGroup> getResponseOnNewGroup = chittichatServices.getResponseOnNewGroup(newGroupInformation);
        subscription_first = getResponseOnNewGroup.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (responseOnNewGroup->{
                    if(uploadImageUri.toString().equals("")){
                        Log.d("abcdefghij","1");
                        Intent intent = new Intent(CreateNewGroup.this,FirstActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Log.d("abcaa",responseOnNewGroup.getMessage()+"ab");
                        if(responseOnNewGroup.getMessage().equals("unsuccessful")){
                            Log.d("abcdefghij","2");
                            Intent intent = new Intent(CreateNewGroup.this,FirstActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Log.d("abcdefghij","3");
                            myresponse = responseOnNewGroup;
                            Log.d("abcdefghij",responseOnNewGroup.getGroupId());
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent groupcontent = new ShareLinkContent.Builder()
                                        .setContentTitle("Hello there, Follow my Group \""+group_name.getText().toString()+"\" in ChittiChat")
                                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=in.co.nerdoo.com.chittichat.chittichat"))

                                        .build();
                                //.setContentUrl(Uri.parse("http://developers.facebook.com/android"))//give the link of chittichat application...


                                shareDialog.show(groupcontent);
                            }
//                        postGroupImage(responseOnNewGroup.getGroupId());
                        }
                    }
                    subscription_first.unsubscribe();

                },throwable -> {
                    if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                        Log.e("error",((HttpException) throwable).response().errorBody().toString());

                    }
                    if (throwable instanceof IOException) {
                        // A network or conversion error happened
                    }
                    subscription_first.unsubscribe();
                });
    }
    public void onClickcreateNewGroup(View view){
        if(!(group_name.getText().toString().equals("") && knockKnockQuestion.getText().toString().equals(""))){
            NewGroupInformation newGroupInformation = new NewGroupInformation(sharedPreferences.getString("ChittiChat_token","false"),group_name
                    .getText().toString(),group_introduction.getText().toString(),selectedCategory,knockKnockQuestion.getText().toString());
            requestCreateNewGroup(newGroupInformation);

        }else {
            Toast.makeText(getApplicationContext(),"Fill All Necessary Content",Toast.LENGTH_SHORT).show();
        }

    }

    public Uri getImageUrifrombitmap(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void onClickSelectImageButton(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 150);


        try{
            intent.putExtra("return-data", true);

            startActivityForResult(intent,PICK_IMAGE_REQUEST);//Intent.createChooser(intent, "Select//Intent.createChooser(intent,"Select Picture")

        }catch (Exception e){
            Log.d("e",e.getMessage());
        }


    }
    private void performCrop(Uri picUri) {
        try {

            String path = getPath(this,picUri);//getPathFromURI(picUri);
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            File f = new File(path);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");

            cropIntent.putExtra("crop", "true");

            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);

            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);


            cropIntent.putExtra("return-data", true);

            startActivityForResult(cropIntent, PIC_CROP );
        }

        catch (ActivityNotFoundException anfe) {

            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
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

        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
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



    private void postGroupImage(String groupId){
        Toast.makeText(getApplicationContext(),"Image uploading",Toast.LENGTH_SHORT).show();
        Log.d("hi","hi"+groupId);
        Log.d("myuploadImage",uploadImageUri.toString());
        if(file == null){
            Log.d("imageUri",uploadImageUri.toString());
            Log.d("imageUri.path",uploadImageUri.getPath());
            Log.d("file path:","is null");
        }else{
            Log.d("paths",uploadImageUri.getPath());
            File compressedImageFile = Compressor.getDefault(this).compressToFile(file);
            RequestBody fbody = RequestBody.create(MediaType.parse("image/*"),compressedImageFile);
            RequestBody mytoken = RequestBody.create(MediaType.parse("text/plain"), sharedPreferences.getString("ChittiChat_token","false"));
            RequestBody mgroupId = RequestBody.create(MediaType.parse("text/plain"), groupId);

            Observable<ResponseMessage> getResponseOnImageUpload = chittichatServices.getResponseOnGroupImage(fbody,mytoken,mgroupId);
            s2 = getResponseOnImageUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(responseMessage->{
                Log.d("Image",responseMessage.getMessage());
                if(responseMessage.getMessage().equals("successful")){
                    Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                    if(file.exists())
                        file.delete();
                    s2.unsubscribe();
                    Intent intent = new Intent(CreateNewGroup.this,FirstActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);


                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
             performCrop(uri);
        }
        if (requestCode == PIC_CROP) {
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                uploadImageUri = getImageUrifrombitmap(this,selectedBitmap);
                Log.i("ImageURI",uploadImageUri.toString()+ uploadImageUri.toString());
                selectGroupImage.setImageBitmap(selectedBitmap);
                //if external storage available
                String root = Environment.getExternalStorageDirectory().toString();
//                String root = Environment.getDownloadCacheDirectory().toString();
                File mydir = new File(root+"/croped_images");
                mydir.mkdirs();
                Random generator = new Random();
                int n = 10000;
                n= generator.nextInt(n);
                String fname = ".Image-"+n+".jpg";
                file = new File(mydir,fname);
                if(file.exists()) file.delete();
                try{
                    FileOutputStream out = new FileOutputStream(file);
                    selectedBitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                    out.flush();
                    out.close();
                }catch (IOException e){
                    Log.d("Exception",e.getMessage());
                }

            }
        }
        }
    }

class NewGroupInformation{
    String token;String group_name;String group_introduction;String group_category;String knock_knock_question;

    public NewGroupInformation(String token, String group_name, String group_introduction, String group_category, String knock_knock_question) {
        this.token = token;
        this.group_name = group_name;
        this.group_introduction = group_introduction;
        this.group_category = group_category;
        this.knock_knock_question = knock_knock_question;
    }
}
class ResponseOnNewGroup{
    String message,_id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroupId() {
        return _id;
    }

    public void setGroupId(String groupId) {
        this._id = groupId;
    }
}
