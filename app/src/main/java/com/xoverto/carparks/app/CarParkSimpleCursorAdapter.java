package com.xoverto.carparks.app;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by andrew on 11/07/2014.
 */
public class CarParkSimpleCursorAdapter extends SimpleCursorAdapter {

    public CarParkSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void setViewText(TextView v, String text) {
        if (v.getId() == android.R.id.text2) { // Make sure it matches your time field
            // You may want to try/catch with NumberFormatException in case `text` is not a numeric value
            //text = WhateverClass.getDate(Long.parseLong(text), "dd. MMMM yyyy hh:mm:ss");

            DateFormat dateF = DateFormat.getDateTimeInstance();
            text = "Last Updated: " + dateF.format(new Date(Long.parseLong(text)));
        }
        v.setText(text);
    }
}
