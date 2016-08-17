package com.socks.jiandan.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseFragment;
import com.socks.jiandan.model.MenuItem;
import com.socks.jiandan.ui.MainActivity;
import com.socks.jiandan.ui.SettingActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/*菜单的操作栏*/
public class MainMenuFragment extends BaseFragment {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView; //菜单列表

    @InjectView(R.id.rl_container)
    RelativeLayout rl_container; //设置的总布局

    private LinearLayoutManager mLayoutManager;
    private MainActivity mainActivity;
    private MenuAdapter mAdapter;
    private MenuItem.FragmentType currentFragment = MenuItem.FragmentType.FreshNews; //列表实体类中的一个字段

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof MainActivity) { //菜单必须继承MainActivity ,一般来说肯定是
            mainActivity = (MainActivity) activity;
        } else {
            throw new IllegalArgumentException("The activity must be a MainActivity !");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        ButterKnife.inject(this, view); //注解框架

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);//默认是垂直方向

         // 点击设置         跳转到设置界面
        rl_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
                mainActivity.closeDrawer(); //关闭菜单栏
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new MenuAdapter();
        addAllMenuItems(mAdapter); //添加数据
        mRecyclerView.setAdapter(mAdapter);
    }

    // 方便   写在内部了
    private class MenuAdapter extends RecyclerView.Adapter<ViewHolder> {

        private ArrayList<MenuItem> menuItems; //列表实体的集合

        public MenuAdapter() {
            menuItems = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item,
                    parent, false);
            return new ViewHolder(view);
        }

         //这里没有进行优化  ，没必要 哈
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final MenuItem menuItem = menuItems.get(position);  //获取每一个item

            holder.tv_title.setText(menuItem.getTitle());
            holder.img_menu.setImageResource(menuItem.getResourceId());

            holder.rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  //点击进行跳转

                    try {
                        if (currentFragment != menuItem.getType()) {  //当切换的界面不是当前页面时，就进行切换fragmnet

                             //获取列表实体 中的Fragment这一项  ，进行实例化
                            Fragment fragment = (Fragment) Class.forName(menuItem.getFragment()
                                    .getName()).newInstance(); //Class.forName 返回一个类

                            mainActivity.replaceFragment(R.id.frame_container, fragment); //切换主内容的fragment
                            currentFragment = menuItem.getType(); //重新赋值进行判断
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //如果是当前界面就不重新加载 新的fragment
                    mainActivity.closeDrawer();
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuItems.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_menu;
        private TextView tv_title;
        private RelativeLayout rl_container;


        public ViewHolder(View itemView) {
            super(itemView);
            img_menu = (ImageView) itemView.findViewById(R.id.img_menu);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            rl_container = (RelativeLayout) itemView.findViewById(R.id.rl_container);
        }
    }


    /**
     * 判断 是否显示 隐藏的妹子图
     */
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //要显示妹子图而现在没显示，则重现设置适配器
        if (sp.getBoolean(SettingFragment.ENABLE_SISTER, false) && mAdapter.menuItems.size() == 4) {
            addAllMenuItems(mAdapter);  //加载全部列表(包含妹子图)
            mAdapter.notifyDataSetChanged();
        } else if (!sp.getBoolean(SettingFragment.ENABLE_SISTER, false) && mAdapter.menuItems.size()
                == 5) {
            addMenuItemsNoSister(mAdapter); //加载部分列表
            mAdapter.notifyDataSetChanged();
        }

    }

    /*加载全部的列表项*/
    private void addAllMenuItems(MenuAdapter mAdapter) {

        mAdapter.menuItems.clear(); //清空列表
        mAdapter.menuItems.add(new MenuItem("新鲜事", R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,
                FreshNewsFragment.class));
        mAdapter.menuItems.add(new MenuItem("无聊图", R.drawable.ic_mood_white_24dp, MenuItem.FragmentType.BoringPicture,
                PictureFragment.class));
        mAdapter.menuItems.add(new MenuItem("妹子图", R.drawable.ic_local_florist_white_24dp, MenuItem.FragmentType.Sister,
                SisterFragment.class));
        mAdapter.menuItems.add(new MenuItem("段子", R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke, JokeFragment
                .class));
        mAdapter.menuItems.add(new MenuItem("小电影", R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,
                VideoFragment.class));
    }

    private void addMenuItemsNoSister(MenuAdapter mAdapter) {
        mAdapter.menuItems.clear();
        mAdapter.menuItems.add(new MenuItem("新鲜事", R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,
                FreshNewsFragment.class));
        mAdapter.menuItems.add(new MenuItem("无聊图", R.drawable.ic_mood_white_24dp, MenuItem.FragmentType.BoringPicture,
                PictureFragment.class));
        mAdapter.menuItems.add(new MenuItem("段子", R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke, JokeFragment
                .class));
        mAdapter.menuItems.add(new MenuItem("小电影", R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,
                VideoFragment.class));
    }

}