package com.socks.jiandan.utils;

import android.app.Activity;

/**
 * 获取屏幕的宽高
 */
public class ScreenSizeUtil {

	public static int getScreenWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	public static int getScreenHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

}
