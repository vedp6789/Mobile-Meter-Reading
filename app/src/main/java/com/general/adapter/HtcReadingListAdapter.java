package com.general.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

/**
 * Created by vvvv on 07-02-2015.
 */
public class HtcReadingListAdapter extends SimpleCursorAdapter {
    public HtcReadingListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
}
