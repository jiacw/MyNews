package com.jiacw.t03mynews.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.model.News;
import com.jiacw.t03mynews.model.NewsDB;
import com.jiacw.t03mynews.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiacw on 16:08 2/3/2016.
 * Email: 313133710@qq.com
 * Function:收藏页
 */
public class FavorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
    private List<News> mNewsList;
    private List<String> mNewsTitle;
    private ArrayAdapter mAdapter;
    public static final int ASK = 123;
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favor_layout);
        mTextView= (TextView) findViewById(R.id.fl_tv);
        ListView listView = (ListView) findViewById(R.id.fl_lv);
        requestFavor();
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mNewsTitle);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    /**
     * 查找收藏新闻
     */
    private void requestFavor() {
        NewsDB newsDB = NewsDB.getInstance(this, false);
        mNewsList = newsDB.findAllFavor();
        mNewsTitle = new ArrayList<>();
        for (News news : mNewsList) {
            mNewsTitle.add(news.getTitle());
            LogUtil.d("jiacw123",news.getTitle());

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NewsWeb.class);
        intent.putExtra("url", mNewsList.get(position).getArticle_url());
        startActivityForResult(intent, ASK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTint();
    }

    /**
     * 显示提示
     */
    private void showTint() {
        if(mNewsTitle.size()>0){
            mTextView.setVisibility(View.GONE);
        }else {
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ASK && resultCode == 233) {
            String url = data.getStringExtra("url");
            int i = 0;
            for (News news : mNewsList) {
                if (news.getArticle_url().equals(url)) {
                    break;
                }
                i++;
            }
            mNewsList.remove(i);
            mNewsTitle.remove(i);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setTitle("确认删除？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewsDB newsDB=NewsDB.getInstance(FavorActivity.this,false);
                        newsDB.updateFavor(mNewsList.get(position).getArticle_url(),false);
                        mNewsList.remove(position);
                        mNewsTitle.remove(position);
                        mAdapter.notifyDataSetChanged();
                        showTint();
                    }
                })
                .setNegativeButton("取消",null)
                .setCancelable(false);
        builder.show();
        return true;
    }
}
