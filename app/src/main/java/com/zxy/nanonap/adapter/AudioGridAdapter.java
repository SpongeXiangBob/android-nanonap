package com.zxy.nanonap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxy.nanonap.R;
import com.zxy.nanonap.entity.GridItem;

import java.util.List;

public class AudioGridAdapter extends ArrayAdapter<GridItem> {
    private Context context;

    public AudioGridAdapter(Context context, List<GridItem> items) {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_list_item, parent, false);
        }

        ImageView iconImageView = convertView.findViewById(R.id.iconImageView);
        TextView textView = convertView.findViewById(R.id.textView);

        iconImageView.setImageResource(item.getIconResourceId());
        textView.setText(item.getText());

        return convertView;
    }


}