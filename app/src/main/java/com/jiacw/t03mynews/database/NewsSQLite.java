package com.jiacw.t03mynews.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jiacw.t03mynews.util.LogUtil;

/**
 * Created by Jiacw on 19:20 11/1/2016.
 * Email: 313133710@qq.com
 * Function:新闻数据库
 */
public class NewsSQLite extends SQLiteOpenHelper {
    private static final String SQL = "create table News(" +
            "id integer primary key autoincrement," +
            "title text," +
            "source text," +
            "article_url text unique," +
            "data text," +
            "digg_count integer," +
            "bury_count integer," +
            "repin_count integer)";

    public NewsSQLite(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.d("jiacwDB", "" + oldVersion);
        switch (oldVersion) {
            case 3:
                db.execSQL("alter table News add column favourite integer");
                break;
        }
    }
}
