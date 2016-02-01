package com.jiacw.t03mynews.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiacw.t03mynews.R;

/**
 * Created by Jiacw on 14:22 21/1/2016.
 * Email: 313133710@qq.com
 * Function:列表尾部视图
 */
public class SListViewFooter extends LinearLayout {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_LOADING = 2;
    private RelativeLayout mRLContentView;
    private ProgressBar mProgressBar;
    private TextView mTVHint;

    public SListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public SListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * created at 21/1/2016 14:26
     * function: 初始视图
     */
    private void initView(Context context) {
        LinearLayout footView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.layout_foot, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(footView, lp);
        mRLContentView = (RelativeLayout) footView.findViewById(R.id.lf_rl_content);
        mProgressBar = (ProgressBar) footView.findViewById(R.id.lf_pb);
        mTVHint = (TextView) footView.findViewById(R.id.lf_tvHint);
    }

    /**
     * created at 21/1/2016 15:27
     * function: 使内容不可见
     */
    public void hide() {
        LinearLayout.LayoutParams lp = (LayoutParams) mRLContentView.getLayoutParams();
        lp.height = 0;
        mRLContentView.setLayoutParams(lp);
    }
    /**
    * created at 21/1/2016 15:34
    * function: 使内容可见
    */
    public void show() {
        LinearLayout.LayoutParams lp = (LayoutParams) mRLContentView.getLayoutParams();
        lp.height= LayoutParams.WRAP_CONTENT;
        mRLContentView.setLayoutParams(lp);
    }
    /**
    * created at 21/1/2016 15:39
    * function: 设置尾部显示状态
    */
    public void setState(int state) {
        //初始化都不可见
        mTVHint.setVisibility(INVISIBLE);
        mProgressBar.setVisibility(INVISIBLE);
        if (state==STATE_READY){//拉动状态
            mTVHint.setVisibility(VISIBLE);
            mTVHint.setText(R.string.footer_ready);
        }else if (state==STATE_LOADING){
            mProgressBar.setVisibility(VISIBLE);
        }else {
            mTVHint.setVisibility(VISIBLE);
            mTVHint.setText(R.string.lf_tvHint);
        }
    }

    /**
     *设置尾部间距
     * @param height 高度
     */
    public void setBottomMargin(int height) {
        if (height<0){
            return ;
        }
        LinearLayout.LayoutParams lp= (LayoutParams) mRLContentView.getLayoutParams();
        lp.bottomMargin=height;
        mRLContentView.setLayoutParams(lp);
    }

    /**
     * 获取尾部间距
     * @return The bottom margin in pixels of the child
     */
    public int getBottomMargin() {
        LinearLayout.LayoutParams lp= (LayoutParams) mRLContentView.getLayoutParams();
        return lp.bottomMargin;
    }
}
