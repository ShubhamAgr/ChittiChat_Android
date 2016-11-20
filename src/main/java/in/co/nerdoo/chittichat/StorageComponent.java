package in.co.nerdoo.chittichat;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shubham on 15/10/16.
 */
@Singleton
@Component(modules = {AppModule.class,StorageModule.class})
public interface StorageComponent {
    void inject(MainActivity mainActivity);
}
