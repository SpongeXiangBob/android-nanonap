package com.zxy.nanonap.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.zxy.nanonap.R;
import com.zxy.nanonap.adapter.AudioGridAdapter;
import com.zxy.nanonap.adapter.VolumeAdapter;
import com.zxy.nanonap.common.MyApplication;
import com.zxy.nanonap.entity.GridItem;
import com.zxy.nanonap.entity.VolumeItem;
import com.zxy.nanonap.outter.CustomBottomDialog;
import com.zxy.nanonap.service.AudioService;
import com.zxy.nanonap.service.CountdownService;
import com.zxy.nanonap.util.AudioConst;
import com.zxy.nanonap.util.ButtonConst;
import com.zxy.nanonap.util.ShakeUtil;
import com.zxy.nanonap.util.TimerStatus;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.dmoral.toasty.Toasty;

public class MainActivity extends Activity {

    private final static int MAX_MEANWHILE_NUMBER = 3;

    /**
     * 存放所有 按钮
     */
    private GridView buttonGridView;
    // 测试用适配器
    private AudioGridAdapter audioGridAdapter;
    List<GridItem> audioGridItem = new ArrayList<>();
    // 声音进度条适配器
    VolumeAdapter volumeAdapter;
    List<VolumeItem> volumeItems = new ArrayList<>();
    RecyclerView recyclerView;

    private ImageButton playPauseButton;
    private ImageButton volumeButton;
    private ImageButton alarmButton;

    private AudioService audioService;
    private boolean isServiceBound = false;
    private long mExitTime;
//    private boolean isPlaying = false;

    /**
     * 计时服务相关
     */
    CountdownService countdownService;
    private boolean isServiceTimeBound = false;

    private Set<Integer> playingButtonSet = new HashSet<>();    // 放的是所有选中的button的position

    // 默认计时状态（写到MainActivity中去）保证不被销毁
    public TimerStatus curTimerStatus = TimerStatus.NO_TIME_NO_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        MyApplication.getInstance().setMainActivityContext(this);

        buttonGridView = findViewById(R.id.buttonGridView);
        playPauseButton = findViewById(R.id.btnPlayPause);
        alarmButton = findViewById(R.id.alarmButton);
        volumeButton = findViewById(R.id.volumeButton);
        bindAudioService();

        // 初始化适配器
        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(0),AudioConst.musicSourceNameList.get(0)));
        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(1),AudioConst.musicSourceNameList.get(1)));
        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(2),AudioConst.musicSourceNameList.get(2)));
        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(3),AudioConst.musicSourceNameList.get(3)));
        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(4),AudioConst.musicSourceNameList.get(4)));
        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));
