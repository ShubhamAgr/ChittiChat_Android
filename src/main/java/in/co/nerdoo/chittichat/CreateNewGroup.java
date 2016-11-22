package in.co.nerdoo.chittichat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
    private  String selectedCategory;
    private EditText group_name, group_introduction,knockKnockQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);
        group_name=(EditText) findViewById(R.id.group_name);
        group_introduction = (EditText) findViewById(R.id.group_introduction);
        knockKnockQuestion = (EditText) findViewById(R.id.knockKnockQuestion);

        spinner =  (Spinner) findViewById(R.id.group_category_spinner);

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

        Observable<ResponseMessage> getResponseOnNewGroup = chittichatServices.getResponseOnNewGroup(newGroupInformation);
        subscription_first = getResponseOnNewGroup.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
                                                                                                                               Observer<ResponseMessage>() {
            @Override
            public void onCompleted() {
                Log.i("ChittiChat_Service","New Groups Request Done");
                subscription_first.unsubscribe();
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
    public void onClickcreateNewGroup(View view){
        if(!(group_name.getText().toString().equals("") && knockKnockQuestion.getText().toString().equals(""))){
            NewGroupInformation newGroupInformation = new NewGroupInformation(sharedPreferences.getString("ChittiChat_token","false"),group_name
                    .getText().toString(),group_introduction.getText().toString(),selectedCategory,knockKnockQuestion.getText().toString());
            requestCreateNewGroup(newGroupInformation);

        }else {
            Toast.makeText(getApplicationContext(),"Fill All Necessary Content",Toast.LENGTH_SHORT).show();
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
