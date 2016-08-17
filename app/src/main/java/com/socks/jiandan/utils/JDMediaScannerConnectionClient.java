package com.socks.jiandan.utils;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Looper;

import com.socks.jiandan.base.ConstantString;

import java.io.File;

/**
 * 媒体扫描库
 */
public class JDMediaScannerConnectionClient implements MediaScannerConnection
        .MediaScannerConnectionClient {

    private boolean isSmallPic;
    private File newFile;
    private MediaScannerConnection mediaScannerConnection; //首先new 一个新对象 ，然后需要实现接口

    public JDMediaScannerConnectionClient(boolean isSmallPic, File newFile) {
        this.isSmallPic = isSmallPic;
        this.newFile = newFile;
    }

    //获取这个对象
    public void setMediaScannerConnection(MediaScannerConnection mediaScannerConnection) {
        this.mediaScannerConnection = mediaScannerConnection;
    }


    //继承的2个方法
    @Override
    public void onMediaScannerConnected() {   //调用connect()方法会触发

        //调用 MediaScannerConnection 的 scanFile ，进行扫描文件（路径，文件类型）
        mediaScannerConnection.scanFile(newFile.getAbsolutePath(),null);
    }


    //扫描结束后会调用这个方法
    @Override
    public void onScanCompleted(String path, Uri uri) {
        Looper.prepare();//Looper封装消息队列的一个类 原因是非主线程中默认没有创建Looper对象，需要先调用Looper.prepare()启用Looper。

        if (isSmallPic) {  //判断
            ShowToast.Short(ConstantString.SAVE_SMALL_SUCCESS + " \n相册" + File.separator + CacheUtil
                    .FILE_SAVE + File.separator + newFile.getName());
        } else {
            ShowToast.Short(ConstantString.SAVE_SUCCESS + " \n相册" + File.separator + CacheUtil
                    .FILE_SAVE + File.separator + newFile.getName());
        }

        //MediaScannerConnection.disconnect()断开连接
        Looper.loop();// 让Looper开始工作，从消息队列里取消息，处理消息。 后面的代码不会执行
    }
}