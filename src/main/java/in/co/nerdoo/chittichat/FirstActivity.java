    package in.co.nerdoo.chittichat;


    import android.content.ActivityNotFoundException;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.res.Resources;
    import android.graphics.Color;
    import android.graphics.Rect;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.speech.RecognizerIntent;
    import android.support.design.widget.AppBarLayout;
    import android.support.design.widget.CollapsingToolbarLayout;
    import android.support.v7.app.ActionBar;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.DefaultItemAnimator;
    import android.support.v7.widget.GridLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.util.TypedValue;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.Toast;

    import com.couchbase.lite.CouchbaseLiteException;
    import com.couchbase.lite.Database;
    import com.couchbase.lite.Document;
    import com.couchbase.lite.Manager;
    import com.couchbase.lite.UnsavedRevision;
    import com.facebook.login.LoginManager;
    import com.squareup.picasso.Picasso;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;
    import java.util.concurrent.ExecutionException;
    import java.util.concurrent.TimeUnit;

    import javax.inject.Inject;
    import javax.inject.Named;

    import io.socket.client.Socket;
    import retrofit2.Retrofit;
    import rx.Observable;
    import rx.Observer;
    import rx.Scheduler;
    import rx.Subscription;
    import rx.android.schedulers.AndroidSchedulers;
    import rx.schedulers.Schedulers;


    public class FirstActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityReceiverListener {
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
        private static RecyclerView recyclerView;
        private GroupCardAdapter adapter;
        private  static List<GroupsList> groupsList;
        private EditText chittichatsearch;
        private ImageButton mysearchbutton;
        private ImageButton mynotificationbutton;
        private ImageView profile_pic_ImageView;
        private Toolbar toolbar;
        private final int REQ_CODE_SPEECH_INPUT = 100;
        private Subscription subscription;
        ActionBar actionBar;

        private static GroupDetail newgroupDetail;
        private  static boolean menuEnabled;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_first);
                chittichatsearch =(EditText) findViewById(R.id.chittichatsearch);
                mysearchbutton = (ImageButton) findViewById(R.id.mysearchbutton);
                mynotificationbutton = (ImageButton) findViewById(R.id.mynotificationbutton);
                menuEnabled = true;
                toolbar = (Toolbar) findViewById(R.id.toolbarXX);
                toolbar.showOverflowMenu();
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
                actionBar.setHomeButtonEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);

                initCollapsingToolbar();

            profile_pic_ImageView = (ImageView)findViewById(R.id.profile_pic);

            ((ChittichatApp) getApplication()).getMainAppComponent().inject(this);

            recyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            chittichatServices = retrofit.create(ChittichatServices.class);


