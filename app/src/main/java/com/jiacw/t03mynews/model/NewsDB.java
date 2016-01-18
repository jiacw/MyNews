package com.jiacw.t03mynews.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jiacw.t03mynews.database.NewsOH;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiacw on 19:32 11/1/2016.
 * Email: 313133710@qq.com
 * Function:处理数据库
 */
public class NewsDB {
    private static final String NEWDB = "NewsDB";
    private SQLiteDatabase mSQLite;
    private static NewsDB mNewsDB;
    private Cursor mCursor;

    private NewsDB(Context context) {
        NewsOH newsOH = new NewsOH(context, NEWDB, null, 1);
        mSQLite = newsOH.getWritableDatabase();
    }

    public synchronized static NewsDB getInstance(Context context) {
        if (mNewsDB == null) {
            mNewsDB = new NewsDB(context);
        }
        return mNewsDB;
    }

    public void saveNews(String title, String source, String article_url, String data
            , int digg_count, int bury_count, int repin_count) {
        ContentValues value = new ContentValues();
        value.put("title", title);
        value.put("source", source);
        value.put("article_url", article_url);
        value.put("data", data);
        value.put("digg_count", digg_count);
        value.put("bury_count", bury_count);
        value.put("repin_count", repin_count);
        mSQLite.insert("News", null, value);
    }

    public List<News> queryNews() {
        mCursor = mSQLite.query("News", null, null, null, null, null, "data desc");
        List<News> list = new ArrayList<>();
        if (mCursor.moveToFirst()) {
            do {
                News news = new News();
                news.setTitle(mCursor.getString(mCursor.getColumnIndex("title")));
                news.setSource(mCursor.getString(mCursor.getColumnIndex("source")));
                news.setArticle_url(mCursor.getString(mCursor.getColumnIndex("article_url")));
                news.setData(mCursor.getString(mCursor.getColumnIndex("data")));
                news.setDigg_count(mCursor.getInt(mCursor.getColumnIndex("digg_count")));
                news.setBury_count(mCursor.getInt(mCursor.getColumnIndex("bury_count")));
                news.setRepin_count(mCursor.getInt(mCursor.getColumnIndex("repin_count")));
                list.add(news);
            } while (mCursor.moveToNext());
        }
        return list;
    }

    /**
     * created at 17/1/2016 8:47
     * function: 关闭数据库和游标
     */
    public void endAll() {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
            mCursor = null;
        }
        if (mSQLite != null && mSQLite.isOpen()) {
            mSQLite.close();
            mSQLite = null;
            mNewsDB = null;
        }
    }


}
