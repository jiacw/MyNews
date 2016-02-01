package com.jiacw.t03mynews.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Jiacw on 10:09 22/1/2016.
 * Email: 313133710@qq.com
 * Function:屏幕工具类
 */
public class ScreenUtil {
    /**
    * created at 22/1/2016 10:40
    * function: 获取屏幕宽高像素，返回Screen对象
    */
    public static Screen getScreenPixel(Context context) {
        DisplayMetrics dm = new DisplayMetrics();//视图信息
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels, dm.heightPixels);

    }

    public static class Screen {
        public final int width;
        public final int height;

        public Screen(int widthPixels, int heightPixels) {
            width = widthPixels;
            height = heightPixels;
        }

        @Override
        public String toString() {
            return "(" + width + "," + height + ")";
        }
    }
}
