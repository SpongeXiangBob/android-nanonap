package com.zxy.nanonap.entity;

public class GridItem {
    private int iconResourceId;
    private String text;

    public GridItem(int iconResourceId, String text) {
        this.iconResourceId = iconResourceId;
        this.text = text;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getText() {
        return text;
    }
}
