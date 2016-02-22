package com.jiacw.t03mynews.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jiacw.t03mynews.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by Jiacw on 21:53 11/1/2016.
 * Email: 313133710@qq.com
 * Function:新闻列表适配器
 */
public class RVAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEAD = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOT = 2;
    //添加首尾
    RecyclerView.Adapter mAdapter;
    ArrayList<View> mHeaderViewInfos;
    ArrayList<View> mFooterViewInfos;
    static final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();
    int firstHeadSize;

    //------------------------------构造函数-----------------------------------------------------


    public RVAdapter(ArrayList<View> headViewInfos, ArrayList<View> footViewInfos, RecyclerView.Adapter adapter) {
        mAdapter = adapter;
//        LogUtil.d("jiacw", "RVAdapter->mAdapter " + mAdapter.getItemCount() + " " + i++);
        if (headViewInfos == null) {
            mHeaderViewInfos = EMPTY_INFO_LIST;
        } else {
            mHeaderViewInfos = headViewInfos;
        }
        firstHeadSize = mHeaderViewInfos.size();
        if (footViewInfos == null) {
            mFooterViewInfos = EMPTY_INFO_LIST;
        } else {
            mFooterViewInfos = footViewInfos;
        }
    }

    //-----------------------------------接口方法--------------------------------------------
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEAD:
                View view = mHeaderViewInfos.get(0);
//                LogUtil.d("jiacw", "TYPE_HEAD ");
                mHeaderViewInfos.remove(0);
                return new VHHead(view);
            case TYPE_ITEM:
                return mAdapter.onCreateViewHolder(parent, viewType);
            case TYPE_FOOT:
                View foot = mFooterViewInfos.get(0);
                return new VHFoot(foot);
        }
        throw new RuntimeException("没有对应viewType：" + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        LogUtil.d("jiacw", "onBindViewHolder->mAdapter " + mAdapter + " " + i++);
        if (isHeader(position)) {
//            LogUtil.d("jiacw", "onBind Head " + position);
            return;
        }
        if (isFooter(position)) {
//            LogUtil.d("jiacw", "onBind Footer " + position);
            return;
        }
        if (mAdapter != null) {
            int adjPosition = position - firstHeadSize;
//            LogUtil.d("jiacw", "onBind Item " + adjPosition);
            mAdapter.onBindViewHolder(holder, adjPosition);
        }
    }

    @Override
    public int getItemCount() {
//        LogUtil.d("jiacw", "getItemCount->mAdapter " + mAdapter + " " + i++);
        if (mAdapter != null) {
            return firstHeadSize + getFootsCount() + mAdapter.getItemCount();
        } else {
            return firstHeadSize + getFootsCount();
        }
    }

    //-------------------------覆盖方法-------------------------------
    @Override
    public int getItemViewType(int position) {
//        LogUtil.d("jiacw", "getItemViewType->mAdapter " + mAdapter + " " + i++);
//        LogUtil.d("jiacw", "Type position " + position);
//        LogUtil.d("jiacw", "headSize " + firstHeadSize);
        if (position < firstHeadSize) {
            return TYPE_HEAD;
        }
        if (mAdapter != null) {
            int adjPosition = position - firstHeadSize;
            int adapterCount;
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return TYPE_ITEM;
            }
        }
        return TYPE_FOOT;
    }


    //---------------------------自定义方法----------------------------------
    private boolean isFooter(int position) {
        return position >= getItemCount() - getFootsCount();
    }

    private boolean isHeader(int position) {
        return position < firstHeadSize;
    }


    public int getFootsCount() {
        return mFooterViewInfos.size();
    }

    //-------------------ViewHolder类------------------------------------
    class VHHead extends RecyclerView.ViewHolder {

        public VHHead(View itemView) {
            super(itemView);
        }
    }

    class VHFoot extends RecyclerView.ViewHolder {

        public VHFoot(View itemView) {
            super(itemView);
        }
    }

}
