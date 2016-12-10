package in.co.nerdoo.chittichat;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.couchbase.lite.Document;


import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;


import io.socket.client.Socket;
import retrofit2.Retrofit;

/**
 * Created by shubham on 15/10/16.
 */

public class ChittichatApp extends Application {
    private  static ChittichatApp chittichatAppInstance;
    private static MainAppComponent mainAppComponent;
    @Inject @Named("Document1")
    Document document_one;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Socket socket;


    @Override
    public void onCreate(){
     super.onCreate();

        chittichatAppInstance = this;
        mainAppComponent = DaggerMainAppComponent.builder().appModule(new AppModule(this)).storageModule(new StorageModule())
                .netModule(new NetModule("http://ec2-35-160-113-29.us-west-2.compute.amazonaws.com")).build();

    }


    @Override
    public void onTerminate(){
        super.onTerminate();

        if(socket.connected()){
            //send socket to delete user id
            try{

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("Chittichat_token","null"));
                socket.emit("app_close",jsonObject);

            }catch (JSONException je){

                Log.e("app_close exception",je.getMessage());

            }

        }else{
            socket.connect();
            try{

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token",sharedPreferences.getString("ChittiChat_token","null"));
                socket.emit("app_close",jsonObject);

            }catch (JSONException je){

                Log.e("app_close exception",je.getMessage());

            }
        }

    }


    @Override
    public void onLowMemory(){
        super.onLowMemory();
    }

    public static synchronized  ChittichatApp getInstance(){
        return chittichatAppInstance;
    }

    public void setConnectivityListener(ConnectivityReciever.ConnectivityReceiverListener listener) {
        ConnectivityReciever.connectivityReceiverListener = listener;
    }
    public MainAppComponent getMainAppComponent() {
        return mainAppComponent;
    }
}
