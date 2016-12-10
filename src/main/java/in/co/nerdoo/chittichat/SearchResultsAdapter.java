package in.co.nerdoo.chittichat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shubham on 2/12/16.
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private  static List<GroupSearchResult> searchLists;
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context viewContext;
        private TextView searched_content;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            searched_content = (TextView) v.findViewById(R.id.searched_name);
            viewContext = v.getContext();
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(viewContext,showUsersOrGroupsActivity.class);
            intent.putExtra("GroupId",searchLists.get(getPosition()).get_id());
            intent.putExtra("Question",searchLists.get(getPosition()).getKnock_knock_question());
            intent.putExtra("GroupName",searchLists.get(getPosition()).getGroup_name());
            intent.putExtra("GroupAbout",searchLists.get(getPosition()).getGroup_about());
            viewContext.startActivity(intent);
        }
    }
    public  SearchResultsAdapter(List<GroupSearchResult> groupSearchResults){
            searchLists = groupSearchResults;
    }
    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_card, parent, false);

              return (new ViewHolder(v));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.searched_content.setText(searchLists.get(position).getGroup_name());
    }


    @Override
    public int getItemCount() {
        return searchLists.size();
    }
}
