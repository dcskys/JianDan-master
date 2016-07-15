package com.socks.jiandan.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadMoreListener;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;

/**
 * 自定义的recycleView
 * 实现了下拉加载  和 滑动暂停图片加载的功能
 */
public class AutoLoadRecyclerView extends RecyclerView implements LoadFinishCallBack {

    private LoadMoreListener loadMoreListener; //接口只有一个加载更多
    private boolean isLoadingMore;

    public AutoLoadRecyclerView(Context context) {
        this(context, null);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isLoadingMore = false;////用来标记是否向最后一个滑动
        addOnScrollListener(new AutoLoadScrollListener(null, true, true));//添加监听
    }


    /**
     * 如果需要显示图片，需要设置这几个参数，快速滑动时，暂停图片加载
     *
     * @param pauseOnScroll
     * @param pauseOnFling
     */
    public void setOnPauseListenerParams(boolean pauseOnScroll, boolean pauseOnFling) {
        addOnScrollListener(new AutoLoadScrollListener(ImageLoadProxy.getImageLoader(), pauseOnScroll, pauseOnFling));
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


     //当调用接口时，这个方法会得到调用
    @Override
    public void loadFinish(Object obj) {
        isLoadingMore = false;
    }

    /**
     * 滑动自动加载监听器
     */
    private class AutoLoadScrollListener extends OnScrollListener {

        private ImageLoader imageLoader;
        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;

        public AutoLoadScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
            this.imageLoader = imageLoader;
        }

        //   //滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。 //dx 表示横向    dy 表示纵向
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //由于GridLayoutManager是LinearLayoutManager子类，所以也适用
            if (getLayoutManager() instanceof LinearLayoutManager) {
                // // 获取最后一个完全显示的item的postion
                int lastVisibleItem = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

                int totalItemCount = AutoLoadRecyclerView.this.getAdapter().getItemCount();//总数

                //有回调接口，当刷新成功时，loadFinish方法 执行 上拉刷新方法会得到调用

                // 并且不是加载状态，并且剩下2个item ，dy纵向  并且向下滑动， 则调用这个方法

                //最后一个显示item的postion  >= 总数 -2   10>= 10 -2  ，才会执行下拉刷新方法
                if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount -
                        2 && dy > 0) {
                    loadMoreListener.loadMore(); //抽象方法 当实现接口时得到调用（具体实现为加载下一页）
                    isLoadingMore = true;
                }
            }
        }

        //滑动改变时候的监听
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            if (imageLoader != null) {  //用来在滑动的时候  加不加载图片

                switch (newState) { //newState滚动的状态

                    case SCROLL_STATE_IDLE: //表示滚动结束
                        imageLoader.resume();
                        break;
                    case SCROLL_STATE_DRAGGING: //正在拖动执行
                        if (pauseOnScroll) { //true 时暂停
                            imageLoader.pause();
                        } else {
                            imageLoader.resume();
                        }
                        break;
                    case SCROLL_STATE_SETTLING: //正在沉降相当于松手后
                        if (pauseOnFling) {  // true 暂停
                            imageLoader.pause();
                        } else {
                            imageLoader.resume();
                        }
                        break;
                }
            }
        }
    }

}
