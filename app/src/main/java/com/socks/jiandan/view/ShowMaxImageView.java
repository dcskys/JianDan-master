package com.socks.jiandan.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.socks.jiandan.utils.ScreenSizeUtil;

/**
 * 实现自动填充效果
 * 自定义控件，用于显示宽度和ImageView相同，高度自适应的图片显示模式.
 * 除此之外，还添加了最大高度限制，若图片长度大于等于屏幕长度，则高度显示为屏幕的1/3
 * Created by zhaokaiqiang on 15/4/20.
 */
public class ShowMaxImageView extends ImageView {

	private float mHeight = 0;

	public ShowMaxImageView(Context context) {
		super(context);
	}
    /*引入布局就会 引用这个函数*/
	public ShowMaxImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ShowMaxImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	@Override
	public void setImageBitmap(Bitmap bm) {

		if (bm != null) {
			getHeight(bm);
		}
		super.setImageBitmap(bm);

		requestLayout();  //重新绘制onMeasure函数
	}


	@Override
	public void setImageDrawable(Drawable drawable) {

		if (drawable != null) {
			getHeight(drawableToBitamp(drawable)); //drawable 转化成Bitmap
		}
		super.setImageDrawable(drawable);
		requestLayout();
	}



	/*尺寸测绘    宽度填充，高度自适应*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (mHeight != 0) {
			int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
			int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

			int resultHeight = (int) Math.max(mHeight, sizeHeight); //高取最大值

			//若图片长度大于等于屏幕长度，则高度显示为屏幕的1/2
			if (resultHeight >= ScreenSizeUtil.getScreenHeight((Activity) getContext())) {

				resultHeight = ScreenSizeUtil.getScreenHeight((Activity) getContext()) / 2;
			}
             //设置view的宽高
			setMeasuredDimension(sizeWidth, resultHeight);

		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}

	/*获取 宽和高*/
	private void getHeight(Bitmap bm) {

		float bitmapWidth = bm.getWidth();
		float bitmapHeight = bm.getHeight();

		if (bitmapWidth > 0 && bitmapHeight > 0) {
			float scaleWidth = getWidth() / bitmapWidth; //其实是屏幕宽 /图片宽
			mHeight = bitmapHeight * scaleWidth;  //高*这个比例
		}
	}

        /*把drawable 转化  bitmap*/
	private Bitmap drawableToBitamp(Drawable drawable) {

		if (drawable != null) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		} else {
			return null;
		}
	}


}
