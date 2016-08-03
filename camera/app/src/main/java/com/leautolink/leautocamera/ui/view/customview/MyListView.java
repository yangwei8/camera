package com.leautolink.leautocamera.ui.view.customview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.utils.Logger;

public class MyListView extends ListView implements AbsListView.OnScrollListener {
    View.OnTouchListener mGestureListener;




    private View touchView;

    /** 存储上下文 */
    private Context context;
    /** 上拉加载的ListView的回调监听 */
    private MyPullUpListViewCallBack myPullUpListViewCallBack;
    /** 记录第一行Item的数值 */
    private int firstVisibleItem;

    private boolean isScrllToBottom;
    private boolean isLoadingMore = false;

    public void touchView(View view) {
        touchView = view;
    }

    /** 底部显示正在加载的页面 */
    private View footerView = null;
    private TextView loadmore;


    public MyListView(Context context) {
        super(context);
        this.context = context;
        initListView();
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFadingEdgeLength(0);
        this.context = context;
        initListView();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initListView();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {

        // 为ListView设置滑动监听
        setOnScrollListener(this);
        // 去掉底部分割线
        setFooterDividersEnabled(false);
    }

    /**
     * 初始化话底部页面
     */
    public void initBottomView() {

        if (footerView == null) {
            footerView = LayoutInflater.from(this.context).inflate(
                    R.layout.listview_loadbar, null);
        }
        loadmore= (TextView) footerView.findViewById(R.id.loadmore);
        addFooterView(footerView);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchView != null) {
            //判断在视图内的动作则返回 false
            Rect rect = getTouchViewRect();
            if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public Rect getTouchViewRect(){
        if (touchView != null) {
            return new Rect(touchView.getLeft(), touchView.getTop(), touchView.getRight(), touchView.getBottom());
        }
        return null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //当滑动到底部时
        if (scrollState == SCROLL_STATE_IDLE
                || scrollState == SCROLL_STATE_FLING) {
            Logger.e("加载更多数据 " + isScrllToBottom   + "   :  " + isLoadingMore);
            if (isScrllToBottom&&!isLoadingMore) {
                isLoadingMore = true;
                // 当前到底部
                Logger.e("加载更多数据");
               this.setSelection(this.getCount());

                if (myPullUpListViewCallBack != null) {
                    myPullUpListViewCallBack.scrollBottomState();
                }

            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;

        if (footerView != null) {
//            //判断可视Item是否能在当前页面完全显示
//            if (visibleItemCount == totalItemCount) {
//                // removeFooterView(footerView);
//                footerView.setVisibility(View.GONE);//隐藏底部布局
//            } else {
//                // addFooterView(footerView);
//                footerView.setVisibility(View.VISIBLE);//显示底部布局
//            }
            if (getLastVisiblePosition() == totalItemCount -1){
                isScrllToBottom = true;

            }else {
                isScrllToBottom = false;

            }

        }
    }
    public void setMyPullUpListViewCallBack(
            MyPullUpListViewCallBack myPullUpListViewCallBack) {
        this.myPullUpListViewCallBack = myPullUpListViewCallBack;
    }

    /**
     * 上拉刷新的ListView的回调监听
     */
    public interface MyPullUpListViewCallBack {

        void scrollBottomState();
    }

    public void loadingEnd(){
        this.isLoadingMore = false;

    }

    public void stopLoadingMore(){
//
        //removeFooterView(footerView);
        this.isLoadingMore = true;
        loadmore.setText(getResources().getString(R.string.loadfinish));
    }

}