//            chittichatsearch = (EditText) findViewById(R.id.chittichatsearch);
//            chittichatsearch.setVisibility(View.GONE);


              Log.d("profile_pic_url",sharedPreferences.getString("profile_pic_url","default"));

                try{
                Picasso.with(getApplicationContext())
                        .load(sharedPreferences.getString("profile_pic_url","default")).fit()
                        .into(profile_pic_ImageView);}catch (Exception e) {
                    Log.e("Error",e.getMessage());

                }

                String ChittiChat_token = sharedPreferences.getString("ChittiChat_token","");
                if(!ChittiChat_token.equals("")){
                    getGroups(ChittiChat_token);
                }
            }

        private void getGroups(String token) {

            final Observable<List<GroupsList>> groupsList = chittichatServices.getResponseOnGroups(token);
            subscription = groupsList.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<GroupsList>>() {
                @Override
                public void onCompleted() {
                    Log.d("Response","completed");

                    subscription.unsubscribe();
                }
                @Override
                public void onError(Throwable e) {
                    Log.e("Response",e.getMessage());
                }
                @Override
                public void onNext(final List<GroupsList> groupsLists) {
                   Log.d("abcd",groupsLists.get(0).get_id()) ;
                    FirstActivity.groupsList = groupsLists;
//                    FirstActivity.groupsList = groupsLists;
//                    Iterator<GroupsList> it = groupsLists.listIterator();
//                    Map<String,List<GroupsList>> properties  = new HashMap<>();
//                    properties.put("groupsList",groupsLists);
                    try {
                        groupDocument.update(new Document.DocumentUpdater() {
                            @Override
                            public boolean update(UnsavedRevision newRevision) {
                              Map<String,Object> properties = newRevision.getProperties();
                                properties.put("groupsList",groupsLists);
                                newRevision.setProperties(properties);
                                return true;
                            }
                        });
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    for(int i = 0; i< groupsLists.size();i++){
                        JSONObject joinRoom = new JSONObject();
                        try{
                            joinRoom.put("room_id",groupsLists.get(i).get_id());
                            socket.emit("joinRoom",joinRoom);
                        }catch (JSONException e){
                            Log.e("problem",e.getMessage());
                        }
                        getGroupDetails(groupsLists.get(i).get_id());
                    }
                    GroupCardAdapter groupCardAdapter = new GroupCardAdapter(getApplicationContext(),groupDocument,groupsLists);
                    recyclerView.setAdapter(groupCardAdapter);





                }
            });

        }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.menu_first, menu);
            if(menuEnabled){
                for(int i=0; i< menu.size();i++){
                    menu.getItem(i).setVisible(true);
                }
            }else{
                for(int i=0; i< menu.size();i++){
                    menu.getItem(i).setVisible(false);
                }
            }
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
             switch (id){
                 case R.id.action_settings:
                     startActivity(new Intent(FirstActivity.this,SettingsActivity.class));
                     return true;
                 case R.id.newGroup:
                     startActivity(new Intent(FirstActivity.this,CreateNewGroup.class));
                     return true;
                 case R.id.editProfile:
                     startActivity(new Intent(FirstActivity.this,SettingsActivity.class));
                     return true;
                 case R.id.fb_logut:
                     LoginManager.getInstance().logOut();
                     return true;
                 case android.R.id.home:
                     menuEnabled = true;
                     invalidateOptionsMenu();
                     actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
                     actionBar.setHomeButtonEnabled(false);
                     actionBar.setDisplayHomeAsUpEnabled(false);
                     mynotificationbutton.setVisibility(View.VISIBLE);
                     mysearchbutton.setVisibility(View.VISIBLE);
                     chittichatsearch.setVisibility(View.GONE);
                     chittichatsearch.setText("");
                 default:
//                     Toast.makeText(getApplicationContext(),"Does not match any options",Toast.LENGTH_SHORT).show();
             }
            return super.onOptionsItemSelected(item);
        }
        private  void getGroupDetails(final String groupId) {
            Log.d("cccc",groupId);
            Observable<GroupDetail> getGroupDetails = chittichatServices.getResponseOnGroupDetail(sharedPreferences.getString("ChittiChat_token",
                    ""),groupId);
            getGroupDetails.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GroupDetail>() {
                @Override
                public void onCompleted() {
                    Log.d("message","Information fetched");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Error",e.getMessage().toString());
                    e.printStackTrace();
                }

                @Override
                public void onNext(GroupDetail groupDetail) {
                    //add each group detail to the database

                    try {
                        Map<String, Object> properties = new HashMap<>();
                        properties.putAll(groupDocument.getProperties());
                        properties.put(groupId+"_id",groupDetail.get_id());
                        properties.put(groupId+"name",groupDetail.getName());
                        properties.put(groupId+"motivation",groupDetail.getAbout());
                        properties.put(groupId+"pic_url",groupDetail.getPic_url());
                        try {
                            groupDocument.putProperties(properties);
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                    }catch (Exception e) {
                        Log.e("ERROR2",e.getMessage().toString());
//                        e.printStackTrace();
                    }
                }
            });
        }
        private  void search(String query) {
            Observable<ResponseMessage> getSearchResults = chittichatServices.getResponseOnSearch(query);
            getSearchResults.subscribeOn(Schedulers.newThread()).debounce(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe
                    (new Observer<ResponseMessage>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ResponseMessage responseMessage) {

                }
            });
        }

        @Override
        protected void onResume(){
            super.onResume();
            ((ChittichatApp) getApplication()).getInstance().setConnectivityListener(this);
        }


        @Override
        protected void onDestroy() {
            super.onDestroy();
            if(socket.connected()) {
                Iterator<GroupsList> it = groupsList.listIterator();
                while (it.hasNext()) {
                    JSONObject joinRoomrequest = new JSONObject();
                    try {
                        joinRoomrequest.put("room_id", it.next().get_id().toString());
                        socket.emit("leaveRoom", joinRoomrequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            Log.d("Network connection","Changed");
            if(isConnected){
                Toast.makeText(getApplicationContext(),String.valueOf("isConnected"),Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(), String.valueOf("isnotConnected"), Toast.LENGTH_LONG).show();
            }
        }

        private void createContent(){
            Map<String,Object> map = new HashMap<>();
            map.put("ss1","Sssss");
            try {
                document_one.putProperties(map);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }

        private void readContent(){
            Log.d("content",String.valueOf(document_one.getProperties()));

            Toast.makeText(getApplicationContext(),"abcde"+document_one.getProperties(),Toast.LENGTH_LONG).show();
        }



        private void checkConnection(){
            boolean isConnected = ConnectivityReciever.isConnected();
            if(isConnected){
                Toast.makeText(getApplicationContext(),String.valueOf("isConnected"),Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(),String.valueOf("isnotConnected"),Toast.LENGTH_LONG).show();
            }

            Log.d("IsConnected",String.valueOf(isConnected));
        }

        public void onClickSearchButton(View view) {

            menuEnabled = false;
            invalidateOptionsMenu();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            actionBar.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            mynotificationbutton.setVisibility(View.GONE);
            mysearchbutton.setVisibility(View.GONE);
            chittichatsearch.setVisibility(View.VISIBLE);
            chittichatsearch.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }

        public void onVoiceSearchClick(View view) {

        }

        private void promptSpeechInput() {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));
            try {
                startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);

            }catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();

            }
        }

        /**Receiving speech input**/
        @Override
        protected  void onActivityResult(int requestCode,int resultCode,Intent data ) {
            super.onActivityResult(requestCode,resultCode,data);
            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT:{
                    if(resultCode==RESULT_OK && null != data) {
                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        chittichatsearch.setText(result.get(0));

                    }
                    break;
                }
            }
        }


        /**
         * Initializing collapsing toolbar
         * Will show and hide the toolbar title on scroll
         */
        private void initCollapsingToolbar() {
            final CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//            collapsingToolbar.setTitle("Shubham");//setTo user name or First name.....


            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            appBarLayout.setExpanded(true);

            // hiding & showing the title when toolbar expanded & collapsed
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                        actionBar.hide();
                        collapsingToolbar.setTitle("Shubham");
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(getString(R.string.app_name));
                        actionBar.show();
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbar.setTitle("Shubham");
                        actionBar.hide();
                        isShow = false;
                    }
                }
            });
        }


        /**
         * RecyclerView item decoration - give equal margin around grid item
         */
        public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

            private int spanCount;
            private int spacing;
            private boolean includeEdge;

            public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
                this.spanCount = spanCount;
                this.spacing = spacing;
                this.includeEdge = includeEdge;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int column = position % spanCount; // item column

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            }
        }

        /**
         * Converting dp to pixel
         */
        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        }

    }



