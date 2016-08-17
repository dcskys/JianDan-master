package com.socks.jiandan.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.ui.fragment.FreshNewsDetailFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/*
* 新鲜事的详情界面*/

public class FreshNewsDetailActivity extends BaseActivity {

    @InjectView(R.id.vp)
    ViewPager viewPager;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_news_detail);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        ButterKnife.inject(this);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);  //设置toolbar 的颜色
        mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back); //设置返回按钮
    }

    @Override
    protected void initData() {


        //获取传递过来的实体列表
        ArrayList<FreshNews> FreshNews = (ArrayList<FreshNews>) getIntent().getSerializableExtra
                (DATA_FRESH_NEWS);
        //获取传递过来的位置
        int position = getIntent().getIntExtra(DATA_POSITION, 0);

        viewPager.setAdapter(new FreshNewsDetailAdapter(getSupportFragmentManager(), FreshNews));
        viewPager.setCurrentItem(position); //呈现的位置
    }


    private class FreshNewsDetailAdapter extends FragmentPagerAdapter {

        private ArrayList<FreshNews> freshNewses; //实体类的集合

        public FreshNewsDetailAdapter(FragmentManager fm, ArrayList<FreshNews> freshNewses) {
            super(fm);
            this.freshNewses = freshNewses;
        }

         //返回一个fragment
        @Override
        public Fragment getItem(int position) {
            return FreshNewsDetailFragment.getInstance(freshNewses.get(position));
        }

        @Override
        public int getCount() {
            return freshNewses.size();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
