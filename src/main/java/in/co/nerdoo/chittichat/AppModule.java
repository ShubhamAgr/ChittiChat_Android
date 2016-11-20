package in.co.nerdoo.chittichat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shubham on 15/10/16.
 */
@Module
public class AppModule {
    ChittichatApp application;

    public AppModule(ChittichatApp application){
        this.application = application;
    }
    @Provides
    @Singleton
    ChittichatApp providesApplication(){
        return  application;
    }
}
