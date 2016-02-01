package com.jiacw.t03mynews.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiacw.t03mynews.R;
import com.jiacw.t03mynews.cache.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiacw on 16:36 21/1/2016.
 * Email: 313133710@qq.com
 * Function:适配顶部图片
 */
public class TopViewPageAdapter extends PagerAdapter {
    private final List<String> mImageList;
    private final List<View> mViewPageList;
    private final Context mContext;

    /**
     * 实例化视图列表
     * @param context 上下文
     * @param imageList 图片地址
     */
    public TopViewPageAdapter(Context context, List<String> imageList) {
        mContext = context;
        mImageList = imageList;
        mViewPageList = new ArrayList<>();
        for (int i = 0; i < mImageList.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.news_viewpage_item, null);
            mViewPageList.add(view);
        }
    }

    @Override
    public int getCount() {
        return mViewPageList != null ? mImageList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewPageList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewPageList.get(position));
        final ImageView imageView= (ImageView) mViewPageList.get(position).findViewById(R.id.nv_iv);
        ImageDownloader imageDownloader = new ImageDownloader(mContext);
        imageDownloader.download(mImageList.get(position), imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击图片", Toast.LENGTH_SHORT).show();
            }
        });
        return mViewPageList.get(position);
    }

}
