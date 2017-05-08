package com.palmwifi.ktv.manager;



import com.palmwifi.ktv.event.TimeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by David on 2017/3/26.
 */

public class TimeManager {

    private Timer timer;
    private Calendar calendar;

    private static TimeManager mInstance;
    public static TimeManager getInstance() {
        if (mInstance == null) {
            synchronized (TimeManager.class) {
                if (mInstance == null) {
                    mInstance = new TimeManager();
                }
            }
        }
        return mInstance;
    }

    private TimeManager(){

    }


    public void start(){
        calendar =  Calendar.getInstance();

        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                EventBus.getDefault().post(new TimeEvent.TimeChangedEvent());
                Calendar c = Calendar.getInstance();
               // Log.e("huang","计时器........."+c.get(Calendar.MINUTE));
                if(c.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH)){
                    EventBus.getDefault().post(new TimeEvent.DayChangedEvent());
                }else{
                    EventBus.getDefault().post(new TimeEvent.DayChangedEvent());
                }
                calendar = c;
            }
        };
        timer = new Timer();
        int seconds = 60-calendar.get(Calendar.SECOND);
        timer.schedule(task,seconds*1000,60*1000);
    }

    public void cancel(){
        if(timer != null) {
            timer.cancel();
        }
    }
}
