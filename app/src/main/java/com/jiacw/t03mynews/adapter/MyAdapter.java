package com.jiacw.t03mynews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.interfaces.OnRVItemClickListener;
import com.jiacw.t03mynews.model.News;

import java.util.List;

/**
 * Created by Jiacw on 12:43 20/2/2016.
 * Email: 313133710@qq.com
 * Function:
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<News> mNewsList;
    private Context mContext;
    static OnRVItemClickListener mOnRVItemClickListener;

    public MyAdapter(Context context, List<News> newsList) {
        mContext = context;
        mNewsList = newsList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        item.setOnClickListener(this);
        return new VHItem(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        News news = mNewsList.get(position);
        if (holder instanceof VHItem) {
            holder.itemView.setTag(news);
            ((VHItem) holder).mTVTitle.setText(news.getTitle());
            ((VHItem) holder).mTVSource.setText(news.getSource());
            ((VHItem) holder).mTVTime.setText(news.getData());
            ((VHItem) holder).mTVDigg.setText(String.valueOf(news.getDigg_count()));
            ((VHItem) holder).mTVBury.setText(String.valueOf(news.getBury_count()));
            ((VHItem) holder).mTVRepin.setText(String.valueOf(news.getRepin_count()));
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onClick(View v) {
        News news = (News) v.getTag();
        if (mOnRVItemClickListener != null) {
            mOnRVItemClickListener.onItemClick(v, news);
        }
    }

    //----------------------------------自定义--------------------
    public void addMoreNews(List<News> newsList) {
        mNewsList.addAll(newsList);
    }

    public static void realizeOnItemCLick(OnRVItemClickListener listener) {
        mOnRVItemClickListener = listener;
    }

    class VHItem extends RecyclerView.ViewHolder {
        public TextView mTVTitle;
        public TextView mTVSource;
        public TextView mTVTime;
        public TextView mTVDigg;
        public TextView mTVBury;
        public TextView mTVRepin;

        public VHItem(View itemView) {
            super(itemView);
            mTVTitle = (TextView) itemView.findViewById(R.id.in_title);
            mTVSource = (TextView) itemView.findViewById(R.id.in_source);
            mTVTime = (TextView) itemView.findViewById(R.id.in_time);
            mTVDigg = (TextView) itemView.findViewById(R.id.in_digg);
            mTVBury = (TextView) itemView.findViewById(R.id.in_bury);
            mTVRepin = (TextView) itemView.findViewById(R.id.in_repin);
        }
    }

}


