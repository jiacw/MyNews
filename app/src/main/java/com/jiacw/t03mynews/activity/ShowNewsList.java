package com.jiacw.t03mynews.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.adapter.MyAdapter;
import com.jiacw.t03mynews.adapter.TopViewPageAdapter;
import com.jiacw.t03mynews.model.News;
import com.jiacw.t03mynews.model.NewsDB;
import com.jiacw.t03mynews.util.HttpCallbackListener;
import com.jiacw.t03mynews.util.HttpUtil;
import com.jiacw.t03mynews.util.JsonUtil;
import com.jiacw.t03mynews.view.SListView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiacw on 19:08 11/1/2016.
 * Email: 313133710@qq.com
 * Function:显示新闻列表
 */
public class ShowNewsList extends Activity implements AdapterView.OnItemClickListener
        , SListView.IXListViewListener {
    //成员变量
    private List<String> mImageList;//图片list
    private List<News> subList = new ArrayList<>();//用于分页加载
    private List<News> newsList;//接收查询的新闻
    private Handler mHandler;//发送刷新和加载任务
    private int i = 0;//索引
    private int j = 20;
    private int totalSize;
    //新闻操作类
    private NewsDB mNewsDB;
    //自定义列表
    private SListView mLV;
    private MyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);
        getImage();
        initialize();
    }

    /**
     * 将网络图片地址添加到List中
     */
    private void getImage() {
        mImageList = new ArrayList<>();
        mImageList.add("http://img3.cache.netease.com/photo/0001/2016-01-21/BDRV43BL00AO0001.jpg");
        mImageList.add("http://img5.cache.netease.com/photo/0001/2016-01-21/BDRSKGTI00AP0001.jpg");
        mImageList.add("http://img3.cache.netease.com/photo/0001/2016-01-21/BDRGMIJ44T8E0001.jpg");
    }

    /**
     * 寻找控件，实例化类
     */
    private void initialize() {
        mNewsDB = NewsDB.getInstance(this);
        View headView = LayoutInflater.from(this).inflate(R.layout.news_viewpage, null);
        ViewPager topViewPager = (ViewPager) headView.findViewById(R.id.nv_vp);
        TopViewPageAdapter topViewAdapter = new TopViewPageAdapter(this, mImageList);
        topViewPager.setAdapter(topViewAdapter);
        mLV = (SListView) findViewById(R.id.sn_lv);
        mLV.setPullLoadEnabled(true);
        mLV.addHeaderView(headView);
        mLV.setXListViewListener(this);
        mLV.setOnItemClickListener(this);
        mHandler = new Handler();
        requestNews();

    }


    /**
     * created at 18/1/2016 19:17
     * function: 网络请求最新数据
     */
    private void requestNews() {
        try {
            URL url = new URL("http://api.1-blog.com/biz/bizserver/news/list.do");
            HttpUtil.requestNews(url, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    boolean finishJson = JsonUtil.handleJsonData(mNewsDB, response);
                    if (finishJson) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showNews();
                            }
                        });

                    }
                }

                @Override
                public void onError() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShowNewsList.this, "网络请求出错>_< ", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取新闻列表，为listView适配数据
     */
    private void showNews() {
        newsList = mNewsDB.queryNews();
        totalSize = newsList.size();
        subList.addAll(newsList.subList(0, 20));
        mAdapter = new MyAdapter(this, subList);
        mLV.setAdapter(mAdapter);
    }

    /**
     * 判断新闻是否最新
     *
     * @return true-新闻最新，false-需要更新
     */
    private boolean isNewest() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String date_share = preferences.getString("time", "1992-11-17");
        String date = newsList.get(0).getData();
        if (date.equals(date_share)) {
            Toast.makeText(ShowNewsList.this, "已是最新", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("time", date);
            editor.apply();
            Toast.makeText(ShowNewsList.this, "请享用", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        mNewsDB.endAll();
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ShowNewsContent.class);
        News news = (News) parent.getAdapter().getItem(position);
        intent.putExtra("url", news.getArticle_url());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isNewest()) {
                    requestNews();
                }
                finishRefresh();
            }
        }, 2000);
    }

    /**
     * 停止刷新，设置刷新时间
     */
    private void finishRefresh() {
        mLV.stopRefresh();
        mLV.setRefreshTime();
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreDate();
                mLV.stopLoadMore();
            }
        }, 500);
    }

    /**
     * 加载更多数据
     */
    private void loadMoreDate() {
        i += 20;
        j += 20;
        if (j >= totalSize) {
            mLV.setPullLoadEnabled(false);
            j = totalSize;
        }
        mAdapter.addMoreNews(newsList.subList(i, j));
        mAdapter.notifyDataSetChanged();
    }


}
