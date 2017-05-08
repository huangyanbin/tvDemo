package com.palmwifi.ktv;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.palmwifi.ktv.manager.TimeManager;



/**
 * Created by David on 2016/12/29.
 */

public  class TimeService extends Service {


    public static  void  startService(Context context){
        Intent i = new Intent(context,TimeService.class);
        context.startService(i);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TimeManager.getInstance().start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        TimeManager.getInstance().cancel();
        super.onDestroy();
    }

}
