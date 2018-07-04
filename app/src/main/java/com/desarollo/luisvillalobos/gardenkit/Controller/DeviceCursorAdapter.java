package com.desarollo.luisvillalobos.gardenkit.Controller;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.desarollo.luisvillalobos.gardenkit.R;

public class DeviceCursorAdapter extends CursorAdapter {
    public DeviceCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView lblDescription = (TextView) view.findViewById(R.id.lblDescription);
        lblDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")));

        TextView lblApikey = (TextView) view.findViewById(R.id.lblApiKey);
        lblApikey.setText(cursor.getString(cursor.getColumnIndexOrThrow("apiKey")));

        TextView lblDevice = (TextView) view.findViewById(R.id.lblDevice);
        lblDevice.setText(cursor.getString(cursor.getColumnIndexOrThrow("device")));

        TextView lblUser = (TextView) view.findViewById(R.id.lblUser);
        lblUser.setText(cursor.getString(cursor.getColumnIndexOrThrow("user")));
    }


}
