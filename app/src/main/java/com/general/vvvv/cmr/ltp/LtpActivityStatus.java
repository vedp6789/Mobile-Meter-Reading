package com.general.vvvv.cmr.ltp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.general.SQLite.DbConnection;
import com.general.SQLite.DbLTPReading;
import com.general.SQLite.DbLtpDownloadStatus;
import com.general.SQLite.DownloadStatusDb;
import com.general.adapter.ExpandableAdapter;
import com.general.adapter.LtpExpandableAdapter;
import com.general.adapter.PojoDownload;
import com.general.vvvv.cmr.MyReading;
import com.general.vvvv.cmr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 23-01-2015.
 */
public class LtpActivityStatus extends ActionBarActivity {
    LtpExpandableAdapter ltpExpandableAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String, ArrayList<PojoDownload>> listDataChild;
    static Boolean download_data = false;


    DbLtpDownloadStatus dbLtpDownloadStatus;
    SQLiteDatabase liteDatabase1;

    DbLTPReading dbLTPReading;
    SQLiteDatabase sqLiteDatabase;

    ArrayList<PojoDownload> arrayList_myDownload = null;
    ArrayList<PojoDownload> arrayList_myReading = null;
    ArrayList<PojoDownload> arrayList_no_of_Lock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        dbLTPReading = new DbLTPReading(LtpActivityStatus.this, DbLTPReading.DB_NAME, null, DbLTPReading.DB_VERSION);
        sqLiteDatabase = dbLTPReading.getWritableDatabase();

        dbLtpDownloadStatus = new DbLtpDownloadStatus(LtpActivityStatus.this, DbLtpDownloadStatus.DB_NAME, null, DbLtpDownloadStatus.DB_VERSION);
        liteDatabase1 = dbLtpDownloadStatus.getWritableDatabase();

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        prepareListData();

        ltpExpandableAdapter = new LtpExpandableAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(ltpExpandableAdapter);

