package com.general.vvvv.cmr.htc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.general.SQLite.DbHTCReading;
import com.general.SQLite.DbLTPReading;
import com.general.adapter.HtcReadingListAdapter;
import com.general.adapter.LtpReadingListAdapter;
import com.general.vvvv.cmr.R;
import com.general.vvvv.cmr.ltp.LtpActivityReadingForConsumerList;

/**
 * Created by vvvv on 07-02-2015.
 */
public class HtcActivityAreaConsumerList extends ActionBarActivity {
    TextView tv_area;
    ListView lv_reading_by_area;
    HtcReadingListAdapter htcReadingListAdapter;
    DbHTCReading dbHTCReading;
    SQLiteDatabase sqLiteDatabase=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltp_cosumer_by_area);
        dbHTCReading=new DbHTCReading(HtcActivityAreaConsumerList.this,DbHTCReading.DB_NAME,null,DbHTCReading.DB_VERSION);
        sqLiteDatabase=dbHTCReading.getReadableDatabase();
        String area_code=getIntent().getExtras().getString("area");
        final Cursor cursor=dbHTCReading.getListOfConsumers(area_code,sqLiteDatabase);
        lv_reading_by_area= (ListView) findViewById(R.id.lv_ltp_reading);
        tv_area= (TextView) findViewById(R.id.tv_area_val);
        tv_area.setText(area_code);

        htcReadingListAdapter=new HtcReadingListAdapter(HtcActivityAreaConsumerList.this,R.layout.ltp_area_adapter,cursor,new String[]{DbHTCReading.HTC_NO,DbHTCReading.NAME},new int[]{R.id.tv_ltp_val,R.id.tv_name_val},0);
        lv_reading_by_area.setAdapter(htcReadingListAdapter);
        lv_reading_by_area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent intent=new Intent(HtcActivityAreaConsumerList.this,HtcActivityReadingForConsumerList.class);
                intent.putExtra("HTC",cursor.getInt(cursor.getColumnIndex(DbHTCReading.HTC_NO)));
                startActivity(intent);
                finish();
            }
        });

        //cursor.close();
        //sqLiteDatabase.close();





    }

}
