package com.jiacw.t03mynews.util;

import com.jiacw.t03mynews.model.NewsDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by Jiacw on 18:24 11/1/2016.
 * Email: 313133710@qq.com
 * Function:解析返回的json数据
 */
public class JsonUtil {
    /**
    * created at 20/1/2016 16:10
    * function: 解析json数据
    */
    public static boolean handleJsonData(NewsDB newsDb, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean status=jsonObject.get("status").equals("000000");
            if (status) {
                JSONArray jsonArray = jsonObject.getJSONArray("detail");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonNews = (JSONObject) jsonArray.get(i);
                    String title = jsonNews.getString("title");
                    String source = jsonNews.getString("source");
                    String article_url = jsonNews.getString("article_url");
                    Long behot_time = jsonNews.getLong("behot_time");
                    int digg_count = jsonNews.getInt("digg_count");
                    int bury_count = jsonNews.getInt("bury_count");
                    int repin_count = jsonNews.getInt("repin_count");
                    String data=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(behot_time);
                    newsDb.saveNews(title, source, article_url,data, digg_count, bury_count
                            , repin_count);
                }
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
