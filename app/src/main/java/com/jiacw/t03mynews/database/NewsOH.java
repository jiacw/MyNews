package com.jiacw.t03mynews.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jiacw on 19:20 11/1/2016.
 * Email: 313133710@qq.com
 * Function:
 */
public class NewsOH extends SQLiteOpenHelper {
    public static final String SQL="create table News(" +
            "id integer primary key autoincrement," +
            "title text," +
            "source text," +
            "article_url text unique," +
            "data text," +
            "digg_count integer," +
            "bury_count integer," +
            "repin_count integer)";
    public NewsOH(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
