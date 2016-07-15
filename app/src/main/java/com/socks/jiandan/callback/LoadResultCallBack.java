package com.socks.jiandan.callback;

/**
 *  成功访问网络的常量 和2 个抽象方法onSuccess，onError
 */
public interface LoadResultCallBack {

    int SUCCESS_OK = 1001;
    int SUCCESS_NONE = 1002;
    int ERROR_NET = 1003;

    void onSuccess(int result, Object object);

    void onError(int code, String msg);
}
