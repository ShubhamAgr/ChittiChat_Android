package in.co.nerdoo.chittichat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shubham on 15/11/16.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private String[] mDataset;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
//    public TextView textView;
//    public  CardView cardView;
//    public TextView textView2;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
//        textView = (TextView) v.findViewById(R.id.info_text);
//        cardView = (CardView) v.findViewById(R.id.card_view);
//        textView2 = (TextView) v.findViewById(R.id.info_text2);
        }
        @Override
        public void onClick(View v) {

        }
    }


        public TopicAdapter(String[] myDataset) {
            mDataset = myDataset;
        }


        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_card, parent, false);

            ViewHolder vh = new ViewHolder(v);


            return vh;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

//        holder.textView.setText(mDataset[position]);


        }


        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
