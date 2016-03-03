package com.jiacw.t03mynews.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.model.NewsDB;
import com.jiacw.t03mynews.view.MyWebView;

/**
 * Created by Jiacw on 19:47 17/1/2016.
 * Email: 313133710@qq.com
 * Function:显示新闻内容
 */
public class NewsWeb extends AppCompatActivity implements View.OnClickListener {
    private MyWebView webView;
    private String url;
    private boolean like = false;
    private Drawable mDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_web);
        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.nc_tb);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);
        //网页
        webView = (MyWebView) findViewById(R.id.cn_wv);
        url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
        mDrawable = getResources().getDrawable(R.drawable.ic_favorite_white_24dp);
        mDrawable.setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_button, menu);
        NewsDB newsDB = NewsDB.getInstance(this, false);
        if (newsDB.queryFavor(url)) {
            menu.findItem(R.id.button_save).setIcon(mDrawable);
            like = true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.button_save) {
            like = !like;
            NewsDB newsDB = NewsDB.getInstance(this, false);
            newsDB.updateFavor(url, like);
            if (like) {
                item.setIcon(mDrawable);
            } else {
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                Intent intent = new Intent();
                intent.putExtra("url", url);
                setResult(233, intent);
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
