package in.co.nerdoo.chittichat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
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
    HashSet<Integer> selectedPositions;
    private  static boolean isAlreadyLongPressed;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView article_image;
        private TextView article_content;
        private  TextView username_article1;
        private  TextView username_article2;
        private Context viewContext;

        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
//            v.setOnClickListener(this);
            article_content =(TextView) v.findViewById(R.id.articleTextView);
            article_image = (ImageView) v.findViewById(R.id.articleImageView_card2);
            username_article1 = (TextView) v.findViewById(R.id.ArticleUsername_card1);
            username_article2 = (TextView) v.findViewById(R.id.ArticleUsername_card2);
            viewContext = v.getContext();

        }
//        @Override
//        public void onClick(View v) {
//            Log.v("clicked","true");
//        }

//        @Override
//        public boolean onLongClick(View v) {
//            if (TopicActivity.isAdmin) {
//                Log.d("Long Pressed", "true");
//
//
//            }
//            Log.v("Long Pressed","true");
//            return true;
//        }
    }


    public ArticleAdapter(List<Articles> articles) {
        this.articles = articles;
        this.selectedPositions = new HashSet<>();
        isAlreadyLongPressed = false;
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
        }else if(article_content.equals("image")){
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
        final Articles article = articles.get(position);
        switch (holder.getItemViewType()) {
            case TEXT:
                holder.username_article1.setText(articles.get(position).getPublisher_name());
                holder.article_content.setText(articles.get(position).getArticle_content());
                holder.itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }
                });
                holder.itemView.removeOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }
                });

                holder.itemView.setOnClickListener(v1 -> {
                    Log.d("Position",String.valueOf(position));
                    if(TopicActivity.isAdmin && isAlreadyLongPressed){
                        article.setSelected(!article.isSelected());
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        TopicActivity.deleteArticleIds.add(articles.get(position).get_id());
                    }else {
                        Log.v("selected status","Normal");
                    }
                });

                holder.itemView.setOnLongClickListener(v -> {
                    if(TopicActivity.isAdmin){

                        Log.d("Position",String.valueOf(position));
                        isAlreadyLongPressed = true;
                        article.setSelected(!article.isSelected());
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        TopicActivity.deleteArticleIds.add(articles.get(position).get_id());
                        new TopicActivity().onLongPressedArticle();
                        return true;
                    }
                    return false;
                });
                break;
            case IMAGE:
                holder.itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }
                });
                holder.itemView.removeOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        Log.d("Attched",String.valueOf(position)+"attached");
                    }
                });


                holder.username_article2.setText(articles.get(position).getPublisher_name());
                Picasso.with(holder.viewContext).load(ChittichatApp.getBaseUrl()+"/images/"+articles.get(position)
                        .getArticle_content()).resize(600,500).centerInside().into(holder.article_image);
//                holder.setIsRecyclable(false);
                holder.itemView.setOnClickListener(v1 -> {
                    Log.d("Position",String.valueOf(position));
                    if(TopicActivity.isAdmin && isAlreadyLongPressed){
                        article.setSelected(!article.isSelected());
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        TopicActivity.deleteArticleIds.add(articles.get(position).get_id());
                    }else {
                        Log.v("selected status","Normal");
                    }
                });

                holder.itemView.setOnLongClickListener(v -> {
                    if(TopicActivity.isAdmin){

                        Log.d("Position",String.valueOf(position));
                        isAlreadyLongPressed = true;
                        article.setSelected(!article.isSelected());
                        holder.itemView.setAlpha(article.isSelected()?0.4f:1.0f);
                        holder.itemView.setBackgroundColor(article.isSelected()?Color.parseColor("#872f93ff"):Color.TRANSPARENT);
                        TopicActivity.deleteArticleIds.add(articles.get(position).get_id());
                        new TopicActivity().onLongPressedArticle();
                        return true;
                    }
                    return false;
                });
                break;
            case AUDIO:
                break;
            case VIDEO:
                break;
            default:
                Toast.makeText(holder.viewContext,"Case not satisfied in on bind",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public long getItemId(int position) {
     return  getItemId(position);
    }

    @Override
    public int getItemCount() {
        return  articles.size();

    }
}

