package com.zxy.nanonap.util;

import com.zxy.nanonap.R;

import java.util.ArrayList;
import java.util.List;

public class AudioConst {
    /**
     * 音源表  音源id 用position对应取得
     */
    public final static List<Integer> musicSourceList = new ArrayList<>();
    static {
        musicSourceList.add(R.raw.audio1);
        musicSourceList.add(R.raw.audio2);
        musicSourceList.add(R.raw.audio3);
        musicSourceList.add(R.raw.audio4);
        musicSourceList.add(R.raw.audio5);
        musicSourceList.add(R.raw.audio6);
    }

    /**
     * 图标表
     */
    public final static List<Integer> iconSourceList = new ArrayList<>();
    static {
        iconSourceList.add(R.drawable.bird_black);
        iconSourceList.add(R.drawable.huanjing_black);
        iconSourceList.add(R.drawable.shuye_black);
        iconSourceList.add(R.drawable.baoyu_black);
        iconSourceList.add(R.drawable.tv_black);
        iconSourceList.add(R.drawable.taifeng_black);
    }

    /**
     * 反色图标表
     */
    public final static List<Integer> iconSourceTransColorList = new ArrayList<>();
    static {
        iconSourceTransColorList.add(R.drawable.bird_white);
        iconSourceTransColorList.add(R.drawable.huanjing_white);
        iconSourceTransColorList.add(R.drawable.shuye_white);
        iconSourceTransColorList.add(R.drawable.baoyu_white);
        iconSourceTransColorList.add(R.drawable.tv_white);
        iconSourceTransColorList.add(R.drawable.taifeng_white);
    }

    public final static List<String> musicSourceNameList = new ArrayList<>();
    static {
        musicSourceNameList.add("鸟鸣");
        musicSourceNameList.add("环境");
        musicSourceNameList.add("安静");
        musicSourceNameList.add("下雨");
        musicSourceNameList.add("电视机");
        musicSourceNameList.add("风声");
    }
}
