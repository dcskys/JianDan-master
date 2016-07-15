package com.socks.jiandan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.socks.jiandan.R;
import com.socks.jiandan.adapter.FreshNewsAdapter;
import com.socks.jiandan.base.BaseFragment;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.callback.LoadMoreListener;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.utils.ShowToast;
import com.socks.jiandan.view.AutoLoadRecyclerView;
import com.victor.loading.rotate.RotateLoading;

import butterknife.ButterKnife;
import butterknife.InjectView;


/*新鲜事界面
* 接口  ：成功访问网络的常量 和2 个抽象方法onSuccess，onError
* */
public class FreshNewsFragment extends BaseFragment implements LoadResultCallBack {

    @InjectView(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;   //自定义的列表
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout; //官方的下拉刷新控件
    @InjectView(R.id.loading)
    RotateLoading loading;  //加载动画的id

    private FreshNewsAdapter mAdapter;

    public FreshNewsFragment() {
    }


    /*前一般都是在Activity中添加menu菜单，一般是重写onCreateOptionsMenu和onOptionsItemSelected方法。

现在用fragment用的多了，就在fragment里面添加menu菜单， setHasOptionsMenu(true);不然菜单无法使用*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  //让fragment 使用菜单选项。。这个必须为true,
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
        ButterKnife.inject(this, view); //注解式框架
        return view;
    }

    @Override   //当所在的activity 启动完成后调用
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setHasFixedSize(false); /// true 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能

        //自定义的加载更多
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

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.loadFirst();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setOnPauseListenerParams(false, true);//设置图片时，快速滑动时，暂停图片加载


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isLargeMode = sp.getBoolean(SettingFragment.ENABLE_FRESH_BIG, true); //是否允许大图，默认为大图

         //这里调用的构造函数 ，注意  mRecyclerView实现了LoadFinishCallBack接口，this指当前这个类实现了LoadResultCallBack接口
        mAdapter = new FreshNewsAdapter(getActivity(), mRecyclerView, this, isLargeMode);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.loadFirst(); //第一次加载  （没有使用刷新的状态，只是用了动画）
        loading.start(); //启动动画（本来就没数据呈现白色，当获取数据成功，动画结束，数据的进入方式有动画从下往上）
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            mAdapter.loadFirst();    //下拉刷新，重新加载数据
            return true;
        }
        return false;
    }


    //下面为实现接口中的方法 ,一旦有刷新操作，，就会执行下面2个方法
    @Override
    public void onSuccess(int result, Object object) {
       loading.stop(); //停止动画
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false); //刷新结束
        }
    }

    @Override
    public void onError(int code, String msg) {
        loading.stop();
        ShowToast.Short(ConstantString.LOAD_FAILED); //加载失败
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);//刷新结束
    }
    }
}