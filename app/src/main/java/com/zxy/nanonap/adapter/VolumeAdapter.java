package com.zxy.nanonap.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zxy.nanonap.R;
import com.zxy.nanonap.entity.VolumeItem;
import com.zxy.nanonap.service.AudioService;
import com.zxy.nanonap.util.AudioConst;
import com.zxy.nanonap.util.ShakeUtil;

import java.util.List;

public class VolumeAdapter extends RecyclerView.Adapter<VolumeAdapter.ViewHolder> {

    private List<VolumeItem> volumeItemList;
    private AudioService audioService;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView volumeIcon;
        public SeekBar volumeSeekBar;
        public TextView volumeValueTextView;
        public LinearLayout volumeItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            volumeIcon = itemView.findViewById(R.id.volumeIcon);
            volumeSeekBar = itemView.findViewById(R.id.volumeSeekBar);
            volumeValueTextView = itemView.findViewById(R.id.volumeValueTextView);
            volumeItemLayout = itemView.findViewById(R.id.volumeItemLayout);
        }
    }

    public VolumeAdapter(List<VolumeItem> volumeItemList, AudioService audioService) {
        this.volumeItemList = volumeItemList;
        this.audioService = audioService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_volume_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        VolumeItem volumeItem = volumeItemList.get(position);
        holder.volumeIcon.setImageResource(AudioConst.iconSourceTransColorList.get(volumeItem.getIndex()));
        holder.volumeSeekBar.setProgress(volumeItem.getVolumeProgress());
        holder.volumeValueTextView.setText(AudioConst.musicSourceNameList.get(volumeItem.getIndex()));
        holder.volumeValueTextView.setTextColor(Color.WHITE);

        // 为SeekBar添加进度条调节事件监听器
        holder.volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 用户拖动进度条时，更新音量值
                volumeItem.setVolumeProgress(progress);
                volumeItem.getMediaPlayer().setVolume(progress / 100.0f, progress / 100.0f);
                audioService.recordAudioVolumeToMap(volumeItem.getAudioResourceId(), progress / 100.0f);
                ShakeUtil.startShakeByView(holder.volumeItemLayout,1f, 1.2f, 5f, 120);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 用户开始拖动进度条时的操作（可选）
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 用户停止拖动进度条时的操作（可选）
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return volumeItemList.size();
    }
}

