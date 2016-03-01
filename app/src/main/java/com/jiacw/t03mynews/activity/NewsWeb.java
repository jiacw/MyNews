package com.jiacw.t03mynews.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.view.MyWebView;

/**
 * Created by Jiacw on 19:47 17/1/2016.
 * Email: 313133710@qq.com
 * Function:显示新闻内容
 */
public class NewsWeb extends AppCompatActivity {
    private MyWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.nc_tb);
        setSupportActionBar(toolbar);
        //网页
        webView = (MyWebView) findViewById(R.id.cn_wv);
        String uri = getIntent().getStringExtra("url");
        webView.loadUrl(uri);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
        super.onBackPressed();
    }
}
