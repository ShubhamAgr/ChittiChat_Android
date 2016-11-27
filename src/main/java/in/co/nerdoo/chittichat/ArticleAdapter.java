package in.co.nerdoo.chittichat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shubham on 15/11/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Articles> articles;
    private  final  int TEXT = 0;
    private final int  IMAGE = 1;
    private  final int VIDEO = 2;
    private final int  AUDIO = 3;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
    }


    public ArticleAdapter(List<Articles> articles) {
        this.articles = articles;
    }


    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        ArticleAdapter.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case TEXT:
                View v1 = layoutInflater.inflate(R.layout.article_card, parent, false);
                viewHolder = new ViewHolder(v1);
                break;
            case IMAGE:
                View v2 = layoutInflater.inflate(R.layout.article_card2, parent, false);
                viewHolder = new ViewHolder(v2);
                break;
            case VIDEO:
                View v3 = layoutInflater.inflate(R.layout.article_card, parent, false);
                viewHolder = new ViewHolder(v3);
                break;
            case AUDIO:
                View v4 = layoutInflater.inflate(R.layout.article_card, parent, false);
                viewHolder = new ViewHolder(v4);
                break;
            default:
                View v = layoutInflater.inflate(R.layout.article_card, parent, false);
                viewHolder = new ViewHolder(v);
                break;
        }
        return viewHolder;
    }
    @Override
    public int getItemViewType(int position){
        String article_content = articles.get(position).getContent_type();
        if(article_content.equals("texts")){
            return TEXT;
        }else if(article_content.equals("images")){
            return IMAGE;
        }else if(article_content.equals("videos")){
            return  VIDEO;
        }else if(article_content.equals("audios")){
            return AUDIO;
        }
        return  -1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TEXT:
                break;
            case IMAGE:
                break;
            case AUDIO:
                break;
            case VIDEO:
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}

