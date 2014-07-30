package com.xoverto.carparks.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        //get reference to the row
        View view = super.getView(position, convertView, parent);
        //check for odd or even to set alternate colors to the row background
        if(position % 2 == 0){
            view.setBackgroundColor(Color.rgb(238, 233, 233));
        }
        else {
            view.setBackgroundColor(Color.rgb(255, 255, 255));
        }


        return view;
    }


    @Override
    public void setViewText(TextView v, String text) {
        if (v.getId() == R.id.updated) { // Make sure it matches your time field
            // You may want to try/catch with NumberFormatException in case `text` is not a numeric value
            //text = WhateverClass.getDate(Long.parseLong(text), "dd. MMMM yyyy hh:mm:ss");

            DateFormat dateF = DateFormat.getDateTimeInstance();
            if(text.isEmpty() == false) {
                text = "Last Updated: " + dateF.format(new Date(Long.parseLong(text)));
            }
        }


        v.setText(text);
    }
}
