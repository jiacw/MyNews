package com.jiacw.t03mynews.interfaces;

import android.view.View;
import android.widget.AdapterView;

import com.jiacw.t03mynews.model.News;

/**
 * Created by Jiacw on 14:36 18/2/2016.
 * Email: 313133710@qq.com
 * Function:
 */
public interface OnRVItemClickListener {
    void onItemClick( View view, News news);
}
