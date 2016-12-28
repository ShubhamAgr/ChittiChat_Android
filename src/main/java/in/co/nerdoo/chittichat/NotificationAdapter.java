package in.co.nerdoo.chittichat;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shubham on 17/12/16.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<groupRequestsNotification> groupRequests;
    private static final int GROUP_REQUEST_NOTIFICATION = 0;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context viewContext;
        TextView requests_username;
        TextView requests_answer;
        Button  accept,deny;
        public ViewHolder(View v){
            super(v);
            viewContext = v.getContext();
            requests_answer = (TextView) v.findViewById(R.id.requests_answer);
            requests_username = (TextView) v.findViewById(R.id.requests_username);
            accept = (Button) v.findViewById(R.id.accept_request);
            deny = (Button) v.findViewById(R.id.deny_request);
        }
        @Override
        public void onClick(View v) {

        }
    }

    public NotificationAdapter(List<groupRequestsNotification> groupRequests){
        this.groupRequests = groupRequests;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotificationAdapter.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case GROUP_REQUEST_NOTIFICATION:
                View v1 = layoutInflater.inflate(R.layout.request_card, parent, false);
                viewHolder = new ViewHolder(v1);
                break;
            default:
                View v = layoutInflater.inflate(R.layout.request_card, parent, false);
                viewHolder = new ViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (holder.getItemViewType()){
            case(GROUP_REQUEST_NOTIFICATION):

                holder.requests_username.setText(groupRequests.get(position).getUsername());
                holder.requests_answer.setText(groupRequests.get(position).getKnock_knock_answer());
                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NotificationActivity.onAcceptRequest(position);

                    }
                });
                holder.deny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NotificationActivity.onDenyRequest(position);
                    }
                });
                break;
            default:
                holder.requests_username.setText(groupRequests.get(position).getBy());
                holder.requests_answer.setText(groupRequests.get(position).getKnock_knock_answer());
        }
    }
    @Override
    public int getItemViewType(int position){
        if(groupRequests.get(position) instanceof  groupRequestsNotification){
            return GROUP_REQUEST_NOTIFICATION;
        }
        return  -1;
    }

    @Override
    public int getItemCount() {
        return groupRequests.size();
    }


}


