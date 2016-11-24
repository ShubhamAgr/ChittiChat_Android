package in.co.nerdoo.chittichat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shubham on 15/11/16.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private static List<TopicsWithArticle> articlesDataSet;
    private final int TOPICS_WITH_ARTICLE=0;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context viewContext;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            viewContext = v.getContext();
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(viewContext,TopicActivity.class);
            intent.putExtra("TopicId",articlesDataSet.get(getPosition()).get_id());
            viewContext.startActivity(intent);
        }
    }


        public TopicAdapter(List<TopicsWithArticle> articlesDataSet) {
            this.articlesDataSet = articlesDataSet;
        }


        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            TopicAdapter.ViewHolder viewHolder;
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            switch (viewType){
                case TOPICS_WITH_ARTICLE:
                    View v1 = layoutInflater.inflate(R.layout.group_card, parent, false);
                    viewHolder = new ViewHolder(v1);
                    break;
                default:
                    View v = layoutInflater.inflate(R.layout.group_card, parent, false);
                    viewHolder = new ViewHolder(v);
                    break;
            }
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            switch (holder.getItemViewType()){
                case TOPICS_WITH_ARTICLE:

                    break;
                default:

            }
//        holder.textView.setText(mDataset[position]);


        }

        @Override
        public int getItemViewType(int position){
            if(articlesDataSet.get(position) instanceof  TopicsWithArticle){
                return TOPICS_WITH_ARTICLE;
            }
        return  -1;
        }

        @Override
        public int getItemCount() {
            return articlesDataSet.size();
        }
    }
