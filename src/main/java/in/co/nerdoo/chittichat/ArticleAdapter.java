package in.co.nerdoo.chittichat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shubham on 15/11/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private String[] mDataset;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
    }


    public ArticleAdapter(String[] myDataset) {
        mDataset = myDataset;
    }


    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_card, parent, false);

        ViewHolder vh = new ViewHolder(v);


        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

