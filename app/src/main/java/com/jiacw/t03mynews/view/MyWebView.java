package com.jiacw.t03mynews.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jiacw.t03mynews.R;

/**
 * Created by Jiacw on 14:32 28/2/2016.
 * Email: 313133710@qq.com
 * Function: 自带进度条
 */
public class MyWebView extends WebView {
    private Context mContext;
    private ProgressBar progressBar;

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MyWebView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    /**
     * 初始方法
     */
    private void initView() {
        progressBar = new ProgressBar(mContext,null,android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));//设置位置
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_diy));
        addView(progressBar);
        doSetting();
        setWebChromeClient(new MyWebChromeClient());
        setWebViewClient(new MyWebViewClient());
    }

    /**
     * 处理一些设置
     */
    private void doSetting() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);//支持js脚本
        webSettings.setUseWideViewPort(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams layoutParams= (LayoutParams) progressBar.getLayoutParams();
        layoutParams.x=l;
        layoutParams.y=t;
        progressBar.setLayoutParams(layoutParams);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(GONE);
            } else {
                        progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view,newProgress);
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(mContext, "出错啦\n"
                        + error.getDescription().toString(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "出错啦 >_<", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
