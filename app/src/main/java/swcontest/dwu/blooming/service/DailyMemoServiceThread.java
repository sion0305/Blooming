package swcontest.dwu.blooming.service;


import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import swcontest.dwu.blooming.db.UserDBHelper;

import static swcontest.dwu.blooming.MainActivity.hour_sleep;
import static swcontest.dwu.blooming.MainActivity.hour_wake;
import static swcontest.dwu.blooming.MainActivity.minute_sleep;
import static swcontest.dwu.blooming.MainActivity.minute_wake;

public class DailyMemoServiceThread extends Thread {

    Handler handler;
    boolean isRun = true;
    int state = 0;

    public DailyMemoServiceThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized(this){
            this.isRun = false;
        }
    }

    public void run() {

//        if (getH - Integer.parseInt(hour_wake) >= 0 && (getH - Integer.parseInt(hour_wake)) % 4 == 0 && getM == Integer.parseInt(minute_wake) && getSeconds.equals("00")) {
        while (isRun) {
            //현재 시간 구함
            long now = System.currentTimeMillis();
            Date mDate = new Date(now);
            SimpleDateFormat simpleDateH = new SimpleDateFormat("HH");
            String getHour = simpleDateH.format(mDate);

            SimpleDateFormat simpleDateM = new SimpleDateFormat("mm");
            String getMinute = simpleDateM.format(mDate);

            SimpleDateFormat simpleDateS = new SimpleDateFormat("ss");
            String getSeconds = simpleDateS.format(mDate);

            int getH = Integer.valueOf(getHour);
            int getM = Integer.valueOf(getMinute);
            int getS = Integer.valueOf(getSeconds);
//
//            Log.d("따란1", String.valueOf(Integer.parseInt(hour_wake)));
//            Log.d("따란2", String.valueOf(Integer.parseInt(minute_wake)));
//
//            Log.d("따란3", String.valueOf(getSeconds));
//            Log.d("따란4", String.valueOf(minute_sleep));
//            Log.d("따란5", String.valueOf(Integer.parseInt(hour_sleep)));
//            Log.d("따란6", String.valueOf(Integer.parseInt(minute_sleep)));
//            Log.d("따란7", String.valueOf(getH));
//
//            Log.d("따란8", String.valueOf(getM));
//            Log.d("따란9",  String.valueOf(getS));

             // 알람 울린 후엔 1로.
////
            if (getH - Integer.parseInt(hour_wake) > 0 && getM == Integer.parseInt(minute_wake) && (getH - Integer.parseInt(hour_wake)) % 3 == 0 && state == 0) {
                if (Integer.parseInt(hour_sleep) > getH) {
                    handler.sendEmptyMessage(0);
                    state = 1;
                } else if (Integer.parseInt(hour_sleep) == getH) {
                    if (Integer.parseInt(minute_sleep) >= getM) {
                        handler.sendEmptyMessage(0);
                        state = 1;
                    }
                }
            }
            try {
                    if (state == 1){
                        Thread.sleep(1000 * 60 * 60 * 3); //3시간
                        state = 0;
                    }

                } catch (Exception e) {
                    }
                }
            }

    }
