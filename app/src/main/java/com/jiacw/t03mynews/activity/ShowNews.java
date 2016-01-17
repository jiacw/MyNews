package com.jiacw.t03mynews.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.model.News;
import com.jiacw.t03mynews.model.NewsDB;
import com.jiacw.t03mynews.util.HttpCallbackListener;
import com.jiacw.t03mynews.util.HttpUtil;
import com.jiacw.t03mynews.util.JsonUtil;
import com.jiacw.t03mynews.util.MyAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Jiacw on 19:08 11/1/2016.
 * Email: 313133710@qq.com
 * Function:显示新闻列表
 */
public class ShowNews extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private ListView mLV;
    private NewsDB mNewsDB;
    private Button mButton;
    private List<News> mNewsList;
    private boolean isUpdated=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_news);

        mNewsDB = NewsDB.getInstance(this);
        mButton = (Button) findViewById(R.id.sn_btn);
        mButton.setOnClickListener(this);
        mLV = (ListView) findViewById(R.id.sn_lv);
        mLV.setOnItemClickListener(this);
        if (!isUpdated){
            requestNews();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sn_btn) {
          requestNews();
        }
    }
    private void requestNews(){
        try {
            URL url = new URL("http://api.1-blog.com/biz/bizserver/news/list.do?size=5");
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
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShowNews.this, "网络请求出错>_<", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void showNews() {
        mNewsList = mNewsDB.queryNews();
        Adapter adapter = new MyAdapter(this, mNewsList);

        mLV.setAdapter((ListAdapter) adapter);
    }

    @Override
    protected void onDestroy() {
        mNewsDB.endAll();
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent =new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mNewsList.get(position).getArticle_url()));
        startActivity(intent);
    }
}
