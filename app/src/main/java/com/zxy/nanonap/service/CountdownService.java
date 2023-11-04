package com.zxy.nanonap.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.zxy.nanonap.R;
import com.zxy.nanonap.activity.CountdownActivity;
import com.zxy.nanonap.activity.MainActivity;

public class CountdownService extends Service {

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private volatile boolean isPaused;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public CountdownService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        return START_STICKY;
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                if (!isPaused) {
                    // 每秒刷新一次倒计时 发送广播通知Activity刷新UI
                    Intent intent = new Intent("countdown");
                    intent.putExtra("countdown", millisUntilFinished);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                } else {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                // 计时结束时的操作
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        // 移除前台通知
        stopForeground(true);
        // 停止倒计时 (防止内存泄漏-计时器与前台通知同时清除)
        pauseCountDown();
        timeLeftInMillis = 0L;
    }

    // 暂停计时
    public synchronized void pauseCountDown() {
        isPaused = true;
        if (countDownTimer != null) {
            countDownTimer.cancel(); // 取消计时器
            countDownTimer = null;
        }
    }

    // 继续计时
    public synchronized void resumeCountDown() {
        isPaused = false;
        startCountDownTimer();
    }

    // 设置倒计时时间
    public synchronized void setCountDownTime(long millisInFuture) {
        timeLeftInMillis = millisInFuture;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        resumeCountDown();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new CountdownService.CountdownServiceBinder();
    }

    public class CountdownServiceBinder extends Binder {
        public CountdownService getService() {
            return CountdownService.this;
        }
    }

    // ========= 前台服务 ==========

    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "time_channel";

    // 创建前台通知
    private void createNotification() {
        // 创建通知渠道（适用于 Android 8.0 及更高版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Time Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 创建通知内容
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_black)
                .setContentTitle("正在倒计时")
                .setContentText("计时中-点击返回计时页面……")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 设置通知点击后的操作（例如打开音乐播放界面）
        Intent notificationIntent = new Intent(this, CountdownActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // 设置 FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP 标志
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        // 显示前台通知
        startForeground(NOTIFICATION_ID, builder.build());
    }

}
