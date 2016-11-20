package in.co.nerdoo.chittichat;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shubham on 15/10/16.
 */
@Singleton
@Component(modules = {AppModule.class,NetModule.class})
public interface NetComponent {
 void inject(MainActivity mainActivity);

}
