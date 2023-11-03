package com.zxy.nanonap.entity;

import android.media.MediaPlayer;

public class VolumeItem {
    private Integer index; // 音量项的唯一标识符position
    private Integer audioResourceId;    // 音源id
    private MediaPlayer mediaPlayer;    // 在哪个音乐播放器播放着呢
    private int volumeProgress; // 音量进度值（0-100）

    public VolumeItem() {
    }

    public VolumeItem(Integer index, Integer audioResourceId, MediaPlayer mediaPlayer, int volumeProgress) {
        this.index = index;
        this.audioResourceId = audioResourceId;
        this.mediaPlayer = mediaPlayer;
        this.volumeProgress = volumeProgress;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getAudioResourceId() {
        return audioResourceId;
    }

    public void setAudioResourceId(Integer audioResourceId) {
        this.audioResourceId = audioResourceId;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public Integer getIndex() {
        return index;
    }

    public int getVolumeProgress() {
        return volumeProgress;
    }

    public void setVolumeProgress(int volumeProgress) {
        this.volumeProgress = volumeProgress;
    }
}

