package in.co.nerdoo.chittichat;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shubham on 15/10/16.
 */

@Module
public class StorageModule {
    public StorageModule(){

    }
    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(ChittichatApp application){
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Manager provideManager(ChittichatApp application){
        Manager manager = null;
        try {
            manager = new Manager(new AndroidContext(application),Manager.DEFAULT_OPTIONS);
            manager.enableLogging("Sync", Log.VERBOSE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

    @Provides
    @Singleton
    Database provideDatabase(Manager manager){
           Database database = null;
        try {
            database = manager.getExistingDatabase("com.chittichat.chittichat");
            if(database != null){
                return  database;
            }else{
                database = manager.getDatabase("com.chittichat.chittichat");
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return database;
    }

    @Provides @Named("Document1") @Singleton
    Document getDocumentId(Database database,SharedPreferences sharedPreferences){
        Document document = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String document1_id = sharedPreferences.getString("Document1_id",null);
        if(document1_id == null){
            document = database.createDocument();
            document1_id =  document.getId();
            editor.putString("Document1_id",document1_id);
            editor.apply();
        }else{
            document = database.getDocument(document1_id);
        }
        return document;
    }
    @Provides @Named("GroupDocument") @Singleton
    Document newGroupDocument(Database database,SharedPreferences sharedPreferences) {
        Document groupDocument = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String document1_id =  sharedPreferences.getString("groupDocument_id",null);
        if(document1_id == null){
            groupDocument = database.createDocument();
            document1_id =  groupDocument.getId();
            editor.putString("groupDocument_id",document1_id);
            editor.apply();
        }else{
            groupDocument = database.getDocument(document1_id);
        }
        return groupDocument;
    }
    @Provides @Named("TopicDocument") @Singleton
    Document newTopicDocument(Database database,SharedPreferences sharedPreferences) {
        Document TopicDocument = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String document1_id =  sharedPreferences.getString("topicDocument_id",null);
        if(document1_id == null){
            TopicDocument = database.createDocument();
            document1_id =  TopicDocument.getId();
            editor.putString("groupDocument_id",document1_id);
            editor.apply();
        }else{
            TopicDocument = database.getDocument(document1_id);
        }
        return TopicDocument;
    }
    @Provides @Named("ArticleDocument") @Singleton
    Document newArticleDocument(Database database,SharedPreferences sharedPreferences) {
        Document ArticleDocument = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String document1_id =  sharedPreferences.getString("articleDocument_id",null);
        if(document1_id == null){
            ArticleDocument = database.createDocument();
            document1_id =  ArticleDocument.getId();
            editor.putString("groupDocument_id",document1_id);
            editor.apply();
        }else{
            ArticleDocument = database.getDocument(document1_id);
        }
        return ArticleDocument;
    }
}
