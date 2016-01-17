package com.jiacw.t03mynews.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.model.News;

import java.util.List;

/**
 * Created by Jiacw on 21:53 11/1/2016.
 * Email: 313133710@qq.com
 * Function:适配器
 */
public class MyAdapter extends BaseAdapter{
    private List<News> mNewsList;
    private Context mContext;

    public MyAdapter(Context context, List<News> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    @Override
    public int getCount() {
        return mNewsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        News news = mNewsList.get(position);

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTVTitle = (TextView) view.findViewById(R.id.in_title);
            viewHolder.mTVSource = (TextView) view.findViewById(R.id.in_source);
            viewHolder.mTVTime = (TextView) view.findViewById(R.id.in_time);
            viewHolder.mTVDigg = (TextView) view.findViewById(R.id.in_digg);
            viewHolder.mTVBury = (TextView) view.findViewById(R.id.in_bury);
            viewHolder.mTVRepin = (TextView) view.findViewById(R.id.in_repin);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mTVTitle.setText(news.getTitle());
        viewHolder.mTVSource.setText(news.getSource());
        viewHolder.mTVTime.setText(news.getData());
        viewHolder.mTVDigg.setText(String.valueOf(news.getDigg_count()));
        viewHolder.mTVBury.setText(String.valueOf(news.getBury_count()));
        viewHolder.mTVRepin.setText(String.valueOf(news.getRepin_count()));
        return view;
    }

    class ViewHolder {
        public TextView mTVTitle;
        public TextView mTVSource;
        public TextView mTVTime;
        public TextView mTVDigg;
        public TextView mTVBury;
        public TextView mTVRepin;
    }
}
