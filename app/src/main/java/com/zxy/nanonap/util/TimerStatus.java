package com.zxy.nanonap.util;

/**
 * @author zhangxiangyu
 * @version 1.0.0
 * @classname TimerStatus
 * @description TODO
 * @date 2023/9/12 11:49
 */
public enum TimerStatus {
    NO_TIME_NO_START(0,"未选时间、未开始倒计时"),
    HAS_TIME_NO_START(1,"已选时间、未开始倒计时"),
    HAS_TIME_PLAYING(2,"已选时间、正在倒计时"),
    HAS_TIME_PAUSE(3,"已选时间、倒计时暂停"),
    NO_TIME_RESET(4,"时间重置、倒计时重置");
    
    private int value;
    private String desc;

    TimerStatus() {
    }

    TimerStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
