package com.socks.jiandan.ui.fragment;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.socks.jiandan.R;
import com.socks.jiandan.adapter.PictureAdapter;
import com.socks.jiandan.base.BaseFragment;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadMoreListener;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.NetWorkEvent;
import com.socks.jiandan.model.Picture;
import com.socks.jiandan.utils.JDMediaScannerConnectionClient;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.ShowToast;
import com.socks.jiandan.view.AutoLoadRecyclerView;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/*
* 无聊图界面
*
* LoadResultCallBack 接口 用来判断网络是否请求成功
*
* */
public class PictureFragment extends BaseFragment implements LoadResultCallBack, LoadFinishCallBack {

    @InjectView(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;//自定义的列表
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;//官方的下拉刷新控件
    @InjectView(R.id.loading)
    RotateLoading loading; //加载动画的id

    private PictureAdapter mAdapter;
    //用于判断网络发生了变化 ，进行提示
    private boolean isFirstChange;
    //记录最后一次提示显示时间，防止多次提示
    private long lastShowTime;

    private MediaScannerConnection connection; //媒体扫描服务
    protected Picture.PictureType mType;

    public PictureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  //允许fragment 使用菜单
        isFirstChange = true;
        mType = Picture.PictureType.BoringPicture; //实体类的枚举 （和菜单fragment中的枚举类似）
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_load, container, false); //公用的布局
        ButterKnife.inject(this, view); //注解框架
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setHasFixedSize(false); //每个item的高度不固定

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //上啦  加载更多
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                mAdapter.loadNextPage();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //下拉刷新  加载第一页
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.loadFirst();
            }
        });

        mRecyclerView.setOnPauseListenerParams(false, true); //设置滑动时停止加载动画


        mAdapter = new PictureAdapter(getActivity(), this, mRecyclerView, mType);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setmSaveFileCallBack(this); //

        mAdapter.loadFirst();//第一次加载  （没有使用刷新的状态，只是用了动画）
        loading.start(); //启动动画
    }


    /*在其绑定的activity中 监听了网络状态的变化*/
    public void onEventMainThread(NetWorkEvent event) {

        if (event.getType() == NetWorkEvent.AVAILABLE) {  //网络可用
            if (NetWorkUtil.isWifiConnected(getActivity())) { //网路可用且是wifi模式
                mAdapter.setIsWifi(true);
                //当判断为true  且  上次提示距离现在大于3秒
                if (!isFirstChange && (System.currentTimeMillis() - lastShowTime) > 3000) {
                    ShowToast.Short("已切换为WIFI模式，自动加载GIF图片");
                    lastShowTime = System.currentTimeMillis();
                }

            } else {
                mAdapter.setIsWifi(false);

                if (!isFirstChange && (System.currentTimeMillis() - lastShowTime) > 3000) {
                    ShowToast.Short("已切换为省流量模式，只加载GIF缩略图");
                    lastShowTime = System.currentTimeMillis();
                }
            }

            isFirstChange = false; //网络发生了变化 ，进行提示
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除内存缓存，避免由于内存缓存造成的图片显示不完整
        ImageLoadProxy.getImageLoader().clearMemoryCache();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {  //菜单栏的刷新重新加载最新界面
            mSwipeRefreshLayout.setRefreshing(true);
            mAdapter.loadFirst();
            return true;
        }
        return false;
    }


    @Override
    public void onSuccess(int result, Object object) {
        loading.stop();
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(int code, String msg) {
        loading.stop(); //停止动画
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);//刷新结束
        }
    }

    public void setType(Picture.PictureType mType) {
        this.mType = mType;
    }


    @Override
    public void loadFinish(Object obj) {
        //获取bundle 中的值
        Bundle bundle = (Bundle) obj;
        boolean isSmallPic = bundle.getBoolean(DATA_IS_SIAMLL_PIC);

        String filePath = bundle.getString(DATA_FILE_PATH);
        File newFile = new File(filePath);

        JDMediaScannerConnectionClient connectionClient = new JDMediaScannerConnectionClient(isSmallPic,
                newFile); //这个类的实例

        connection = new MediaScannerConnection(getActivity(), connectionClient);

        connectionClient.setMediaScannerConnection(connection);//传递MediaScannerConnection对象 到定义的类
        connection.connect(); //才能调动定义类的方法中的方法
    }
}