package com.jiacw.t03mynews.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jiacw.t03mynews.R;

/**
 * Created by Jiacw on 10:13 21/1/2016.
 * Email: 313133710@qq.com
 * Function:自定义头视图
 */
public class MyRVHeader extends LinearLayout {
    private static final int ROTATE_ANIM_DURATION = 180;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_REFESHING = 2;
    public static final int STATE_READY = 1;
    private LinearLayout mHeadLayout;
    private ImageView mIVArrow;
    private TextView mTVHint;
    private ProgressBar mProgressBar;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;
    private int mVisiableHeight;
    private int mLastState = STATE_NORMAL;

    //构造方法
    public MyRVHeader(Context context) {
        super(context);
        initView(context);
    }

    public MyRVHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * created at 21/1/2016 10:17
     * function: 初始化
     */
    private void initView(Context context) {
        //初始，设置头视图高度为0
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mHeadLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_head, null);
        //添加子视图
        addView(mHeadLayout, lp);
        //寻找控件
        mIVArrow = (ImageView) findViewById(R.id.lh_ivArrow);
        mTVHint = (TextView) findViewById(R.id.lh_tvHint);
        mProgressBar = (ProgressBar) findViewById(R.id.lh_pb);
        //箭头向上
        mRotateUpAnim = new RotateAnimation(0, -180f, Animation.RELATIVE_TO_SELF, 0.5f
                , Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        //箭头向下
        mRotateDownAnim = new RotateAnimation(-180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f
                , Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    /**
     * 获取可见高度
     * @return 头部视图高度
     */
    public int getVisibleHeight() {
        return mHeadLayout.getHeight();
    }

    /**
     * 设置可见高度
     * @param height 高度
     */
    public void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LinearLayout.LayoutParams lp = (LayoutParams) mHeadLayout.getLayoutParams();
        lp.height = height;
        mHeadLayout.setLayoutParams(lp);
    }

    /**
     * 设置动画和提示文字
     * @param state 状态
     */
    public void setLastState(int state) {
        if (state == mLastState) return;
        if (state == STATE_REFESHING) {//显示进度
            mIVArrow.clearAnimation();
            mIVArrow.setVisibility(INVISIBLE);
            mProgressBar.setVisibility(VISIBLE);
        } else {
            mIVArrow.setVisibility(VISIBLE);
            mProgressBar.setVisibility(INVISIBLE);
        }
        switch (state) {
            case STATE_NORMAL:
                if (mLastState == STATE_READY) {
                    mIVArrow.startAnimation(mRotateDownAnim);
                }
                if (mLastState == STATE_REFESHING) {
                    mIVArrow.clearAnimation();
                }
                mTVHint.setText(R.string.lh_tvHint);
                break;
            case STATE_READY:
                if (mLastState != STATE_READY) {
                    mIVArrow.clearAnimation();
                    mIVArrow.startAnimation(mRotateUpAnim);
                    mTVHint.setText(R.string.lh_tvHint_ready);
                }
                break;
            case STATE_REFESHING:
                mTVHint.setText(R.string.lh_tvHint_loading);
                break;
        }
        mLastState = state;
    }
}
