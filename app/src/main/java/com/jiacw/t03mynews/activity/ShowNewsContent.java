package com.jiacw.t03mynews.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jiacw.t03mynews.R;

/**
 * Created by Jiacw on 19:47 17/1/2016.
 * Email: 313133710@qq.com
 * Function:
 */
public class ShowNewsContent extends Activity{
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_news);
        mWebView= (WebView) findViewById(R.id.cn_wv);
        mWebView.getSettings().setJavaScriptEnabled(true);//支持js脚本
        mWebView.setWebViewClient(new WebViewClient());
        String uri=getIntent().getStringExtra("url");
        mWebView.loadUrl(uri);
    }
}
