package com.general.vvvv.cmr.ltp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.general.SQLite.DbLTPReading;
import com.general.adapter.LtpReadingListAdapter;
import com.general.vvvv.cmr.R;

/**
 * Created by vvvv on 05-02-2015.
 */
public class LtpActivityAreaConsumerList extends ActionBarActivity {
    TextView tv_area;
    ListView lv_reading_by_area;
    LtpReadingListAdapter ltpReadingListAdapter;
    DbLTPReading dbLTPReading;
    SQLiteDatabase sqLiteDatabase=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltp_cosumer_by_area);
        dbLTPReading=new DbLTPReading(LtpActivityAreaConsumerList.this,DbLTPReading.DB_NAME,null,DbLTPReading.DB_VERSION);
        sqLiteDatabase=dbLTPReading.getReadableDatabase();
        String area_code=getIntent().getExtras().getString("area");
        final Cursor cursor=dbLTPReading.getListOfConsumers(area_code,sqLiteDatabase);
        lv_reading_by_area= (ListView) findViewById(R.id.lv_ltp_reading);
        tv_area= (TextView) findViewById(R.id.tv_area_val);
        tv_area.setText(area_code);

        ltpReadingListAdapter=new LtpReadingListAdapter(LtpActivityAreaConsumerList.this,R.layout.ltp_area_adapter,cursor,new String[]{DbLTPReading.LTP_NO,DbLTPReading.NAME},new int[]{R.id.tv_ltp_val,R.id.tv_name_val},0);
        lv_reading_by_area.setAdapter(ltpReadingListAdapter);
        lv_reading_by_area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent intent=new Intent(LtpActivityAreaConsumerList.this,LtpActivityReadingForConsumerList.class);
                intent.putExtra("LTP",cursor.getInt(cursor.getColumnIndex(DbLTPReading.LTP_NO)));
                startActivity(intent);
                finish();
            }
        });
        //cursor.close();
        //sqLiteDatabase.close();





    }
}
