package com.socks.jiandan.view.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socks.jiandan.BuildConfig;
import com.socks.jiandan.R;

/**
 * 图片框架 的单例模式
 */
public class ImageLoadProxy {

    private static final int MAX_DISK_CACHE = 1024 * 1024 * 50;
    private static final int MAX_MEMORY_CACHE = 1024 * 1024 * 10;

    private static boolean isShowLog = false;

    private static ImageLoader imageLoader;

    public static ImageLoader getImageLoader() {

        if (imageLoader == null) {
            synchronized (ImageLoadProxy.class) {
                imageLoader = ImageLoader.getInstance();
            }
        }

        return imageLoader;
    }

    //初始化图片框架
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder build = new ImageLoaderConfiguration.Builder(context);
        build.tasksProcessingOrder(QueueProcessingType.LIFO);
        build.diskCacheSize(MAX_DISK_CACHE);//50 Mb sd卡(本地)缓存的最大值
        build.memoryCacheSize(MAX_MEMORY_CACHE);// 内存缓存的最大值
        build.memoryCache(new LruMemoryCache(MAX_MEMORY_CACHE));//可以通过自己的内存缓存实现
         //build.defaultDisplayImageOptions() //自定义图片缓存路径

        if (BuildConfig.DEBUG && isShowLog) {
            build.writeDebugLogs();
        }
        getImageLoader().init(build.build());
    }

    /**
     * 自定义Option
     *
     * @param url
     * @param target
     * @param options
     */
    public static void displayImage(String url, ImageView target, DisplayImageOptions options) {
        imageLoader.displayImage(url, target, options);
    }

    /**
     * 头像专用
     *
     * @param url
     * @param target
     *
     *  getOptions4Header() 加载头像专用Options
     */
    public static void displayHeadIcon(String url, ImageView target) {
        imageLoader.displayImage(url, target, getOptions4Header());

    }

    /**
     * 图片详情页专用
     *
     * @param url
     * @param target
     * @param loadingListener
     *
     * getOption4ExactlyType()  加载的图片可以进行缩放
     */
    public static void displayImage4Detail(String url, ImageView target, SimpleImageLoadingListener loadingListener) {
        imageLoader.displayImage(url, target, getOption4ExactlyType(), loadingListener);
    }

    /**
     * 图片列表页专用
     *
     * @param url
     * @param target
     * @param loadingResource
     * @param loadingListener
     * @param progressListener
     */
    public static void displayImageList(String url, ImageView target, int loadingResource, SimpleImageLoadingListener loadingListener, ImageLoadingProgressListener progressListener) {
        imageLoader.displayImage(url, target, getOptions4PictureList(loadingResource), loadingListener, progressListener);
    }

    /**
     * 自定义加载中图片
     *
     * @param url
     * @param target
     * @param loadingResource
     */
    public static void displayImageWithLoadingPicture(String url, ImageView target, int loadingResource) {
        imageLoader.displayImage(url, target, getOptions4PictureList(loadingResource));
    }

    /**
     * 使用本方法现下载到本地然后再加载\
     *
     *返回
     *
     * @param url
     * @param loadingListener
     *
     * getOption4ExactlyType()  加载的图片可以进行缩放
     */
    public static void loadImageFromLocalCache(String url, SimpleImageLoadingListener loadingListener) {
        imageLoader.loadImage(url, getOption4ExactlyType(), loadingListener);
    }

    /**
     * 设置图片放缩类型为模式EXACTLY，用于图片详情页的缩放
     *
     * @return
     */
    public static DisplayImageOptions getOption4ExactlyType() {

        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY) //设置图片的缩放类型
                .build();
    }

    /**
     * 加载头像专用Options，默认加载中、失败和空url为 ic_loading_small
     *
     * @return
     */
    public static DisplayImageOptions getOptions4Header() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageForEmptyUri(R.drawable.ic_loading_small)
                .showImageOnFail(R.drawable.ic_loading_small)
                .showImageOnLoading(R.drawable.ic_loading_small)
                .build();
    }

    /**
     * 加载图片列表专用，加载前会重置View
     * {@link com.nostra13.universalimageloader.core.DisplayImageOptions.Builder#resetViewBeforeLoading} = true
     *
     * @param loadingResource
     * @return
     *
     * 设置加载错误，失败等图片的尺寸，大图，小图
     */
    public static DisplayImageOptions getOptions4PictureList(int loadingResource) {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)  //下载前图片进行重置
                .showImageOnLoading(loadingResource)
                .showImageForEmptyUri(loadingResource)
                .showImageOnFail(loadingResource)
                .build();
    }

}
