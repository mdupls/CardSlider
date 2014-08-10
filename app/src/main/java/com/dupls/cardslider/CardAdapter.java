package com.dupls.cardslider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by dupls on 2014-07-15.
 */
public class CardAdapter extends ArrayAdapter<Object> {

    private final LayoutInflater mInflater;

    public CardAdapter(Context context) {
        super(context, R.layout.list_item);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null, false);
        }
        return convertView;
    }
}
