package com.palmwifi.ktv.db;
import com.palmwifi.ktv.comm.KApplication;

/**
 * Created by David on 2016/10/25.
 * 数据库辅助类
 */

public class DBHelper {

    private static DBHelper mInstance;
    private  DaoSession mDaoSession;


    private DBHelper(){}

    public static DBHelper getInstance(){
        if(mInstance == null){
            synchronized (DBHelper.class){
                if(mInstance == null){
                    mInstance = new DBHelper();
                }
            }
        }
        return mInstance;
    }

    public DaoSession getSession(){
        if(mDaoSession == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(KApplication.getInstance(), "ktv.db", null);
            DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
            mDaoSession = daoMaster.newSession();
        }
        return mDaoSession;
    }

    public SongDao getSongDao(){
        DaoSession daoSession = getSession();
        return  daoSession.getSongDao();
    }

    public FavSongDao getFavSongDao(){
        DaoSession daoSession = getSession();
        return  daoSession.getFavSongDao();
    }

}
