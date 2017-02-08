package in.co.nerdoo.chittichat;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shubham on 24/10/16.
 */
@Singleton
@Component(modules = {AppModule.class,StorageModule.class,NetModule.class})
public interface MainAppComponent {
    void inject(LoginActivity loginActivity);
    void inject(FirstActivity firstActivity);
    void inject(TopicActivity topicActivity);
    void inject(SignupDialog signupDialog);
    void inject(ShowTopics showTopics);
    void inject(newTopicDialog newTopicDialog);
    void inject(CreateNewGroup createNewGroup);
    void inject(showUsersOrGroupsActivity mshowUsersOrGroupsActivity);
    void inject(GroupAddRequestDialog groupAddRequestDialog);
    void inject(NotificationActivity notificationActivity);
    void inject(NotificationAdapter notificationAdapter);
    void inject(AllGroupsFragment allGroupsFragment);
    void inject(ActiveGroupFragment activeGroupFragment);
    void inject(FollowingGroupFragment followingGroupFragment);
    void inject(MembersGroupFragment membersGroupFragment);
    void inject(SuggestionGroupFragment suggestionGroupFragment);
}
