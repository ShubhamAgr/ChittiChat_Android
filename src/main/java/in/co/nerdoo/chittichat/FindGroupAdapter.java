package in.co.nerdoo.chittichat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

/**
 * Created by shubham on 19/2/17.
 */
public class FindGroupAdapter  extends RecyclerView.Adapter<FindGroupAdapter.ViewHolder> {
    private static List<Groups> groupsList;
    private boolean isAlreadyLongPressed;
    public  static HashSet<Integer> positionList;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private Context viewContext;
        private TextView topic_title;
        private TextView topic_description;

        public ViewHolder(View v) {
            super(v);
            topic_title = (TextView) v.findViewById(R.id.topic_title);
            topic_description = (TextView) v.findViewById(R.id.topic_Description);
            viewContext = v.getContext();
        }
    }

    public FindGroupAdapter(List<Groups> groupsList){
        this.groupsList = groupsList;
        isAlreadyLongPressed = false;
        positionList = new HashSet<>();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FindGroupAdapter.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v1 = layoutInflater.inflate(R.layout.topic_card, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Groups groups = groupsList.get(position);
        holder.itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                holder.itemView.setAlpha(groups.isSelected()?0.4f:1.0f);
                holder.itemView.setBackgroundColor(groups.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);

            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        holder.itemView.setOnClickListener(v1 -> {
            if(isAlreadyLongPressed){
                Log.d("Position",String.valueOf(position));
                isAlreadyLongPressed = true;
                groups.setSelected(!groups.isSelected());
                holder.itemView.setAlpha(groups.isSelected()?0.4f:1.0f);
                holder.itemView.setBackgroundColor(groups.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);

                if(groups.isSelected()){
                    positionList.add(position);
                }else{
                    positionList.remove(position);
                }
                if(positionList.isEmpty()){
                    isAlreadyLongPressed = false;
                    FindGroups.follow.setEnabled(false);
                }
            }else{
                Intent intent = new Intent(holder.viewContext,showUsersOrGroupsActivity.class);
                intent.putExtra("groupId",groupsList.get(position).get_id());
                intent.putExtra("question",groupsList.get(position).getKnock_knock_question());
                intent.putExtra("groupName",groupsList.get(position).getGroup_name());
                intent.putExtra("groupAbout",groupsList.get(position).getGroup_about());
                intent.putExtra("from","findGroups");
                holder.viewContext.startActivity(intent);
            }

        });
        holder.itemView.setOnLongClickListener(v -> {
            if(!isAlreadyLongPressed) {
                Log.d("Position", String.valueOf(position));
                isAlreadyLongPressed = true;
                groups.setSelected(!groups.isSelected());
                holder.itemView.setAlpha(groups.isSelected() ? 0.4f : 1.0f);
                holder.itemView.setBackgroundColor(groups.isSelected() ? Color.parseColor("#872f93ff") : Color.TRANSPARENT);
                positionList.add(position);
                FindGroups.follow.setEnabled(true);
                return true;
            }
            return false;
        });
        holder.topic_title.setText(groupsList.get(position).getGroup_name());
        holder.topic_description.setText(groupsList.get(position).getGroup_about());
    }

    @Override
    public int getItemCount() {
        return groupsList.size();

    }
}