//        audioGridItem.add(new GridItem(AudioConst.iconSourceList.get(5),AudioConst.musicSourceNameList.get(5)));

        audioGridAdapter = new AudioGridAdapter(this, audioGridItem);
        // 设置自定义适配器
        buttonGridView.setAdapter(audioGridAdapter);

        buttonGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toasty.warning(MainActivity.this, "长按了！！！:::" + i, Toast.LENGTH_SHORT, true).show();
                return false;
            }
        });

        // 设置GridView项的点击事件
        buttonGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取 Vibrator 实例
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // 震动持续的毫秒数
                long milliseconds = 30; // 0.5 秒
                // 触发震动
                vibrator.vibrate(milliseconds);

                ShakeUtil.startShakeByView(view,1f, 1f, 5f, 100);
                TextView textView = view.findViewById(R.id.textView);
                if (!playingButtonSet.contains(position)) {
                    // 打开当前条目
                    if (playingButtonSet.size() < MAX_MEANWHILE_NUMBER) {
                        playAudio(AudioConst.musicSourceList.get(position));
                        playingButtonSet.add(position);
                        // 这里还要切换 button的样式
                        view.setBackgroundResource(R.drawable.rounded_border_background_dark);
                        GridItem currentClickedItem = audioGridItem.get(position);
                        currentClickedItem.setIconResourceId(AudioConst.iconSourceTransColorList.get(position));
                        // 文字颜色也要变
                        textView.setTextColor(Color.WHITE);
                    } else {
                        Toasty.warning(MainActivity.this, "最多只能叠加" + MAX_MEANWHILE_NUMBER + "条音乐进行播放。", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    // 关闭当前条目
                    playingButtonSet.remove(position);
                    stopAudio(AudioConst.musicSourceList.get(position));
                    // 这里还要切换 button的样式
                    view.setBackgroundResource(R.drawable.rounded_border_background_light);
                    GridItem currentClickedItem = audioGridItem.get(position);
                    currentClickedItem.setIconResourceId(AudioConst.iconSourceList.get(position));
                    // 文字颜色也要变
                    textView.setTextColor(Color.BLACK);
                }
                audioGridAdapter.notifyDataSetChanged();
            }
        });

        // 播放 暂停按钮
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        // 音量按钮
        volumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShakeUtil.startShakeByView(v,1f, 1f, 5f, 100);
                if (playingButtonSet.size() == 0) {
                    Toasty.warning(MainActivity.this, "请选择音乐进行播放。", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                // 加载声音页面
                volumeItems.clear();
                for (Integer index: playingButtonSet) {
                    float mediaPlayerVolume = audioService.getMediaPlayerVolume(AudioConst.musicSourceList.get(index));
                    VolumeItem volumeItem = new VolumeItem(index, AudioConst.musicSourceList.get(index), audioService.getMediaPlayers().get(audioService.getAudioResources().indexOf(AudioConst.musicSourceList.get(index))), (int) (100 * mediaPlayerVolume));
                    volumeItems.add(volumeItem);
                }
                showVolumeDialog();
            }
        });

        // 闹钟按钮
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playingButtonSet.size() == 0) {
                    Toasty.warning(MainActivity.this, "请选择音乐进行播放。", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                ShakeUtil.startShakeByView(v,1f, 1f, 5f, 100);
                Intent intent = new Intent(MainActivity.this, CountdownActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     *　播放音乐
     * @param audioResource
     */
    private void playAudio(int audioResource) {
        if (isServiceBound) {
            Intent intent = new Intent(MainActivity.this, AudioService.class);
            intent.putExtra("audio_resource_id", audioResource);
            startService(intent);

            playPauseButton.setImageResource(R.drawable.ic_pause_white);
//            isPlaying = true;
        }
    }

    // 停掉指定音频
    private void stopAudio(int audioResource) {
        if (isServiceBound) {
            audioService.stopAudio(audioResource);
            if (playingButtonSet.size() == 0) {
                playPauseButton.setImageResource(R.drawable.ic_play_white);
//                isPlaying = false;
            }
        }
    }

    /**
     * 切换暂停 播放 按钮
     */
    private void togglePlayPause() {
        if (isServiceBound) {
            if (audioService.isPlaying()) {
                if (audioService.pauseAllMediaPlayers()) {
                    playPauseButton.setImageResource(R.drawable.ic_play_white);
//                    isPlaying = false;
                }
            } else {
                if (!audioService.startAllMediaPlayers()) {
                    Toasty.warning(MainActivity.this, "请选择音乐进行播放。", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                playPauseButton.setImageResource(R.drawable.ic_pause_white);
//                isPlaying = true;
            }
        }
    }

    /**
     * 展示音量调节对话框
     */
    private void showVolumeDialog() {

        View customVolumeView = getLayoutInflater().inflate(R.layout.custom_volume_dialog, null);
        recyclerView = customVolumeView.findViewById(R.id.volumeListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        volumeAdapter = new VolumeAdapter(volumeItems, audioService);
        recyclerView.setAdapter(volumeAdapter);

        CustomBottomDialog.Builder builder = new CustomBottomDialog.Builder(MainActivity.this);
        builder.setTitle("调节音量")
                .setCustomView(customVolumeView)
                .setIcon(R.drawable.ic_shengyin_white)
                .show();
    }

    /**
     * 在主页面按下返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toasty.info(MainActivity.this, "再按一次退出至后台。", Toast.LENGTH_SHORT, true).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Toasty.warning(MainActivity.this, "应用程序已切换至后台。", Toast.LENGTH_SHORT, true).show();
                moveTaskToBack(false);
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消音乐服务绑定
        cancleBindMusicService();
        // 这里也要去除 计时器的服务
        cancleBindTimeService();
        // 应用程序杀掉
        System.exit(0);
    }

    /**
     * ==================音乐服务绑定====================
     */

    private void bindAudioService() {
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioServiceBinder binder = (AudioService.AudioServiceBinder) service;
            audioService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioService = null;
            isServiceBound = false;
        }
    };

    /**
     * ==========
     * 计时服务相关
     * ==========
     */

    public CountdownService getCountdownService() {
        return this.countdownService;
    }

    public boolean getIsServiceTimeBound() {
        return this.isServiceTimeBound;
    }

    public void setCountdownService(CountdownService countdownService) {
        this.countdownService = countdownService;
        isServiceTimeBound = true;
    }

    public void setCountdownServiceUnbind() {
        this.countdownService = null;
        isServiceTimeBound = false;
    }

    public void bindCountdownService() {
        Intent intent = new Intent(MainActivity.this, CountdownService.class);
        bindService(intent, serviceTimeConnection, BIND_AUTO_CREATE);
    }

    public ServiceConnection serviceTimeConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CountdownService.CountdownServiceBinder binder = (CountdownService.CountdownServiceBinder) service;
            CountdownService countdownService = binder.getService();
            ((MainActivity) MyApplication.getInstance().getMainActivityContext()).setCountdownService(countdownService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ((MainActivity) MyApplication.getInstance().getMainActivityContext()).setCountdownServiceUnbind();
        }
    };

    /**
     * 取消定时服务的绑定并停止定时服务
     */
    public void cancleBindTimeService() {
        if (isServiceTimeBound) {
            unbindService(serviceTimeConnection);
            // 定时服务的销毁
            Intent intent = new Intent(this, CountdownService.class);
            stopService(intent);
            isServiceTimeBound = false;
        }
    }

    /**
     * 取消音乐服务的绑定并停止音乐服务
     */
    public void cancleBindMusicService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            // 服务的销毁
            Intent intent = new Intent(this, AudioService.class);
            stopService(intent);
            isServiceBound = false;
        }
    }

    /**
     * 设置当前时间状态
     */
    public void setCurTimerStatus(TimerStatus timerStatus) {
        this.curTimerStatus = timerStatus;
    }

    /**
     * 获取当前时间状态
     */
    public TimerStatus getCurTimerStatus(){
        return this.curTimerStatus;
    }

    /**
     * 暂停计时
     */
    public void pauseCountTime() {
        countdownService.pauseCountDown();
    }

    /**
     * 恢复计时
     */
    public void resumeCountTime() {
        countdownService.resumeCountDown();
    }

    /**
     * 自定义倒计时时间（毫秒）
     */
    public void setCountTimerSecond(long mills) {
        countdownService.setCountDownTime(mills);
    }

    /**
     * 返回剩余时间 毫秒
     * @return
     */
    public long getLeftMills() {
        return countdownService.getTimeLeftInMillis();
    }

    /**
     * 移除时间计时前台服务
     */
    public void removeTimeForeService() {
        countdownService.stopForeground(true);
    }

    /**
     * 计时服务结束事件
     */
    public void timeEndEvent() {
        // 清除掉音量list
        volumeItems.clear();
        // 当前计时状态 恢复未未开始计时
        setCurTimerStatus(TimerStatus.NO_TIME_NO_START);
        for (int position: playingButtonSet) {
            System.out.println(position);
            stopAudio(AudioConst.musicSourceList.get(position));
            GridItem currentClickedItem = audioGridItem.get(position);
            currentClickedItem.setIconResourceId(AudioConst.iconSourceList.get(position));
            View childView = buttonGridView.getChildAt(position);
            // 这里还要切换 button的样式
            childView.setBackgroundResource(R.drawable.rounded_border_background_light);
            ((ImageView) childView.findViewById(R.id.iconImageView)).setBackgroundResource(AudioConst.iconSourceList.get(position));
            // 文字颜色也要变
            ((TextView) childView.findViewById(R.id.textView)).setTextColor(Color.BLACK);
        }
        audioGridAdapter.notifyDataSetChanged();
        playingButtonSet.clear();
        // 清除service中资源
        audioService.clearAllRes();
        // 改变计时页面的按钮状态
        if (null != MyApplication.getInstance().getCountActivityContext()){
            ((CountdownActivity) MyApplication.getInstance().getCountActivityContext()).finish();
        }
        // 按钮恢复到暂停
        playPauseButton.setImageResource(R.drawable.ic_play_white);
        // 去掉计时前台服务
        removeTimeForeService();
        // 计时服务停止(但未取消绑定)
        Intent timeIntent = new Intent(this, CountdownService.class);
        stopService(timeIntent);
        // 音乐播放服务停止(但未取消绑定)
        Intent musicIntent = new Intent(this, AudioService.class);
        stopService(musicIntent);
    }

}