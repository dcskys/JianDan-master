package com.socks.jiandan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.socks.jiandan.R;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.cache.FreshNewsCache;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.net.JSONParser;
import com.socks.jiandan.net.Request4FreshNews;
import com.socks.jiandan.net.RequestManager;
import com.socks.jiandan.ui.FreshNewsDetailActivity;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.ShareUtil;
import com.socks.jiandan.utils.ShowToast;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


/*新鲜图的适配器*/
public class FreshNewsAdapter extends RecyclerView.Adapter<FreshNewsAdapter.ViewHolder> {

    private int page;
    private int lastPosition = -1;
    private boolean isLargeMode;  //大图模式
    private Activity mActivity;
    private DisplayImageOptions options;
    private ArrayList<FreshNews> mFreshNews;

    private LoadFinishCallBack mLoadFinisCallBack;
    private LoadResultCallBack mLoadResultCallBack;//接口 成功访问网络的常量 和2 个抽象方法onSuccess，onError

    public FreshNewsAdapter(Activity activity, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack, boolean isLargeMode) {
        this.mActivity = activity; //其实就是context对象
        this.isLargeMode = isLargeMode;
        this.mLoadFinisCallBack = loadFinisCallBack;
        this.mLoadResultCallBack = loadResultCallBack;
        mFreshNews = new ArrayList<>(); //列表

        int loadingResource = isLargeMode ? R.drawable.ic_loading_large : R.drawable.ic_loading_small;
        options = ImageLoadProxy.getOptions4PictureList(loadingResource); //大图还是小图 ，设置加载失败的图片大小
    }

     //View视图（每一个item） 滑动时出现的动画效果  (包括看上去 fragment从下面出来的效果也是这个)
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            //api自带方法实现动画
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_bottom_in); //下向上出现的动画效果
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    /*
    * 即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用）  */
    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
         /*因为item 加入动画时，快速滑动会出现卡顿 现象，需要调用次方法来清除动画 */
        if (isLargeMode) {
            holder.card.clearAnimation();
        } else {
            holder.ll_content.clearAnimation();
        }
    }


    //创建视图（判断创建大图还是小图）
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 是否是大图模式。呈现出2种布局
        int layoutId = isLargeMode ? R.layout.item_fresh_news : R.layout.item_fresh_news_small;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final FreshNews freshNews = mFreshNews.get(position);  //获取列表每一个item类

        //加载图片 ，单例模式  ，注意2个布局的id 完全一样
        ImageLoadProxy.displayImage(freshNews.getCustomFields().getThumb_m(), holder.img, options);

        holder.tv_title.setText(freshNews.getTitle());
        holder.tv_info.setText(freshNews.getAuthor().getName() + "@" + freshNews.getTags()
                .getTitle());
        holder.tv_views.setText("浏览" + freshNews.getCustomFields().getViews() + "次");

         //分享点击事件
        if (isLargeMode) {  //大图模式的点击事件

            holder.tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareUtil.shareText(mActivity, freshNews.getTitle() + " " + freshNews.getUrl());
                }
            });

             //cardView   点击事件 ，打开详情
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDetailActivity(position);
                }
            });
            setAnimation(holder.card, position); //出现的时候呈现动画

        } else {  //小图模式
            holder.ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDetailActivity(position);
                }
            });
           setAnimation(holder.ll_content, position); //动画
        }

    }


    //把当前的实体列表 （注意不指是实体）和位置传递给 详细页面
    private void toDetailActivity(int position) {
        Intent intent = new Intent(mActivity, FreshNewsDetailActivity.class);
        intent.putExtra(FreshNewsDetailActivity.DATA_FRESH_NEWS, mFreshNews);
        intent.putExtra(FreshNewsDetailActivity.DATA_POSITION, position);
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mFreshNews.size();
    }

    //加载第一页
    public void loadFirst() {
        page = 1;
        loadDataByNetworkType();
    }
     //加载更多页面
    public void loadNextPage() {
        page++;
        loadDataByNetworkType();
    }


    /*加载数据+
    * */
    private void loadDataByNetworkType() {

        if (NetWorkUtil.isNetWorkConnected(mActivity)) { //网络可用

            //addRequest 2个参数，1个请求，1个tag

            /*二次封装的请求*/
            RequestManager.addRequest(new Request4FreshNews(FreshNews.getUrlFreshNews(page),
                    new Response.Listener<ArrayList<FreshNews>>() {
                        @Override
                        public void onResponse(ArrayList<FreshNews> response) {
                            //接口，需要实现，在fragment中进行了实现回调
                            //注意mLoadFinisCallBack 实际是 recycleView
                            mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                            mLoadFinisCallBack.loadFinish(null);
                            if (page == 1) {
                                mFreshNews.clear(); //清空列表
                                FreshNewsCache.getInstance(mActivity).clearAllCache(); //清空数据库中的所有缓存
                            }
                            mFreshNews.addAll(response); //添加所有实体类到集合中

                            notifyDataSetChanged();//不用重新刷新Activity,通知Activity更新View
                            FreshNewsCache.getInstance(mActivity).addResultCache(JSONParser.toString(response),
                                    page); //添加缓存
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //接口，需要实现
                    mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, error.getMessage());
                    mLoadFinisCallBack.loadFinish(null);
                }
            }), mActivity);  //把mActivity 其实也就是context作为tag 添加到请求队列
        } else { //没有网络的时候
            mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
            mLoadFinisCallBack.loadFinish(null);

            if (page == 1) {
                mFreshNews.clear();
                ShowToast.Short(ConstantString.LOAD_NO_NETWORK);//提示没有网络
            }
            mFreshNews.addAll(FreshNewsCache.getInstance(mActivity).getCacheByPage(page));//根据页数添加缓存
            notifyDataSetChanged();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.tv_title)
        TextView tv_title;
        @InjectView(R.id.tv_info)
        TextView tv_info;
        @InjectView(R.id.tv_views)
        TextView tv_views;
        @Optional
        @InjectView(R.id.tv_share)
        TextView tv_share;
        @InjectView(R.id.img)
        ImageView img;
        @Optional
        @InjectView(R.id.card)
        CardView card;
        @Optional
        @InjectView(R.id.ll_content)
        LinearLayout ll_content;

        public ViewHolder(View contentView) {
            super(contentView);
            ButterKnife.inject(this, contentView);
        }
    }

}