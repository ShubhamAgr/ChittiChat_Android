package in.co.nerdoo.chittichat;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by shubham on 15/10/16.
 */
public class ChittichatApp extends Application {
    private NetComponent netComponent;
    private StorageComponent storageComponent;
    private  static ChittichatApp chittichatAppInstance;
    private static MainAppComponent mainAppComponent;
    @Override
    public void onCreate(){
     super.onCreate();
     Fabric.with(this, new Crashlytics());
        chittichatAppInstance = this;
        mainAppComponent = DaggerMainAppComponent.builder().appModule(new AppModule(this)).storageModule(new StorageModule())
                .netModule(new NetModule("http://95df295c.ngrok.io")).build();

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