        // Listview Group click listener
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
               /*Toast.makeText(getApplicationContext(),
                 "Group Clicked " + listDataHeader.get(groupPosition),
                 Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });

        // Listview Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        // Listview Group collasped listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /*Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
*/
            }
        });
        //expandableListView.
        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
              /* Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition)+ " : "+ listDataChild.get(listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();*/
                int groupP = groupPosition;
                int childP = childPosition;
                //  Toast.makeText(getApplicationContext(),"GroupPosition: \t"+String.valueOf(groupP)+"\nChildPosition:\t"+String.valueOf(childP),Toast.LENGTH_LONG).show();

                if (groupP == 2) {
                    //  Toast.makeText(getApplicationContext(),"GroupPosition:..... \t"+String.valueOf(groupP)+"\nChildPosition:\t"+String.valueOf(childP),Toast.LENGTH_LONG).show();
                    PojoDownload pD = arrayList_no_of_Lock.get(childP);
                    int ltp_no = pD.getLtp_no();//lock Sc_no/connection Code get retrieved
                    //  Toast.makeText(getApplicationContext(),"SC:..... \t"+String.valueOf(s_Sc_no),Toast.LENGTH_LONG).show();
                    //Intent launch1=new Intent(MyStatus.this,MyReading.class);
                    //launch1.putExtra("C_code",s_Sc_no);
                    //startActivity(launch1);


                    SQLiteDatabase sqLiteDatabase1 = dbLTPReading.getReadableDatabase();
                    Cursor c = dbLTPReading.getMasterDetailForConnectionID(ltp_no, sqLiteDatabase1);
                    if (c.getCount() > 0) {
                        //Toast.makeText(getActivity(),"Search successful.",Toast.LENGTH_SHORT).show();

                        c.moveToFirst();
                        Bundle bundle = new Bundle();
                        bundle.putInt("C_Id", ltp_no);
                        bundle.putInt("C_code", Integer.parseInt(c.getString(c.getColumnIndex(DbLTPReading.LTP_NO))));
                        //bundle.putString("Meter_no", c.getString(c.getColumnIndex(DbConnection.METER_NO)));
                        bundle.putString("Area_code", c.getString(c.getColumnIndex(DbLTPReading.AREA)));
                        //bundle.putInt("Route_no", Integer.parseInt(c.getString(c.getColumnIndex(DbConnection.ROUTE_NO))));
                        bundle.putString("Cgl_no", c.getString(c.getColumnIndex(DbLTPReading.CGL_NO)));
                        bundle.putString("C_name", c.getString(c.getColumnIndex(DbLTPReading.NAME)));
                        // bundle.putString("Address",c.getString(c.getColumnIndex(DbConnection.ADDRESS)));
                        // bundle.putString("Mobile_no",c.getString(c.getColumnIndex(DbConnection.CONTACT_NO)));
                        //bundle.putString("Feeder_name",c.getString(c.getColumnIndex(DbConnection.FEEDERNAME)));


                        Intent intent = new Intent(LtpActivityStatus.this, LtpActivityReadingFromLtpStatus.class);
                        intent.putExtras(bundle);
                        // dismiss();
                        startActivity(intent);
                        finish();
                        sqLiteDatabase1.close();
                    } else {
                        Toast.makeText(LtpActivityStatus.this, "Consumer code either not valid or not get downloaded.", Toast.LENGTH_SHORT).show();
                        //et_Connection.setText("");
                        //sqLiteDatabase.close();
                        // break;
                    }
                    //MyActivity activity=new MyActivity();
                    //activity.cosumerSearchValidate();
                }


                return false;
            }
        });


    }

    public void prepareListData() {

        List<String> area_code = new ArrayList<String>();
        arrayList_myDownload = new ArrayList<PojoDownload>();
        arrayList_myReading = new ArrayList<PojoDownload>();
        arrayList_no_of_Lock = new ArrayList<PojoDownload>();

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, ArrayList<PojoDownload>>();

        listDataHeader.add("My Downloads");
        listDataHeader.add("Meter Reading");
        listDataHeader.add("Incomplete Meter Reading");
        //listDataHeader.add("Coming Soon..");


        Cursor cursor = dbLtpDownloadStatus.getAll();
        Log.d("getAll method cursor size", String.valueOf(cursor.getCount()));

        if (cursor.getCount() > 0) {

            download_data = true;
            cursor.moveToFirst();
            do {
                PojoDownload pojoDownload = new PojoDownload();
                String area_cod = cursor.getString(cursor.getColumnIndex(DbLtpDownloadStatus.AREA));
                area_code.add(area_cod);
                pojoDownload.setArea_code(area_cod);
                pojoDownload.setConumers(cursor.getInt(cursor.getColumnIndex(DbLtpDownloadStatus.NO_OF_CONSUMERS)));

                String date = cursor.getString(cursor.getColumnIndex(DbLtpDownloadStatus.RECORD_CREATION_DATE));
                String day = date.substring(0, 2);
                String month = date.substring(2, 4);
                String year = date.substring(4, 8);

                pojoDownload.setDate(day + "/" + month + "/" + year);

                arrayList_myDownload.add(pojoDownload);


            } while (cursor.moveToNext());
            listDataChild.put(listDataHeader.get(0), arrayList_myDownload);
            liteDatabase1.close();
        } else {
            download_data = false;
            liteDatabase1.close();
            //Toast.makeText(MyStatus.this,"").show();
        }
        //ArrayList<PojoDownload> arrayList1=new ArrayList<PojoDownload>();
        if (area_code.size() > 0) {

            for (int i = 0; i < area_code.size(); i++) {
                Log.d("area_code size", String.valueOf(area_code.size()));
                String b = area_code.get(i);

                int meter_reading_not_completed = 0;
                meter_reading_not_completed = dbLTPReading.getNotYetReadingCompleted(b.toString());
                int no_of_LOCK_status = 0;
                no_of_LOCK_status = dbLTPReading.getNoOfLockStatus(b.toString());
                int no_of_NO_METER = 0;
                int no_of_POWER_OFF = 0;
                int no_of_NO_DISPLAY = 0;
                no_of_NO_METER = dbLTPReading.getNoOfNoMeter(b.toString());
                no_of_POWER_OFF = dbLTPReading.getNoOfNoPowerOff(b.toString());
                no_of_NO_DISPLAY = dbLTPReading.getNoOfNoDisplay(b.toString());
                Cursor cursor_no_of_Lock_stat = dbLTPReading.getDetailsOfLockReading(b.toString());
                Cursor cursor_no_of_NO_METER = dbLTPReading.getDetailsOfNO_METER_Reading(b.toString());
                Cursor cursor_no_of_POWER_OFF = dbLTPReading.getDetailsOfPOWER_OFF_Reading(b.toString());
                Cursor cursor_no_of_NO_DISPLAY = dbLTPReading.getDetailsOfNoDisplayReading(b.toString());

                //PojoDownload pojoDownload_Lock= new PojoDownload();

                if (cursor_no_of_Lock_stat.getCount() > 0) {
                    cursor_no_of_Lock_stat.moveToFirst();
                    do {
                        PojoDownload pojoDownload_Lock = new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setLtp_no(cursor_no_of_Lock_stat.getInt(cursor_no_of_Lock_stat.getColumnIndex(DbLTPReading.LTP_NO)));

                        arrayList_no_of_Lock.add(pojoDownload_Lock);
                    } while (cursor_no_of_Lock_stat.moveToNext());
                }

                if (cursor_no_of_NO_METER.getCount() > 0) {
                    cursor_no_of_NO_METER.moveToFirst();
                    do {
                        PojoDownload pojoDownload_Lock = new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setLtp_no(cursor_no_of_NO_METER.getInt(cursor_no_of_NO_METER.getColumnIndex(DbLTPReading.LTP_NO)));

                        arrayList_no_of_Lock.add(pojoDownload_Lock);
                    } while (cursor_no_of_NO_METER.moveToNext());
                }
                if (cursor_no_of_POWER_OFF.getCount() > 0) {
                    cursor_no_of_POWER_OFF.moveToFirst();
                    do {
                        PojoDownload pojoDownload_Lock = new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setLtp_no(cursor_no_of_POWER_OFF.getInt(cursor_no_of_POWER_OFF.getColumnIndex(DbLTPReading.LTP_NO)));

                        arrayList_no_of_Lock.add(pojoDownload_Lock);
                    } while (cursor_no_of_POWER_OFF.moveToNext());
                }
                if (cursor_no_of_NO_DISPLAY.getCount() > 0) {
                    cursor_no_of_NO_DISPLAY.moveToFirst();
                    do {
                        PojoDownload pojoDownload_Lock = new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setLtp_no(cursor_no_of_NO_DISPLAY.getInt(cursor_no_of_NO_DISPLAY.getColumnIndex(DbLTPReading.LTP_NO)));

                        arrayList_no_of_Lock.add(pojoDownload_Lock);
                    } while (cursor_no_of_NO_DISPLAY.moveToNext());
                }


                Log.d("b:", b);
                //Toast.makeText(MyStatus.this,"Downlaoaded book_no :"+b,Toast.LENGTH_SHORT).show();
                Cursor newCur = dbLTPReading.getSuccussfulReading(b);
                //Toast.makeText(MyStatus.this,"Meter Reading done for "+String.valueOf(newCur.getCount()),Toast.LENGTH_SHORT).show();
                Integer no = newCur.getCount();
                if (no > 0) {

                    int no_of_con_meter_Reading_Success = 0;
                    newCur.moveToFirst();
                    do {
                        String meter_reading = null;
                        meter_reading = newCur.getString(newCur.getColumnIndex(DbLTPReading.KW));
                        if (meter_reading != null) {
                            ++no_of_con_meter_Reading_Success;
                            // newCur.close();
                            //sqLiteDatabase.close();

                        } else {
                            Log.d("LogMessage:", b + ": not found any meter reading");
                            //newCur.close();
                            //sqLiteDatabase.close();
                        }

                    } while (newCur.moveToNext());

                    PojoDownload pojoDownload1 = new PojoDownload();
                    pojoDownload1.setArea_code(b);
                    pojoDownload1.setConumers(no_of_con_meter_Reading_Success);
                    //pojoDownload1.setConumers(no);
                    pojoDownload1.setNo_of_met_read_nc(meter_reading_not_completed);
                    pojoDownload1.setNo_of_Lock_status(no_of_LOCK_status);
                    pojoDownload1.setNo_of_NO_Meter(no_of_NO_METER);
                    pojoDownload1.setNo_of_Power_Off(no_of_POWER_OFF);
                    pojoDownload1.setNo_of_NO_DISPLAY(no_of_NO_DISPLAY);


                    arrayList_myReading.add(pojoDownload1);

                    newCur.close();
                    sqLiteDatabase.close();

                } else {
                    newCur.close();
                    sqLiteDatabase.close();
                }
                //newCur.close();
                // sqLiteDatabase.close();
                //newCur.close();
            }
        }

        listDataChild.put(listDataHeader.get(0), arrayList_myDownload);
        listDataChild.put(listDataHeader.get(1), arrayList_myReading);
        listDataChild.put(listDataHeader.get(2), arrayList_no_of_Lock);


    }
}
