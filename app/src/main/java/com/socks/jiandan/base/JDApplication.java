package com.socks.jiandan.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.facebook.stetho.Stetho;
import com.socks.greendao.DaoMaster;
import com.socks.greendao.DaoSession;
import com.socks.jiandan.BuildConfig;
import com.socks.jiandan.R;
import com.socks.jiandan.cache.BaseCache;
import com.socks.jiandan.utils.StrictModeUtil;
import com.socks.jiandan.utils.logger.LogLevel;
import com.socks.jiandan.utils.logger.Logger;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class JDApplication extends Application {

     //颜色
    public static int COLOR_OF_DIALOG = R.color.primary;
    public static int COLOR_OF_DIALOG_CONTENT = Color.WHITE;

    private static Context mContext; //全局context

    //GreenDao   数据库全局配置
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    private RefWatcher refWatcher;  //LeakCanary:检测所有的内存泄漏

    @Override
    public void onCreate() {

        StrictModeUtil.init();//内存检测配置
        super.onCreate();
        refWatcher = LeakCanary.install(this); //LeakCanary:检测所有的内存泄漏


        mContext = this;

        ImageLoadProxy.initImageLoader(this);//单例图片框架初始化


        if (BuildConfig.DEBUG) {  //日志工具类
            Logger.init().hideThreadInfo().setMethodCount(1).setLogLevel(LogLevel.FULL);
        }

         //android调试工具
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

    }

    public static Context getContext() {  //全局的context对象
        return mContext;
    }

    public static RefWatcher getRefWatcher(Context context) {
        JDApplication application = (JDApplication) context.getApplicationContext();
        return application.refWatcher;
    }



     //GreenDao  全局配置
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, BaseCache.DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;  //主要是为了获取这个session
    }

}