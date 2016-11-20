package in.co.nerdoo.chittichat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.http.Body;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateNewGroup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @Inject
    Retrofit retrofit;
    private static ChittichatServices chittichatServices;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);


        spinner =  (Spinner) findViewById(R.id.group_category_spinner);

//        spinner.setOnItemClickListener();
        spinner.setOnItemSelectedListener(this);
        List<String> categories =  new ArrayList<>();
        categories.add("Entertaintment");
        categories.add("Social");
        categories.add("Education");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void createnewGroup(String token,String group_name,String group_introduction,String group_category,String knock_knock_question){
        chittichatServices = retrofit.create(ChittichatServices.class);
        NewGroupInformation newGroupInformation = new NewGroupInformation(token,group_name,group_introduction,
                group_category,knock_knock_question);
        Observable<ResponseMessage> getResponseOnNewGroup = chittichatServices.getResponseOnNewGroup(newGroupInformation);
        getResponseOnNewGroup.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                Log.i("ChittiChat_Service","New Groups Request Done");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseMessage responseMessage) {
                //change the activity..
                Intent intent = new Intent(CreateNewGroup.this,FirstActivity.class);
                startActivity(intent);

            }
        });
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
