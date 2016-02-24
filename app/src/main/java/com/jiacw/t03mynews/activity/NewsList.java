package com.jiacw.t03mynews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Toast;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.adapter.MyAdapter;
import com.jiacw.t03mynews.adapter.VPAdapter;
import com.jiacw.t03mynews.interfaces.HttpCallbackListener;
import com.jiacw.t03mynews.interfaces.IXListViewListener;
import com.jiacw.t03mynews.interfaces.OnRVItemClickListener;
import com.jiacw.t03mynews.model.News;
import com.jiacw.t03mynews.model.NewsDB;
import com.jiacw.t03mynews.util.HttpUtil;
import com.jiacw.t03mynews.util.JsonUtil;
import com.jiacw.t03mynews.util.LogUtil;
import com.jiacw.t03mynews.view.MyRecyclerView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiacw on 19:08 11/1/2016.
 * Email: 313133710@qq.com
 * Function:显示新闻列表
 */
public class NewsList extends AppCompatActivity implements OnRVItemClickListener
        , IXListViewListener, NavigationView.OnNavigationItemSelectedListener {
    //成员变量
    private List<String> mImageList;//图片list
    private List<News> subList = new ArrayList<>();//用于分页加载
    private List<News> newsList;//接收查询的新闻
    private Handler mHandler;//发送刷新和加载任务
    private int i = 0;//索引
    private int j = 20;
    private int totalSize;
    //抽屉
    DrawerLayout mDrawer;
    //新闻操作类
    private NewsDB mNewsDB;
    //自定义列表
    private MyRecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//重设
        //fab
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ab_fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "hi", Snackbar.LENGTH_SHORT)
//                        .setAction("hi", null).show();
//            }
//        });
        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.nl_tb);
        setSupportActionBar(toolbar);
        //表格视图
        TabLayout tabLayout = (TabLayout) findViewById(R.id.ab_cl_tl);
        tabLayout.addTab(tabLayout.newTab().setText("头条"));
        tabLayout.addTab(tabLayout.newTab().setText("娱乐"));
        tabLayout.addTab(tabLayout.newTab().setText("体育"));
        tabLayout.addTab(tabLayout.newTab().setText("科技"));
        tabLayout.addTab(tabLayout.newTab().setText("军事"));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //抽屉
        mDrawer = (DrawerLayout) findViewById(R.id.am_dl);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar
                , R.string.nav_drawer_open, R.string.nav_drawer_close);//联系抽屉和标题栏
        mDrawer.setDrawerListener(toggle);//监听
        toggle.syncState();//同步状态
        NavigationView navigationView = (NavigationView) findViewById(R.id.am_dl_nav);
        navigationView.setNavigationItemSelectedListener(this);
        getImage();
        initialize();

    }

    @Override
    protected void onDestroy() {
        mNewsDB.endAll();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//----------------------------自定义------------------------------------------------------

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
        View headViewPage = LayoutInflater.from(this).inflate(R.layout.news_viewpage, null);
        ViewPager topViewPager = (ViewPager) headViewPage.findViewById(R.id.nv_vp);
        VPAdapter topViewAdapter = new VPAdapter(this, mImageList);
        topViewPager.setAdapter(topViewAdapter);
        mRecyclerView = (MyRecyclerView) findViewById(R.id.nl_rv);
        mRecyclerView.setPullLoadEnabled(true);
        mRecyclerView.addHeaderView(headViewPage);
        mRecyclerView.setXListViewListener(this);
        mRecyclerView.setOnItemClickListener(this);
        mRecyclerView.setViewParent(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
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
                            Snackbar.make(mDrawer, "网络请求出错>_<", Snackbar.LENGTH_LONG)
                                    .setAction("退出", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            finish();
                                        }
                                    }).show();
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
        mRecyclerView.setAdapter(mAdapter);
        LogUtil.d("jiacw", "showNews " + mRecyclerView.mHeadViewInfos.size());
        mRecyclerView.mHeadViewInfos.size();
        isNewest();
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
            //换成SnackBar
            Toast.makeText(NewsList.this, "已最新", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("time", date);
            editor.apply();
            Toast.makeText(NewsList.this, "请享用", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /**
     * 停止刷新，设置刷新时间
     */
    private void finishRefresh() {
        mRecyclerView.stopRefresh();
        mRecyclerView.setRefreshTime();
    }

    /**
     * 加载更多数据
     */
    private void loadMoreDate() {
        i += 20;
        j += 20;
        if (j >= totalSize) {
            mRecyclerView.setPullLoadEnabled(false);
            j = totalSize;
        }
        mAdapter.addMoreNews(newsList.subList(i, j));
        mAdapter.notifyDataSetChanged();
    }

    //--------------------------------接口-------------------------------------------------------
    //    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(this, NewsWeb.class);
//        News news = (News) parent.getAdapter().getItem(position);
//        intent.putExtra("url", news.getArticle_url());
//        startActivity(intent);
//    }
    @Override
    public void onItemClick(View view, News news) {
        Intent intent = new Intent(this, NewsWeb.class);
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

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreDate();
                mRecyclerView.stopLoadMore();
            }
        }, 500);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_setting:
                break;
            case R.id.nav_feedback:
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_test:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
