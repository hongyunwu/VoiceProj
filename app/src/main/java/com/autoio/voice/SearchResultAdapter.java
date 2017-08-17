package com.autoio.voice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuhongyun on 17-8-17.
 */

class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchItemHolder> {


    private Context context;
    private ArrayList<String> searchResults;

    public SearchResultAdapter(Context context, ArrayList<String> searchResults) {

        this.context = context;
        this.searchResults = searchResults;
        Log.i("SearchResultAdapter","searchResults:"+searchResults.size());
    }

    @Override
    public SearchItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result,parent,false);
        return new SearchItemHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchItemHolder holder, int position) {
        holder.item_search_result.setText(searchResults.get(position));
    }

    @Override
    public int getItemCount() {
        if (searchResults!=null){
            return searchResults.size();
        }
        return 0;
    }

    public void setSearchResults(ArrayList<String> searchResults) {
        this.searchResults = searchResults;
        notifyDataSetChanged();

    }

    class SearchItemHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_search_result)
        TextView item_search_result;
        public SearchItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
