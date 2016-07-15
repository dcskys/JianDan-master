package com.socks.jiandan.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.base.JDApplication;
import com.socks.jiandan.model.NetWorkEvent;
import com.socks.jiandan.ui.fragment.FreshNewsFragment;
import com.socks.jiandan.ui.fragment.MainMenuFragment;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.ShowToast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;  //抽屉式布局

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private BroadcastReceiver netStateReceiver;
    private MaterialDialog noNetWorkDialog;  //Materia风格的第三方库对话框
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    @Override
    protected void initView() { //其实是实现父类的抽象接口

        ButterKnife.inject(this); //注解式框架

         //toolbat
        mToolbar.setTitleTextColor(Color.WHITE); //文字颜色
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //返回键可以用


         //用来和抽屉式菜单相配合
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();//这个之后才能调用菜单
                //Android3.0及以上版本默认menu是打开的，所以必须调用invalidateOptionsMenu()方法，然后系统将调用onPrepareOptionsMenu()执行update操作。
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle); //监听
         //2个不同的布局  （侧滑界面  和内容）
        replaceFragment(R.id.frame_container, new FreshNewsFragment()); //新鲜事

        replaceFragment(R.id.drawer_container, new MainMenuFragment());  //侧滑菜单，菜单的操作
    }


    @Override
    protected void initData() {

        netStateReceiver = new BroadcastReceiver() { //动态广播
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) { //监听网络状况的变化,一有变化就会启动这个广播
                    if (NetWorkUtil.isNetWorkConnected(MainActivity.this)) {
                        EventBus.getDefault().post(new NetWorkEvent(NetWorkEvent.AVAILABLE));//发送消息，网络可用
                    } else {
                        EventBus.getDefault().post(new NetWorkEvent(NetWorkEvent.UNAVAILABLE));//不可用
                    }
                }
            }
        };

        registerReceiver(netStateReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION)); //动态注册广播，用来监听网络状况的变化
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    public void onEvent(NetWorkEvent event) { //接收EventBus发出的变化

        if (event.getType() == NetWorkEvent.UNAVAILABLE) { //无网络的时候

            if (noNetWorkDialog == null) { //对话框
                noNetWorkDialog = new MaterialDialog.Builder(MainActivity.this)
                        .title("无网络连接")
                        .content("去开启网络?")
                        .positiveText("是")
                        .backgroundColor(getResources().getColor(JDApplication.COLOR_OF_DIALOG)) //获取全局中设置的颜色
                        .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .positiveColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .negativeColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .titleColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .negativeText("否")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent intent = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(intent);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                            }
                        })
                        .cancelable(false)
                        .build();
            }
            if (!noNetWorkDialog.isShowing()) {
                noNetWorkDialog.show();  //呈现对话框
            }
        }

    }

    @Override
    protected void onDestroy() { //注销动态广播
        super.onDestroy();
        unregisterReceiver(netStateReceiver);
    }

    @Override   //2次退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ShowToast.Short("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 菜单 抽屉操作   Drawer Method
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

}
