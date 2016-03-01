package com.jiacw.t03mynews.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.jiacw.t03mynews.adapter.MyAdapter;
import com.jiacw.t03mynews.adapter.RVAdapter;
import com.jiacw.t03mynews.interfaces.IXListViewListener;
import com.jiacw.t03mynews.interfaces.OnRVItemClickListener;
import com.jiacw.t03mynews.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by Jiacw on 19:57 20/1/2016.
 * Email: 313133710@qq.com
 * Function:自定义ListView
 */

public class MyRecyclerView extends RecyclerView {

    //成员变量
    private OnScrollListener mScrollListener;//滚动监听
    private ViewParent mViewParent;
    //接口用来引发刷新和加载更多
    private IXListViewListener mListViewListener;
    //头部视图
    private RelativeLayout mHeaderViewContent;//头部试图内容，用来计算头部，隐藏当不可刷新时
    private TextView mHeaderTime;
    private int mHeaderViewHeight;
    private boolean mPullRefreshing = false;
    private int mHeadViewSize;
    //尾部视图
    private MyRVFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean isFooterAdded = false;
    //滚动Scroll
    private Scroller mScroller;//用来回滚
    private float mLastY = -1;
    private int mScrollBack;
    private static final int SCROLLBACK_HEADER = 0;
    private static final int SCROLLBACK_FOOTER = 1;
    private static final int SCROLL_DURATION = 400;
    private static final int PULL_LOAD_MORE_DELTA = 50;//当拉动超过50像素，引起更多
    private static final float OFFSET_RADIO = 1.8f;

    //适配器
    Adapter mAdapter;//实例对象
    //添加View
    public ArrayList<View> mHeadViewInfos = new ArrayList<>();
    public ArrayList<View> mFootViewInfos = new ArrayList<>();

    //接口
    OnRVItemClickListener mOnRVItemClickListener;
    //测试
    ListView mListView;


    public MyRecyclerView(Context context) {
        super(context);
        initWithContext(context);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }
//------------------自定义方法--------------------------------------------------------------

    /**
     * created at 21/1/2016 9:49
     * function: 初始化
     */
    private void initWithContext(Context context) {
        //监听ListView的滚动事件
        mScroller = new Scroller(context, new DecelerateInterpolator());
        //实例化头部视图
        //添加头部视图
        //初始化尾部视图
        mFooterView = new MyRVFooter(context);
        //获取头部试图的高度
    }

    public void addHeaderView(View v) {
        mHeadViewInfos.add(v);
        mHeadViewSize++;
        if (mAdapter != null) {
            if (!(mAdapter instanceof RVAdapter)) {
                mAdapter = new RVAdapter(mHeadViewInfos, mFootViewInfos, mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addFooterView(View view) {

        mFootViewInfos.clear();
        mFootViewInfos.add(view);
        if (mAdapter != null) {
            mAdapter = new RVAdapter(mHeadViewInfos, mFootViewInfos, mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置是否可拉动加载；
     *
     * @param enable 可用或不可用
     */
    public void setPullLoadEnabled(boolean enable) {
        mEnablePullLoad = enable;
        //如果不可拉动加载
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);//不监听点击事件
        } else {
            mPullLoading = false;
            mFooterView.show();
            //上拉和点击都能调用加载
            mFooterView.setState(MyRVFooter.STATE_NORMAL);
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 设置正在拉动状态；设置状态-加载中；监听加载更多事件
     */
    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(MyRVFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    /**
     * 注册监听对象，监听尾部加载和头部刷新
     *
     * @param l 监听对象
     */
    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing) {
            mPullRefreshing = false;
        }
    }


    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(MyRVFooter.STATE_NORMAL);
        }
    }



    /**
     * 重置尾部高度
     */
    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private int getLastVisiblePosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        return layoutManager.findLastVisibleItemPosition() - 1;
    }

    private int getTotalItemCount() {
        RecyclerView.LayoutManager manager = getLayoutManager();
        return manager.getItemCount() - mFootViewInfos.size()-mHeadViewSize;
    }

    /**
     * 更新尾部视图
     *
     * @param delta 滑动值
     */
    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            //高度足够载入
            if (height > PULL_LOAD_MORE_DELTA) {
                mFooterView.setState(MyRVFooter.STATE_READY);
            } else {
                mFooterView.setState(MyRVFooter.STATE_NORMAL);
            }
            mFooterView.setBottomMargin(height);

        }
    }


    public void setViewParent(ViewParent view) {
        mViewParent = view;
    }

    /**
     * 刷新
     */
    public void onRefresh() {
        if (mListViewListener!=null){
            mListViewListener.onRefresh();
        }
    }
    //--------------------------------覆盖方法----------------------------------------------------
    @Override
    public void setAdapter(Adapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (!isFooterAdded) {//只会执行一次
            isFooterAdded = true;
            addFooterView(mFooterView);
            if (mHeadViewInfos.size() > 0 || mFootViewInfos.size() > 0) {
                adapter = new RVAdapter(mHeadViewInfos, mFootViewInfos, adapter);
                super.setAdapter(adapter);
            } else {
                super.setAdapter(adapter);
            }
        }
        mAdapter = adapter;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtil.d("jiacw3", "onTouch total " + getTotalItemCount());
        LogUtil.d("jiacw3", "onTouch last " + getLastVisiblePosition());
        if (mLastY == -1) {
            mLastY = ev.getRawY();//获取原始y坐标
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                //第一个项目出现，头部显示或拉下
                getTotalItemCount();
                LogUtil.d("jiacw4",getLastVisiblePosition()+" "+getTotalItemCount());
                if (getLastVisiblePosition() == getTotalItemCount() &&
                        (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default://抬起或划出
                mLastY = -1;//reset
                if (getLastVisiblePosition() == getTotalItemCount()) {
                    //invoke load more
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
                            && !mPullLoading) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_FOOTER) {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    //--------------------------实现接口--------------------------------------


    public void setOnItemClickListener(OnRVItemClickListener listener) {
        mOnRVItemClickListener = listener;
        if (mOnRVItemClickListener != null) {
            MyAdapter.realizeOnItemCLick(mOnRVItemClickListener);
        }
    }



}
