package com.zxy.nanonap.service;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.zxy.nanonap.R;
import com.zxy.nanonap.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioService extends Service {

    private AudioManager audioManager;
    private List<MediaPlayer> mediaPlayers = new ArrayList<>();     // mediaplayer
    private List<Integer> audioResources = new ArrayList<>();       // 音源id
    private boolean isPlaying = false;  // 标识：是否有声音输出
    private Map<Integer, Float> audioVolumeMap = new HashMap<>();   // 音源id---0.5

    private final static float DEFAULT_VOLUME = 0.5f;

    public AudioService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 创建并显示前台通知
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int audioResourceId = intent.getIntExtra("audio_resource_id", -1);
        if (audioResourceId != -1) {
            startAudio(audioResourceId);
            recordAudioVolumeToMap(audioResourceId, DEFAULT_VOLUME);
        }
        return START_STICKY;
    }

    // 停止特定音频的播放
    public void stopAudio(int audioResourceId) {
        int index = audioResources.indexOf(audioResourceId);
        if (index >= 0 && index < mediaPlayers.size()) {
            MediaPlayer mediaPlayer = mediaPlayers.get(index);
            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayers.remove(index);
        }
        audioResources.remove(index);
        if (mediaPlayers.size() == 0) {
            isPlaying = false;
        }
        removeFromAudioVolumeMap(audioResourceId);
    }

    // 开始特定音频的播放
    public void startAudio(int audioResourceId) {
        if (!audioResources.contains(audioResourceId)) {
            audioResources.add(audioResourceId);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioResourceId);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(DEFAULT_VOLUME, DEFAULT_VOLUME);
            mediaPlayers.add(mediaPlayer);
            startAllMediaPlayers();
        }
    }

    @Override
    public void onDestroy() {
        stopAllMediaPlayers();
        releaseAllMediaPlayers();
        mediaPlayers.clear();
        audioResources.clear();
        audioVolumeMap.clear();
        // 移除前台通知
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AudioServiceBinder();
    }

    public boolean pauseAllMediaPlayers() {
        if (mediaPlayers.size() == 0) {
            return false;
        }
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.pause();
        }
        isPlaying = false;
        return true;
    }

    public List<MediaPlayer> getMediaPlayers() {
        return this.mediaPlayers;
    }

    public List<Integer> getAudioResources() {
        return this.audioResources;
    }

    public class AudioServiceBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean startAllMediaPlayers() {
        if (mediaPlayers.size() == 0) {
            return false;
        }
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.start();
        }
        isPlaying = true;
        return true;
    }

    public boolean stopAllMediaPlayers() {
        if (mediaPlayers.size() == 0) {
            return false;
        }
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        return true;
    }

    public boolean releaseAllMediaPlayers() {
        if (mediaPlayers.size() == 0) {
            return false;
        }
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        return true;
    }

    // 获取特定 MediaPlayer 的音量
    public float getMediaPlayerVolume(int audioSourceId) {
        if (audioVolumeMap.get(audioSourceId) == null) {
            return 0.0f;
        }
        return audioVolumeMap.get(audioSourceId);
    }

    // 设置特定 MediaPlayer 的音量
    public void setMediaPlayerVolume(int mediaPlayerIndex, float volume) {
        if (mediaPlayerIndex >= 0 && mediaPlayerIndex < mediaPlayers.size()) {
            MediaPlayer mediaPlayer = mediaPlayers.get(mediaPlayerIndex);
            mediaPlayer.setVolume(volume, volume); // 设置音量
            recordAudioVolumeToMap(audioResources.get(mediaPlayerIndex), volume);
        }
    }

    public void recordAudioVolumeToMap(int audioResourceId, float volume) {
        audioVolumeMap.put(audioResourceId, volume);
    }

    public void removeFromAudioVolumeMap(int audioResourceId) {
        audioVolumeMap.remove(audioResourceId);
    }

    // ========= 前台服务 ==========

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "music_channel";

    // 创建前台通知
    private void createNotification() {
        // 创建通知渠道（适用于 Android 8.0 及更高版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Music Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 创建通知内容
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.mogui)
                .setContentTitle("正在播放音乐")
                .setContentText("音乐名称或其他信息")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 设置通知点击后的操作（例如打开音乐播放界面）
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // 设置 FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP 标志
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        // 显示前台通知
        startForeground(NOTIFICATION_ID, builder.build());
    }

}
