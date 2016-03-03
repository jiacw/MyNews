package com.jiacw.t03mynews.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jiacw.t03mynews.database.NewsSQLite;

import java.util.ArrayList;
import java.util.Date;
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
        NewsSQLite newsOH = new NewsSQLite(context, NEWDB, 5);
        mSQLite = newsOH.getWritableDatabase();
    }

    /**
     * created at 20/1/2016 16:12
     * function: 获取NewsDB实例
     */
    public synchronized static NewsDB getInstance(Context context, boolean reset) {
        if (mNewsDB == null || reset) {
            mNewsDB = new NewsDB(context);
        }
        return mNewsDB;
    }

    /**
     * created at 20/1/2016 16:13
     * function: 保存新闻到数据库
     */
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

    /**
     * created at 20/1/2016 16:13
     * function: 查询新闻添加到新闻列表中
     */
    public List<News> queryNews() {
        if (mCursor == null) {
            mCursor = mSQLite.query("News", null, null, null, null, null, "data desc");
        }
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
     * 收藏新闻
     *
     * @param url  新闻链接
     * @param like 喜欢或不喜欢
     */
    public void updateFavor(String url, boolean like) {
        int sign = 0;
        if (like) sign = 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put("favourite", sign);
        contentValues.put("collectTime",System.currentTimeMillis()/1000);
        mSQLite.update("News", contentValues, "article_url=?", new String[]{url});
        contentValues.clear();
    }

    /**
     * 查询是否已收藏
     *
     * @param url 新闻链接
     * @return true 收藏了；false 没收藏
     */
    public boolean queryFavor(String url) {
        Cursor cursor = mSQLite.query("News", new String[]{"favourite"}, "article_url=?"
                , new String[]{url}, null, null, "collectTime desc");
        int sign = 0;
        if (cursor.moveToFirst()) {
            sign = cursor.getInt(cursor.getColumnIndex("favourite"));
        }
        cursor.close();
        return sign == 1;
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

    /**
     * 查找所有已收藏新闻
     * @return 返回收藏新闻
     */
    public List<News> findAllFavor() {
        List<News> newses = new ArrayList<>();
        Cursor cursor = mSQLite.query("News", new String[]{"title", "article_url"}
                , "favourite=?", new String[]{1 + ""}, null, null, "collectTime desc");
        if (cursor.moveToFirst()) {
            do {
                News news = new News();
                news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                news.setArticle_url(cursor.getString(cursor.getColumnIndex("article_url")));
                newses.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return newses;
    }
}
