package com.jiacw.t03mynews.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.jiacw.t03mynews.R;

/**
 * Created by Jiacw on 19:57 20/1/2016.
 * Email: 313133710@qq.com
 * Function:自定义ListView
 */
public class SListView extends ListView implements AbsListView.OnScrollListener {
    public enum State{

    }
    private float mLastY = -1;
    private Scroller mScroller;//用来回滚
    private OnScrollListener mScrollListener;//滚动监听
    //接口用来引发刷新和加载更多
    private IXListViewListener mListViewListener;
    //头部视图
    private SListViewHeader mHeaderView;
    private RelativeLayout mHeaderViewContent;//头部试图内容，用来计算头部，隐藏当不可刷新时
    private TextView mHeaderTime;
    private int mHeaderViewHeight;
    private final boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false;
    //尾部视图
    private SListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean isFooterReady = false;
    private int mTotalItemCount;//列表数目，用来检测是否到底部
    //回滚
    private int mScrollBack;
    private static final int SCROLLBACK_HEADER = 0;
    private static final int SCROLLBACK_FOOTER = 1;
    private static final int SCROLL_DURATION = 400;
    private static final int PULL_LOAD_MORE_DELTA = 50;//当拉动超过50像素，引起更多
    private static final float OFFSET_RADIO = 1.8f;

    //构造方法
    public SListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public SListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    /**
     * created at 21/1/2016 9:49
     * function: 初始化
     */
    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        //监听ListView的滚动事件
        super.setOnScrollListener(this);
        //实例化头部视图
        mHeaderView = new SListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.lh_RL_content);
        mHeaderTime = (TextView) mHeaderView.findViewById(R.id.lh_tvUpdateTime);
        //添加头部视图
        addHeaderView(mHeaderView);
        //初始化尾部视图
        mFooterView = new SListViewFooter(context);
        //获取头部试图的高度
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (!isFooterReady) {
            isFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }
    //实现滚动接口的方法
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount
            , int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
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
            mFooterView.setState(SListViewFooter.STATE_NORMAL);
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
        mFooterView.setState(SListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    /**
     * 注册监听对象，监听尾部加载和头部刷新
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
            resetHeaderHeight();
        }
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) {
            return;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0;// default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisibleHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }



    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(SListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * 设置刷新时间
     */
    public void setRefreshTime() {
        mHeaderTime.setText("刚刚");
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();//获取原始y坐标
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                //第一个项目出现，头部显示或拉下
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisibleHeight() > 0
                        || deltaY > 0)) {
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    //最后一个项目已经拉上或想拉上
                } else if (getLastVisiblePosition() == mTotalItemCount - 1 &&
                        (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default://抬起或划出
                mLastY = -1;//reset
                if (getFirstVisiblePosition() == 0) {
                    //调用刷新
                    if (mEnablePullRefresh && mHeaderView.getVisibleHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setLastState(SListViewHeader.STATE_REFESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
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
                mFooterView.setState(SListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(SListViewFooter.STATE_NORMAL);
            }
            mFooterView.setBottomMargin(height);

        }
    }

    /**
     * 更新头部视图高度
     *
     * @param delta 滑动值
     */
    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisibleHeight((int) (delta + mHeaderView.getVisibleHeight()));
        if (mEnablePullRefresh && !mPullRefreshing) {
            if (mHeaderView.getVisibleHeight() > mHeaderViewHeight) {
                mHeaderView.setLastState(SListViewHeader.STATE_READY);
            } else {
                mHeaderView.setLastState(SListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0);// scroll to top each time
    }

    /**
     * created at 21/1/2016 15:57
     * function: 实现接口，刷新和加载更多活动
     */
    public interface IXListViewListener {
        void onRefresh();

        void onLoadMore();
    }


}
