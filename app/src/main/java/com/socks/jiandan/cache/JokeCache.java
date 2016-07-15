package com.socks.jiandan.cache;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.socks.greendao.JokeCacheDao;
import com.socks.jiandan.base.JDApplication;
import com.socks.jiandan.model.Joke;
import com.socks.jiandan.net.JSONParser;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;

public class JokeCache extends BaseCache {

    private static JokeCache instance;
    private static JokeCacheDao mJokeCacheDao;

    private JokeCache() {
    }  //私有化的构造方法

    public static JokeCache getInstance(Context context) {

        if (instance == null) {

            synchronized (JokeCache.class) {
                if (instance == null) {
                    instance = new JokeCache();//单例模式
                }
            }

            mDaoSession = JDApplication.getDaoSession(context);

            mJokeCacheDao = mDaoSession.getJokeCacheDao();
        }
        return instance;
    }

    /**
     * 清除全部缓存
     */
    public void clearAllCache() {
        mJokeCacheDao.deleteAll();
    }

    /**
     * 根据页码获取缓存数据
     *
     * @param page
     * @return
     */

    @Override
    public ArrayList<Joke> getCacheByPage(int page) {

          //Joke 实体类，代码自定生成的  ，查询页数相同的数据
        QueryBuilder<com.socks.greendao.JokeCache> query = mJokeCacheDao.queryBuilder().where(JokeCacheDao.Properties.Page.eq("" + page));

        if (query.list().size() > 0) {
            return (ArrayList<Joke>) JSONParser.toObject(query.list().get(0).getResult(),
                    new TypeToken<ArrayList<Joke>>() {
                    }.getType());
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * 添加Jokes缓存
     *
     * @param result
     * @param page
     */
    @Override
    public void addResultCache(String result, int page) {
        com.socks.greendao.JokeCache jokeCache = new com.socks.greendao.JokeCache();
        jokeCache.setResult(result);
        jokeCache.setPage(page);
        jokeCache.setTime(System.currentTimeMillis());

        mJokeCacheDao.insert(jokeCache);
    }

}
