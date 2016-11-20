package in.co.nerdoo.chittichat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
            Intent intent = new Intent(viewContext,TopicActivity.class);
            intent.putExtra("groupId",mDataset.get(getPosition()).getGroupId());
            viewContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


    public GroupCardAdapter(Context mycontext,Document group_document,List<GroupsList> groupsLists)
    {
        this.group_document = group_document;
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
        Map<String,Object> properties = group_document.getProperties();
        JSONObject jsonObject = null;
        try{

            String groupId = mDataset.get(position).getGroupId();
            jsonObject = (JSONObject) properties.get(groupId);
            Picasso.with(group_profile_context).load(jsonObject.getString("group_profile_url")).into(holder.group_profile_pic);
            holder.group_notification.setText(jsonObject.getString("group_notification"));
            holder.group_title.setText(jsonObject.getString("group_name"));

        }catch (Exception e){
            Log.e("GroupCardAdapterEx:",e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}