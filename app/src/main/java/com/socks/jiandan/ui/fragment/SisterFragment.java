package com.socks.jiandan.ui.fragment;

import android.os.Bundle;

import com.socks.jiandan.model.Picture;


/*����ͼ ���ò���   ����ͼ�Ĳ���*/
public class SisterFragment extends PictureFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mType = Picture.PictureType.Sister;
    }
}
