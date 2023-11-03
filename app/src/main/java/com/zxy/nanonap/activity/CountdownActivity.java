package com.zxy.nanonap.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zxy.nanonap.R;
import com.zxy.nanonap.common.MyApplication;
import com.zxy.nanonap.service.AudioService;
import com.zxy.nanonap.service.CountdownService;
import com.zxy.nanonap.util.ButtonConst;
import com.zxy.nanonap.util.DateUtil;
import com.zxy.nanonap.util.TimerStatus;

import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class CountdownActivity extends Activity {

    /**
     * 所有组件
     */
    TextView countdownTextView;
    ImageButton startButton;
    ImageButton pauseButton;
    ImageButton resetButton;
    ImageButton selectTimeButton;
    ImageButton finishButton;

    /**
     * 其他属性
     */
    private Date lastSelectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 没绑定服务才需要重新绑定
        if (!((MainActivity) MyApplication.getInstance().getMainActivityContext()).getIsServiceTimeBound()) {
            ((MainActivity) MyApplication.getInstance().getMainActivityContext()).bindCountdownService();
        }
        setTheme(android.R.style.Theme_Dialog);
        // 禁用标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置对话框内容
        setContentView(R.layout.activity_countdown_dialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.dimAmount = 0.5f; //设置窗口之外部分透明程度
        attributes.x = 0;
        attributes.y = 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        attributes.height = (int) (screenHeight * 0.8);
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;;
        attributes.gravity = Gravity.BOTTOM; // 位于底部

        getWindow().setAttributes(attributes);
        // 设置下方这个 底部Activity就变成可点击了
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

        countdownTextView = findViewById(R.id.countdownTextView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        finishButton = findViewById(R.id.finishButton);

        // 获取背景和内容容器
        View dialogContainer = findViewById(R.id.dialogContainer);

        // 页面刚打开时 根据当前状态 设置各个按钮的可用状态
        toggleTimeButtonStyle();


        // 设置下滑手势关闭页面
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaY = e2.getY() - e1.getY();
                if (Math.abs(deltaY) > 200 && deltaY < 0) {
                    finish(); // 下滑关闭
                    overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
                    return true;
                }
                return false;
            }
        });

        dialogContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        /**
         * 上方退出按钮点击事件
         */
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
            }
        });

        /**
         * startButton按钮点击事件
         */
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新计时状态
                switch (getCurTimeStatus()) {
                    case HAS_TIME_NO_START:
                        setCurTimeStatus(TimerStatus.HAS_TIME_PLAYING);
                        break;
                    default:
                        System.out.println("error command");
                        break;
                }
                // 更新button样式
                toggleTimeButtonStyle();
                // 开启前台服务
                // 满足一定条件后即可开启服务
                if (((MainActivity) MyApplication.getInstance().getMainActivityContext()).getIsServiceTimeBound()) {
                    Intent intent = new Intent(MyApplication.getInstance().getMainActivityContext(), CountdownService.class);
                    startService(intent);
                } else {
                    ((MainActivity) MyApplication.getInstance().getMainActivityContext()).bindCountdownService();
                    Intent intent = new Intent(MyApplication.getInstance().getMainActivityContext(), CountdownService.class);
                    startService(intent);
                }
                // 开启服务计时


            }
        });

        /**
         * pauseButton按钮点击事件
         */
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新计时状态
                switch (getCurTimeStatus()) {
                    case HAS_TIME_PLAYING:
                        // 切换按钮文字 暂停

                        // 暂停服务

                        // 更新状态
                        setCurTimeStatus(TimerStatus.HAS_TIME_PAUSE);
                        break;
                    case HAS_TIME_PAUSE:
                        // 切换按钮文字 继续

                        // 继续服务

                        // 更新状态
                        setCurTimeStatus(TimerStatus.HAS_TIME_PLAYING);
                        break;
                    default:
                        System.out.println("error command");
                        break;
                }
                // 更新button样式
                toggleTimeButtonStyle();
            }
        });


        /**
         * resetButton按钮点击事件
         */
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1、lastSelectedDate重置
                lastSelectedDate = new Date(2013,0,1, 0, 0, 0);;
                // 2、Text重置
                countdownTextView.setText(DateUtil.formatDateToString(lastSelectedDate, DateUtil.HMS_DATE_FORMAT));
                // 3、状态重置
                switch (getCurTimeStatus()) {
                    case HAS_TIME_NO_START:
                        setCurTimeStatus(TimerStatus.NO_TIME_RESET);
                        break;
                    case HAS_TIME_PAUSE:
                        // 关时间服务
                        closeTimerService();
                        // 改状态
                        setCurTimeStatus(TimerStatus.NO_TIME_RESET);
                        break;
                    default:
                        System.out.println("error command");
                        break;
                }
                // 更新button样式
                toggleTimeButtonStyle();
            }
        });

        /**
         * selectTimeButton按钮点击事件
         */
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 设置一个默认时间 主要关注 时 分 秒
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(2013,0,1, 0, 0, 0);
                selectedDate = lastSelectedDate != null ? DateUtil.dateToCalendar(lastSelectedDate) : selectedDate;

                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(CountdownActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        if (DateUtil.formatDateToString(date, DateUtil.HMS_DATE_FORMAT).equals("00:00:00")) {
                            Toasty.warning(CountdownActivity.this, "请选择大于0的计时时间。", Toast.LENGTH_SHORT, true).show();
                            return;
                        }
                        countdownTextView.setText(DateUtil.formatDateToString(date, DateUtil.HMS_DATE_FORMAT));
                        lastSelectedDate = date;    // 记录一下上次选择的时间
                        // 更新计时状态
                        switch (getCurTimeStatus()) {
                            case NO_TIME_NO_START:
                            case HAS_TIME_NO_START:
                            case NO_TIME_RESET:
                                setCurTimeStatus(TimerStatus.HAS_TIME_NO_START);
                                break;
                            default:
                                System.out.println("error command");
                                break;
                        }
                        // 更新button样式
                        toggleTimeButtonStyle();
                    }
                }).setType(new boolean[]{false, false, false, true, true, true})// 默认全部显示
                        .setCancelText("取消")//取消按钮文字
                        .setSubmitText("确定")//确认按钮文字
                        .setContentTextSize(18)//滚轮文字大小
                        .setTitleSize(20)//标题文字大小
                        .setTitleText("选择倒计时时间")//标题文字
                        .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                        .isCyclic(true)//是否循环滚动
                        .setTitleColor(Color.BLACK)//标题文字颜色
                        .setSubmitColor(Color.GRAY)//确定按钮文字颜色
                        .setCancelColor(Color.GRAY)//取消按钮文字颜色
                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/                        .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                        .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .isDialog(false)//是否显示为对话框样式
                        .build();
                pvTime.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 切换按钮样式
     */
    private void toggleTimeButtonStyle() {
        int[] curStatusButtonEnableArray = ButtonConst.buttonEnableArray[getCurTimeStatus().getValue()];
        startButton.setEnabled(curStatusButtonEnableArray[0] == 1);
        pauseButton.setEnabled(curStatusButtonEnableArray[1] == 1);
        resetButton.setEnabled(curStatusButtonEnableArray[2] == 1);
        selectTimeButton.setEnabled(curStatusButtonEnableArray[3] == 1);
    }

    /**
     * 关掉时间服务（本Activity可用）
     */
    private void closeTimerService(){
        if (((MainActivity) MyApplication.getInstance().getMainActivityContext()).getIsServiceTimeBound()) {
            Intent intent = new Intent(MyApplication.getInstance().getMainActivityContext(), CountdownService.class);
            stopService(intent);
        }
        ((MainActivity) MyApplication.getInstance().getMainActivityContext()).cancleBindTimeService();
    }

    private TimerStatus getCurTimeStatus() {
        return ((MainActivity) MyApplication.getInstance().getMainActivityContext()).getCurTimerStatus();
    }

    private void setCurTimeStatus(TimerStatus timeStatus) {
        ((MainActivity) MyApplication.getInstance().getMainActivityContext()).setCurTimerStatus(timeStatus);
    }

}
