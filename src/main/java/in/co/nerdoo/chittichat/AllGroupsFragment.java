package in.co.nerdoo.chittichat;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.socket.client.Socket;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AllGroupsFragment extends Fragment {
    @Inject
    Manager manager;
    @Inject
    Database database;
    @Inject @Named("Document1")
    Document document_one;
    @Inject @Named("GroupDocument")
    Document groupDocument;
    @Inject
    Socket socket;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    private static ChittichatServices chittichatServices;
    private GroupCardAdapter adapter;
    private  static List<GroupsList> groupsList;
    private GroupCardAdapter groupCardAdapter;
    private  static Subscription subscription,s1,s2,s3;

    private static RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((ChittichatApp) getActivity().getApplication()).getMainAppComponent().inject(this);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.groups_recycler_view);
//        callRecyclerProperties_one();
        return inflater.inflate(R.layout.fragment_all_groups, container, false);
    }

    private  void callRecyclerProperties_one(){
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);

//            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
//
    private  void callRecyclerProperties_two(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void getGroups(String token) {

        final Observable<List<GroupsList>> groupsList = chittichatServices.getResponseOnGroups(token);
        subscription = groupsList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(groupsLists->{
            AllGroupsFragment.groupsList = groupsLists;

//                try {
//                    groupDocument.update(new Document.DocumentUpdater() {
//                        @Override
//                        public boolean update(UnsavedRevision newRevision) {
//                            Map<String,Object> properties = newRevision.getProperties();
//                            properties.put("groupsList",groupsLists);
//                            newRevision.setProperties(properties);
//                            return true;
//                        }
//                    });
//                } catch (CouchbaseLiteException e) {
//                    e.printStackTrace();
//                }
//                for(int i = 0; i< groupsLists.size();i++){
//                    getGroupDetails(groupsLists.get(i).get_id());
//                }
            callRecyclerProperties_one();
            groupCardAdapter = new GroupCardAdapter(getActivity(),groupsLists);
            if(recyclerView.getAdapter()  == null){
                recyclerView.setAdapter(groupCardAdapter);
            }else if(recyclerView.getAdapter() instanceof GroupCardAdapter){
                groupCardAdapter.notifyDataSetChanged();
            }else{
                recyclerView.swapAdapter(groupCardAdapter,false);//make true
            }

            subscription.unsubscribe();
        },throwable -> {
            if(throwable instanceof HttpException) {
//                ((HttpException) throwable).code() == 400;
                Log.e("error",((HttpException) throwable).response().errorBody().toString());
            }
            if (throwable instanceof IOException) {
                // A network or conversion error happened
            }
            Log.e("error",throwable.getMessage());
        });


    }


}

//
//
//class GroupSearchResult{
//    String _id;
//    String group_name;
//    String group_about;
//    String knock_knock_question;
//
//    public String getGroup_about() {
//        return group_about;
//    }
//
//    public void setGroup_about(String group_about) {
//        this.group_about = group_about;
//    }
//
//    public String getKnock_knock_question() {
//        return knock_knock_question;
//    }
//
//    public void setKnock_knock_question(String knock_knock_question) {
//        this.knock_knock_question = knock_knock_question;
//    }
//
//    public String get_id() {
//        return _id;
//    }
//
//    public void set_id(String _id) {
//        this._id = _id;
//    }
//
//    public String getGroup_name() {
//        return group_name;
//    }
//
//    public void setGroup_name(String group_name) {
//        this.group_name = group_name;
//    }
//}
//class SearchRequest{
//    String query;
//
//    public SearchRequest(String query) {
//        this.query = query;
//    }
//}





