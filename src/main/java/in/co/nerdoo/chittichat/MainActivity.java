package in.co.nerdoo.chittichat;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityReceiverListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkConnection();
    }

    private void checkConnection(){
        boolean isConnected = ConnectivityReciever.isConnected();
        Log.d("IsConnected",String.valueOf(isConnected));
    }

    @Override
    protected void onResume(){
        super.onResume();
        ChittichatApp.getInstance().setConnectivityListener(this);
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d("IsConnected",String.valueOf(isConnected));

    }
}
