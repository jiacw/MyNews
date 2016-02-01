package com.jiacw.t03mynews.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.jiacw.t03mynews.R;

/**
 * Created by Jiacw on 19:47 17/1/2016.
 * Email: 313133710@qq.com
 * Function:显示新闻内容
 */
public class ShowNewsContent extends Activity {
    private final Activity activity = this;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.news_content);
        webView = (WebView) findViewById(R.id.cn_wv);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//支持js脚本
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                activity.setProgress(newProgress * 100);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request
                    , WebResourceError error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(ShowNewsContent.this, "出错啦\n"
                            + error.getDescription().toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShowNewsContent.this, "出错啦 >_<", Toast.LENGTH_SHORT).show();
                }
            }
        });
        webSettings.setUseWideViewPort(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        String uri = getIntent().getStringExtra("url");
        webView.loadUrl(uri);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
