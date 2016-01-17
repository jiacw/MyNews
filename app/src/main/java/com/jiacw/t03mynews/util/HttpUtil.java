package com.jiacw.t03mynews.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jiacw on 15:07 11/1/2016.
 * Email: 313133710@qq.com
 * Function:请求url
 */
//1.新建工具类
public class HttpUtil {
    public static void requestNews(final URL url, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            HttpURLConnection mURLConnection;
            @Override
            public void run() {
                try {
                    mURLConnection= (HttpURLConnection) url.openConnection();
                    mURLConnection.setRequestMethod("GET");
                    mURLConnection.setReadTimeout(8000);
                    mURLConnection.setConnectTimeout(8000);
                    InputStream inputStream=mURLConnection.getInputStream();
                    BufferedReader bReader=new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response=new StringBuilder();
                    String line="";
                    while ((line=bReader.readLine())!=null){
                        response.append(line);
                    }
                    if (listener!=null){
                        listener.onFinish(response.toString());
                    }
                } catch (IOException e) {
                    if (listener!=null)
                    {
                        listener.onError(e);
                    }
                }finally {
                    if (mURLConnection!=null)
                    {
                        mURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }
}
