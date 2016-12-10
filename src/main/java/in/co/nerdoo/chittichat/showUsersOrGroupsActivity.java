package in.co.nerdoo.chittichat;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import in.co.nerdoo.chittichat.R;

public class showUsersOrGroupsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users_or_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    public void onClickfab(View view){
        Toast.makeText(getApplicationContext(),"abcde",Toast.LENGTH_SHORT).show();
        DialogFragment dialogFragment = new GroupAddRequestDialog();
        dialogFragment.show(getFragmentManager(),"knock_knock");
    }
}
