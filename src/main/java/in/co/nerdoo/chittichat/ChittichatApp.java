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
    private static String baseurl;
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
        baseurl = "http://ec2-35-154-131-124.ap-south-1.compute.amazonaws.com";//"http://ec2-35-160-113-29.us-west-2.compute.amazonaws.com";
        // "http://192.168.43.91:3000/";
        chittichatAppInstance = this;
        mainAppComponent = DaggerMainAppComponent.builder().appModule(new AppModule(this)).storageModule(new StorageModule())
                .netModule(new NetModule(baseurl)).build();

    }


    @Override
    public void onTerminate(){
        super.onTerminate();
        Log.d("onTerminate","Invoked");

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
    public static String getBaseUrl(){
        return baseurl;
    }
    public MainAppComponent getMainAppComponent() {
        return mainAppComponent;
    }
}
