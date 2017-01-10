package in.co.nerdoo.chittichat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.couchbase.lite.Document;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;

/**
 * Created by shubham on 16/10/16.
 */
public class GroupCardAdapter extends RecyclerView.Adapter<GroupCardAdapter.ViewHolder> {
    private static List<GroupsList> mDataset;
    private static Context myContext;
    private Document group_document;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        // each data item is just a string in this case
        private ImageView group_profile_pic;
        private TextView group_title;
        private TextView group_notification;
        private Context viewContext;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            group_profile_pic = (ImageView) v.findViewById(R.id.group_profilePic);
            group_title = (TextView) v.findViewById(R.id.group_name);
            group_notification = (TextView) v.findViewById(R.id.group_notification);
            viewContext = v.getContext();
        }
        @Override
        public void onClick(final View v) {
            Intent intent = new Intent(viewContext,ShowTopics.class);
            intent.putExtra("groupId",mDataset.get(getPosition()).get_id());
            if(mDataset.get(getPosition()).getRole().equals("administrator")){
                intent.putExtra("isadmin",true);
                intent.putExtra("ShowEdittext",true);
            }else if(mDataset.get(getPosition()).getRole().equals("member")){
                intent.putExtra("isadmin",false);
                intent.putExtra("ShowEdittext",true);
            }
            else{
                intent.putExtra("isadmin",false);
                intent.putExtra("ShowEdittext",false);
            }
            viewContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


    public GroupCardAdapter(Context mycontext,List<GroupsList> groupsLists)
    {
        this.myContext = mycontext;
        mDataset = groupsLists;
    }


    @Override
    public GroupCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_card, parent, false);

        ViewHolder vh = new ViewHolder(v);


        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Context group_profile_context = holder.group_profile_pic.getContext();
//        Map<String,Object> properties = group_document.getProperties();
        JSONObject jsonObject = null;
        try{

            String groupId = mDataset.get(position).get_id();
            String pic_url= mDataset.get(position).get_id();//(String)properties.get(groupId+"pic_url");
            Log.d("picture url",pic_url);
            if(pic_url.equals("default")){
                Picasso.with(group_profile_context).load(R.drawable.group_icon_black).into(holder.group_profile_pic);
            }else{
                Picasso.with(group_profile_context).load(ChittichatApp.getBaseUrl()+"/images/"+pic_url).into(holder.group_profile_pic);
            }
            holder.group_notification.setEllipsize(TextUtils.TruncateAt.END);
            holder.group_notification.setMaxLines(1);
            holder.group_title.setEllipsize(TextUtils.TruncateAt.END);
            holder.group_title.setMaxLines(1);
            holder.group_notification.setText(mDataset.get(position).getGroup_about());//((String)properties.get(groupId+"motivation")));//properties.get(mDataset
            // .get
            // (position)
            // .get_id()
            // )));
            holder.group_title.setText(mDataset.get(position).getGroup_name());//(String)properties.get(groupId+"name"));

        }catch (Exception e){
            Log.e("GroupCardAdapterEx:",e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        if(mDataset == null){
            return 0;
        }else if(mDataset.isEmpty()){
            return 0;
        }else {
            return mDataset.size();
        }

    }
}