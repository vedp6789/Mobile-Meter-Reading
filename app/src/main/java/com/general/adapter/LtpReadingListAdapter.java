package com.general.adapter;


import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.widget.SimpleCursorAdapter;

/**
 * Created by vvvv on 05-02-2015.
 */
public class LtpReadingListAdapter extends android.support.v4.widget.SimpleCursorAdapter {

    public LtpReadingListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }



}
