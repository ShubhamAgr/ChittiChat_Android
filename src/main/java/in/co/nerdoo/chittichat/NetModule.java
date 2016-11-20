package in.co.nerdoo.chittichat;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.net.URISyntaxException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shubham on 15/10/16.
 */
@Module
public class NetModule {
    String baseUrl;
    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    //okhttp cache
    @Provides @Singleton
    Cache providesOkHttp(ChittichatApp application) {
        int cacheSize = 10*1024*1024;//10MiB
        Cache cache = new Cache(application.getCacheDir(),cacheSize);
        return  cache;
    }

    @Provides @Singleton
    Gson providesGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides @Singleton
    OkHttpClient providesHttpClient(Cache cache) {
        OkHttpClient client = new OkHttpClient();
//        client.setCache(cache);
        return  client;
    }

    @Provides @Singleton
    Retrofit provideRetrofit(Gson gson,OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory
                .create(gson)).baseUrl
                (baseUrl)
                .client(okHttpClient).build();
        return retrofit;
    }

    @Provides @Singleton
    Socket provideSocket(){
        Socket socket = null;
        try {
            socket = IO.socket(baseUrl);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("Event_Connect","socket connected");
                }
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });
            socket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("Event_Disconnect","socket disconnected");
                }
            });
            socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });
            socket.on("newimagenotify",new Emitter.Listener(){
               @Override
                public void call(Object... args){

               }
            });
            socket.on("newarticlenotify",new Emitter.Listener(){
                @Override
                public void call(Object... args){

                }
            });
            socket.on("newvideosnotify",new Emitter.Listener(){
                @Override
                public void call(Object... args){

                }
            });
            socket.on("newaudionotify",new Emitter.Listener(){
                @Override
                public void call(Object... args){

                }
            });
            socket.on("acceptednotify",new Emitter.Listener(){
               @Override
                public void call(Object... args){

               }
            });

            socket.on("onJoinResponse", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });

            socket.on("onLeaveResponse", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });

            socket.on("onKnockknockQuestion", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });

            socket.connect();

        }
        catch (URISyntaxException uriException){
            Log.e("URI Exception", String.valueOf(uriException));
        }
        return socket;
    }
}
