package com.jiacw.t03mynews.util;

/**
 * Created by Jiacw on 16:27 11/1/2016.
 * Email: 313133710@qq.com
 * Function:接口处理网络请求成功或失败
 */
//2.建立接口
public interface HttpCallbackListener {
    public void onFinish(String response);
    public void onError(Exception e);
}
