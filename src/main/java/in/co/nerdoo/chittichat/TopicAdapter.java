package in.co.nerdoo.chittichat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shubham on 15/11/16.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private static List<Topics> topics;
    private  static Boolean ShowEdittext;
    private final int TOPICS_WITH_ARTICLE=0;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context viewContext;
        private TextView topic_title;
        private TextView topic_description;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            topic_title = (TextView) v.findViewById(R.id.topic_title);
            topic_description = (TextView) v.findViewById(R.id.topic_Description);
            viewContext = v.getContext();
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(viewContext,TopicActivity.class);
            intent.putExtra("TopicId",topics.get(getPosition()).get_id());
            intent.putExtra("ShowEdittext",ShowEdittext);
            viewContext.startActivity(intent);
        }
    }


        public TopicAdapter(List<Topics> topics,Boolean ShowEdittext) {
            this.topics = topics;
            this.ShowEdittext = ShowEdittext;
        }


        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            TopicAdapter.ViewHolder viewHolder;
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            switch (viewType){
                case TOPICS_WITH_ARTICLE:
                    View v1 = layoutInflater.inflate(R.layout.topic_card, parent, false);
                    viewHolder = new ViewHolder(v1);
                    break;
                default:
                    View v = layoutInflater.inflate(R.layout.topic_card, parent, false);
                    viewHolder = new ViewHolder(v);
                    break;
            }
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            switch (holder.getItemViewType()){
                case TOPICS_WITH_ARTICLE:
                    holder.topic_title.setText(topics.get(position).getTopic_title());
                    holder.topic_description.setText(topics.get(position).getTopic_detail());
                    break;
                default:
                    holder.topic_title.setText(topics.get(position).getTopic_title());
                    holder.topic_description.setText(topics.get(position).getTopic_detail());

            }


        }

        @Override
        public int getItemViewType(int position){
            if(topics.get(position) instanceof  Topics){
                return TOPICS_WITH_ARTICLE;
            }
        return  -1;
        }

        @Override
        public int getItemCount() {
            return topics.size();
        }
    }
