package com.jiacw.t03mynews.util;

/**
 * Created by Jiacw on 16:27 11/1/2016.
 * Email: 313133710@qq.com
 * Function:接口处理网络请求成功或失败
 */
//2.建立接口
public interface HttpCallbackListener {
    /**
    * created at 20/1/2016 16:09
    * function: 成功获取json字符串
    */
    void onFinish(String response);
    /**
    * created at 20/1/2016 16:09
    * function: 网络请求失败
    */
    void onError();
}
