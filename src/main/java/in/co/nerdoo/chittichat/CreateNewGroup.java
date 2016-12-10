package in.co.nerdoo.chittichat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateNewGroup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;
    Subscription subscription_first;
    private static ChittichatServices chittichatServices;
    Spinner spinner;
    private ImageButton selectGroupImage;
    private  String selectedCategory;
    private int PICK_IMAGE_REQUEST = 1;
    private int PIC_CROP = 2;
    Uri uploadImageUri;
    private  static File file;
    private EditText group_name, group_introduction,knockKnockQuestion;
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
        subscription_first = getResponseOnNewGroup.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseOnNewGroup>() {
            @Override
            public void onCompleted() {
                Log.i("ChittiChat_Service","New Groups Request Done");
                subscription_first.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseOnNewGroup responseOnNewGroup) {

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
                        Log.d("abcdefghij",responseOnNewGroup.getGroupId());
                        postGroupImage(responseOnNewGroup.getGroupId());
                    }
                }


            }
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
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 150);


        try{
            intent.putExtra("return-data", true);

            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);//Intent.createChooser(intent, "Select

        }catch (Exception e){
            Log.d("e",e.getMessage());
        }


    }
    private void performCrop(Uri picUri) {
        try {

            String path = getPathFromURI(picUri);
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

    private void postGroupImage(String groupId){
        Log.d("hi","hi"+groupId);
        Log.d("myuploadImage",uploadImageUri.toString());
//        String mypath = getPathFromURI(uploadImageUri);
//        File file = new File(uploadImageUri.getPath());
//        Log.d("myfilePath",mypath);
        if(file == null){
            Log.d("imageUri",uploadImageUri.toString());
            Log.d("imageUri.path",uploadImageUri.getPath());
            Log.d("file path:","is null");
        }else{
            Log.d("paths",uploadImageUri.getPath());
            RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody mytoken = RequestBody.create(MediaType.parse("text/plain"), sharedPreferences.getString("ChittiChat_token","false"));
            RequestBody mgroupId = RequestBody.create(MediaType.parse("text/plain"), groupId);

            Observable<ResponseMessage> getResponseOnImageUpload = chittichatServices.getResponseOnGroupImage(fbody,mytoken,mgroupId);
            getResponseOnImageUpload.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
                @Override
                public void onCompleted() {
                    Log.d("PostImage","completed");
                    if(file.exists())
                        file.delete();

                }

                @Override
                public void onError(Throwable e) {
                    Log.e("PostImage",e.getMessage());
                }

                @Override
                public void onNext(ResponseMessage responseMessage) {
                    Log.d("Image",responseMessage.getMessage());
                    if(responseMessage.getMessage().equals("successful")){
                        Intent intent = new Intent(CreateNewGroup.this,FirstActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
            });

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
