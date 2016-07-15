package com.socks.jiandan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.socks.jiandan.R;
import com.socks.jiandan.adapter.FreshNewsAdapter;
import com.socks.jiandan.base.BaseFragment;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.callback.LoadMoreListener;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.utils.ShowToast;
import com.socks.jiandan.view.AutoLoadRecyclerView;
import com.victor.loading.rotate.RotateLoading;

import butterknife.ButterKnife;
import butterknife.InjectView;


/*�����½���
* �ӿ�  ���ɹ���������ĳ��� ��2 �����󷽷�onSuccess��onError
* */
public class FreshNewsFragment extends BaseFragment implements LoadResultCallBack {

    @InjectView(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;   //�Զ�����б�
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout; //�ٷ�������ˢ�¿ؼ�
    @InjectView(R.id.loading)
    RotateLoading loading;  //���ض�����id

    private FreshNewsAdapter mAdapter;

    public FreshNewsFragment() {
    }


    /*ǰһ�㶼����Activity�����menu�˵���һ������дonCreateOptionsMenu��onOptionsItemSelected������

������fragment�õĶ��ˣ�����fragment�������menu�˵��� setHasOptionsMenu(true);��Ȼ�˵��޷�ʹ��*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  //��fragment ʹ�ò˵�ѡ����������Ϊtrue,
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
        ButterKnife.inject(this, view); //ע��ʽ���
        return view;
    }

    @Override   //�����ڵ�activity ������ɺ����
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setHasFixedSize(false); /// true �������ȷ��ÿ��item�ĸ߶��ǹ̶��ģ��������ѡ������������

        //�Զ���ļ��ظ���
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                mAdapter.loadNextPage();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //����ˢ��
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.loadFirst();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setOnPauseListenerParams(false, true);//����ͼƬʱ�����ٻ���ʱ����ͣͼƬ����


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isLargeMode = sp.getBoolean(SettingFragment.ENABLE_FRESH_BIG, true); //�Ƿ������ͼ��Ĭ��Ϊ��ͼ

         //������õĹ��캯�� ��ע��  mRecyclerViewʵ����LoadFinishCallBack�ӿڣ�thisָ��ǰ�����ʵ����LoadResultCallBack�ӿ�
        mAdapter = new FreshNewsAdapter(getActivity(), mRecyclerView, this, isLargeMode);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.loadFirst(); //��һ�μ���  ��û��ʹ��ˢ�µ�״̬��ֻ�����˶�����
        loading.start(); //����������������û���ݳ��ְ�ɫ������ȡ���ݳɹ����������������ݵĽ��뷽ʽ�ж����������ϣ�
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            mAdapter.loadFirst();    //����ˢ�£����¼�������
            return true;
        }
        return false;
    }


    //����Ϊʵ�ֽӿ��еķ��� ,һ����ˢ�²��������ͻ�ִ������2������
    @Override
    public void onSuccess(int result, Object object) {
       loading.stop(); //ֹͣ����
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false); //ˢ�½���
        }
    }

    @Override
    public void onError(int code, String msg) {
        loading.stop();
        ShowToast.Short(ConstantString.LOAD_FAILED); //����ʧ��
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);//ˢ�½���
    }
    }
}