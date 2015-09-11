package com.general.vvvv.cmr.htc;

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

import com.general.SQLite.DbHTCReading;
import com.general.SQLite.DbHtcDownloadStatus;
import com.general.adapter.HtcExpandableAdapter;
import com.general.adapter.PojoDownload;
import com.general.vvvv.cmr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 23-01-2015.
 */
public class HtcActivityStatus extends ActionBarActivity {
    HtcExpandableAdapter htcExpandableAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String,ArrayList<PojoDownload>> listDataChild;
    static Boolean download_data=false;


    DbHtcDownloadStatus dbHtcDownloadStatus;
    SQLiteDatabase liteDatabase1;

    DbHTCReading dbHTCReading;
    SQLiteDatabase sqLiteDatabase;

    ArrayList<PojoDownload> arrayList_myDownload=null;
    ArrayList<PojoDownload> arrayList_myReading=null;
    ArrayList<PojoDownload> arrayList_no_of_HT_Lock=null;
    ArrayList<PojoDownload> arrayList_no_of_LT_Lock=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        dbHTCReading=new DbHTCReading(HtcActivityStatus.this,DbHTCReading.DB_NAME,null,DbHTCReading.DB_VERSION);
        sqLiteDatabase=dbHTCReading.getWritableDatabase();

        dbHtcDownloadStatus=new DbHtcDownloadStatus(HtcActivityStatus.this,DbHtcDownloadStatus.DB_NAME,null,DbHtcDownloadStatus.DB_VERSION);
        liteDatabase1=dbHtcDownloadStatus.getWritableDatabase();

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        prepareListData();

        htcExpandableAdapter = new HtcExpandableAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(htcExpandableAdapter);

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
                    PojoDownload pD = arrayList_no_of_HT_Lock.get(childP);
                    int htc_no = pD.getHtc_no();//lock Sc_no/connection Code get retrieved
                    //  Toast.makeText(getApplicationContext(),"SC:..... \t"+String.valueOf(s_Sc_no),Toast.LENGTH_LONG).show();
                    //Intent launch1=new Intent(MyStatus.this,MyReading.class);
                    //launch1.putExtra("C_code",s_Sc_no);
                    //startActivity(launch1);


                    SQLiteDatabase sqLiteDatabase1 = dbHTCReading.getReadableDatabase();
                    Cursor c = dbHTCReading.getMasterDetailForConnectionID(htc_no, sqLiteDatabase1);
                    if (c.getCount() > 0) {
                        //Toast.makeText(getActivity(),"Search successful.",Toast.LENGTH_SHORT).show();

                        c.moveToFirst();
                        Bundle bundle = new Bundle();
                        bundle.putInt("C_Id", htc_no);
                        bundle.putInt("C_code", Integer.parseInt(c.getString(c.getColumnIndex(DbHTCReading.HTC_NO))));
                        //bundle.putString("Meter_no", c.getString(c.getColumnIndex(DbConnection.METER_NO)));
                        bundle.putString("Area_code", c.getString(c.getColumnIndex(DbHTCReading.AREA)));
                        //bundle.putInt("Route_no", Integer.parseInt(c.getString(c.getColumnIndex(DbConnection.ROUTE_NO))));

                        bundle.putString("C_name", c.getString(c.getColumnIndex(DbHTCReading.NAME)));
                        bundle.putString("KW_CM",c.getString(c.getColumnIndex(DbHTCReading.KW_CM)));
                        bundle.putString("KVA_CM",c.getString(c.getColumnIndex(DbHTCReading.KVA_CM)));
                        bundle.putString("KWH_CM",c.getString(c.getColumnIndex(DbHTCReading.KWH_CM)));
                        bundle.putString("KVAH_CM",c.getString(c.getColumnIndex(DbHTCReading.KVAH_CM)));
                        bundle.putString("KVARH_CM",c.getString(c.getColumnIndex(DbHTCReading.KVARH_CM)));
                        bundle.putString("STATUS_CM",c.getString(c.getColumnIndex(DbHTCReading.STATUS_CM)));
                        bundle.putString("TYPE",c.getString(c.getColumnIndex(DbHTCReading.METER_TYPE_CM)));
                        bundle.putString("REMARKS",c.getString(c.getColumnIndex(DbHTCReading.REMARKS_CM)));
                        bundle.putString("REMARK1",c.getString(c.getColumnIndex(DbHTCReading.REMARK1_CM)));


                        // bundle.putString("Address",c.getString(c.getColumnIndex(DbConnection.ADDRESS)));
                        // bundle.putString("Mobile_no",c.getString(c.getColumnIndex(DbConnection.CONTACT_NO)));
                        //bundle.putString("Feeder_name",c.getString(c.getColumnIndex(DbConnection.FEEDERNAME)));


                        Intent intent = new Intent(HtcActivityStatus.this, HTCActivityMMReadingFromHTCStatus.class);
                        intent.putExtras(bundle);
                        // dismiss();
                        startActivity(intent);
                        finish();
                        sqLiteDatabase1.close();
                    } else {
                        Toast.makeText(HtcActivityStatus.this, "Consumer code either not valid or not get downloaded.", Toast.LENGTH_SHORT).show();
                        //et_Connection.setText("");
                        //sqLiteDatabase.close();
                        // break;
                    }
                    //MyActivity activity=new MyActivity();
                    //activity.cosumerSearchValidate();
                }

                if (groupP == 3) {
                    //  Toast.makeText(getApplicationContext(),"GroupPosition:..... \t"+String.valueOf(groupP)+"\nChildPosition:\t"+String.valueOf(childP),Toast.LENGTH_LONG).show();
                    PojoDownload pD = arrayList_no_of_LT_Lock.get(childP);
                    int htc_no = pD.getHtc_no();//lock Sc_no/connection Code get retrieved
                    //  Toast.makeText(getApplicationContext(),"SC:..... \t"+String.valueOf(s_Sc_no),Toast.LENGTH_LONG).show();
                    //Intent launch1=new Intent(MyStatus.this,MyReading.class);
                    //launch1.putExtra("C_code",s_Sc_no);
                    //startActivity(launch1);


                    SQLiteDatabase sqLiteDatabase1 = dbHTCReading.getReadableDatabase();
                    Cursor c = dbHTCReading.getMasterDetailForConnectionID(htc_no, sqLiteDatabase1);
                    if (c.getCount() > 0) {
                        //Toast.makeText(getActivity(),"Search successful.",Toast.LENGTH_SHORT).show();

                        c.moveToFirst();
                        Bundle bundle = new Bundle();
                        bundle.putInt("C_Id", htc_no);
                        bundle.putInt("C_code", Integer.parseInt(c.getString(c.getColumnIndex(DbHTCReading.HTC_NO))));
                        //bundle.putString("Meter_no", c.getString(c.getColumnIndex(DbConnection.METER_NO)));
                        bundle.putString("Area_code", c.getString(c.getColumnIndex(DbHTCReading.AREA)));
                        //bundle.putInt("Route_no", Integer.parseInt(c.getString(c.getColumnIndex(DbConnection.ROUTE_NO))));

                        bundle.putString("C_name", c.getString(c.getColumnIndex(DbHTCReading.NAME)));
                        bundle.putString("KW_MM",c.getString(c.getColumnIndex(DbHTCReading.KW_MM)));
                        bundle.putString("KVA_MM",c.getString(c.getColumnIndex(DbHTCReading.KVA_MM)));
                        bundle.putString("KWH_MM",c.getString(c.getColumnIndex(DbHTCReading.KWH_MM)));
                        bundle.putString("KVAH_MM",c.getString(c.getColumnIndex(DbHTCReading.KVAH_MM)));
                        bundle.putString("KVARH_MM",c.getString(c.getColumnIndex(DbHTCReading.KVARH_MM)));
                        bundle.putString("ZONE1_MM",c.getString(c.getColumnIndex(DbHTCReading.ZONE_1_MM)));
                        bundle.putString("ZONE2_MM",c.getString(c.getColumnIndex(DbHTCReading.ZONE_2_MM)));
                        bundle.putString("ZONE3_MM",c.getString(c.getColumnIndex(DbHTCReading.ZONE_3_MM)));
                        bundle.putString("STATUS_MM",c.getString(c.getColumnIndex(DbHTCReading.STATUS_MM)));
                        bundle.putString("TYPE_MM",c.getString(c.getColumnIndex(DbHTCReading.METER_TYPE_MM)));
                        bundle.putString("REMARKS_MM",c.getString(c.getColumnIndex(DbHTCReading.REMARKS_MM)));
                        bundle.putString("REMARK1_MM",c.getString(c.getColumnIndex(DbHTCReading.REMARK1_MM)));


                        // bundle.putString("Address",c.getString(c.getColumnIndex(DbConnection.ADDRESS)));
                        // bundle.putString("Mobile_no",c.getString(c.getColumnIndex(DbConnection.CONTACT_NO)));
                        //bundle.putString("Feeder_name",c.getString(c.getColumnIndex(DbConnection.FEEDERNAME)));


                        Intent intent = new Intent(HtcActivityStatus.this, HTCActivityCMReadingFromHTCStatus.class);
                        intent.putExtras(bundle);
                        // dismiss();
                        startActivity(intent);
                        finish();
                        sqLiteDatabase1.close();
                    } else {
                        Toast.makeText(HtcActivityStatus.this, "Consumer code either not valid or not get downloaded.", Toast.LENGTH_SHORT).show();
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

    public void prepareListData(){

        List<String> area_code=new ArrayList<String>();
        arrayList_myDownload=new ArrayList<PojoDownload>();
        arrayList_myReading=new ArrayList<PojoDownload>();
        arrayList_no_of_HT_Lock=new ArrayList<PojoDownload>();
        arrayList_no_of_LT_Lock=new ArrayList<PojoDownload>();

        listDataHeader=new ArrayList<String>();
        listDataChild=new HashMap<String, ArrayList<PojoDownload>>();

        listDataHeader.add("My Downloads");
        listDataHeader.add("Meter Reading");
        listDataHeader.add("Incomplete Main Reading");
        listDataHeader.add("Incomplete Check Reading");

        //listDataHeader.add("Coming Soon..");


        Cursor cursor=dbHtcDownloadStatus.getAll();
        Log.d("getAll method cursor size", String.valueOf(cursor.getCount()));

        if(cursor.getCount() > 0){

            download_data=true;
            cursor.moveToFirst();
            do{
                PojoDownload pojoDownload=new PojoDownload();
                String area_cod=cursor.getString(cursor.getColumnIndex(DbHtcDownloadStatus.AREA));
                area_code.add(area_cod);
                pojoDownload.setArea_code(area_cod);
                pojoDownload.setConumers(cursor.getInt(cursor.getColumnIndex(DbHtcDownloadStatus.NO_OF_CONSUMERS)));

                String date=cursor.getString(cursor.getColumnIndex(DbHtcDownloadStatus.RECORD_CREATION_DATE));
                String day=date.substring(0,2);
                String month=date.substring(2,4);
                String year=date.substring(4,8);

                pojoDownload.setDate(day+"/"+month+"/"+year);

                arrayList_myDownload.add(pojoDownload);



            }while(cursor.moveToNext());
            listDataChild.put(listDataHeader.get(0),arrayList_myDownload);
            liteDatabase1.close();
        }else {
            download_data=false;
            liteDatabase1.close();
            //Toast.makeText(MyStatus.this,"").show();
        }
        //ArrayList<PojoDownload> arrayList1=new ArrayList<PojoDownload>();
        if(area_code.size() > 0){

            for (int i=0;i < area_code.size();i++){
                Log.d("area_code size",String.valueOf(area_code.size()));
                String b=area_code.get(i);
                int meter_reading_not_completed=0;
                meter_reading_not_completed=dbHTCReading.getNotYetReadingCompleted1(b.toString())-dbHTCReading.getNotYetReadingCompleted(b.toString());
                int no_of_Main_LOCK_status=0,no_of_Check_LOCK_status=0;
                int no_of_Main_NO_METER=0,no_of_Check_NO_METER=0;
                int no_of_Main_POWER_OFF=0,no_of_Check_POWER_OFF=0;
                int no_of_Main_NO_DISPLAY=0,no_of_Check_NO_DISPLAY=0;

                no_of_Main_LOCK_status=dbHTCReading.getNoOfMainLockStatus(b.toString());
                no_of_Main_NO_METER=dbHTCReading.getNoOfMainNoMeter(b.toString());
                no_of_Main_POWER_OFF=dbHTCReading.getNoOfMainPowerOff(b.toString());
                no_of_Main_NO_DISPLAY=dbHTCReading.getNoOfMainNoDisplay(b.toString());

                no_of_Check_LOCK_status=dbHTCReading.getNoOfCheckLockStatus(b.toString());
                no_of_Check_NO_METER=dbHTCReading.getNoOfCheckNoMeter(b.toString());
                no_of_Check_POWER_OFF=dbHTCReading.getNoOfCheckPowerOff(b.toString());
                no_of_Check_NO_DISPLAY=dbHTCReading.getNoOfCheckNoDisplay(b.toString());

                Cursor cursor_no_of_Main_Lock_stat=dbHTCReading.getDetailsOfMainLockReading(b.toString());
                Cursor cursor_no_of_Main_NO_METER=dbHTCReading.getDetailsOfMain_NO_METER_Reading(b.toString());
                Cursor cursor_no_of_Main_POWER_OFF=dbHTCReading.getDetailsOfMain_POWER_OFF_Reading(b.toString());
                Cursor cursor_no_of_Main_NO_DISPLAY=dbHTCReading.getDetailsOfMainNoDisplayReading(b.toString());

                Cursor cursor_no_of_Check_Lock_stat=dbHTCReading.getDetailsOfCheckLockReading(b.toString());
                Cursor cursor_no_of_Check_NO_METER=dbHTCReading.getDetailsOfCheck_NO_METER_Reading(b.toString());
                Cursor cursor_no_of_Check_POWER_OFF=dbHTCReading.getDetailsOfCheck_POWER_OFF_Reading(b.toString());
                Cursor cursor_no_of_Check_NO_DISPLAY=dbHTCReading.getDetailsOfCheckNoDisplayReading(b.toString());

                //PojoDownload pojoDownload_Lock= new PojoDownload();

                if (cursor_no_of_Main_Lock_stat.getCount() > 0){
                    cursor_no_of_Main_Lock_stat.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Main_Lock_stat.getInt(cursor_no_of_Main_Lock_stat.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_HT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Main_Lock_stat.moveToNext());
                }

                if (cursor_no_of_Main_NO_METER.getCount() > 0){
                    cursor_no_of_Main_NO_METER.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Main_NO_METER.getInt(cursor_no_of_Main_NO_METER.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_HT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Main_NO_METER.moveToNext());
                }
                if (cursor_no_of_Main_POWER_OFF.getCount() > 0){
                    cursor_no_of_Main_POWER_OFF.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Main_POWER_OFF.getInt(cursor_no_of_Main_POWER_OFF.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_HT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Main_POWER_OFF.moveToNext());
                }
                if (cursor_no_of_Main_NO_DISPLAY.getCount() > 0){
                    cursor_no_of_Main_NO_DISPLAY.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Main_NO_DISPLAY.getInt(cursor_no_of_Main_NO_DISPLAY.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_HT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Main_NO_DISPLAY.moveToNext());
                }

                if (cursor_no_of_Check_Lock_stat.getCount() > 0){
                    cursor_no_of_Check_Lock_stat.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Check_Lock_stat.getInt(cursor_no_of_Check_Lock_stat.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_LT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Check_Lock_stat.moveToNext());
                }

                if (cursor_no_of_Check_NO_METER.getCount() > 0){
                    cursor_no_of_Check_NO_METER.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Check_NO_METER.getInt(cursor_no_of_Check_NO_METER.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_LT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Check_NO_METER.moveToNext());
                }
                if (cursor_no_of_Check_POWER_OFF.getCount() > 0){
                    cursor_no_of_Check_POWER_OFF.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Check_POWER_OFF.getInt(cursor_no_of_Check_POWER_OFF.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_LT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Check_POWER_OFF.moveToNext());
                }
                if (cursor_no_of_Check_NO_DISPLAY.getCount() > 0){
                    cursor_no_of_Check_NO_DISPLAY.moveToFirst();
                    do{
                        PojoDownload pojoDownload_Lock= new PojoDownload();
                        pojoDownload_Lock.setArea_code(b.toString());
                        pojoDownload_Lock.setHtc_no(cursor_no_of_Check_NO_DISPLAY.getInt(cursor_no_of_Check_NO_DISPLAY.getColumnIndex(DbHTCReading.HTC_NO)));

                        arrayList_no_of_LT_Lock.add(pojoDownload_Lock);
                    }while (cursor_no_of_Check_NO_DISPLAY.moveToNext());
                }



                Log.d("b:",b);
                //Toast.makeText(MyStatus.this,"Downlaoaded book_no :"+b,Toast.LENGTH_SHORT).show();
                Cursor newCur=dbHTCReading.getSuccussfulReading(b);
                Cursor cursor_for_remaining=dbHtcDownloadStatus.getAll();
                int no_of_consumers_for_b=0;
                cursor_for_remaining.moveToFirst();
                do{
                    String area=cursor_for_remaining.getString(cursor_for_remaining.getColumnIndex(DbHtcDownloadStatus.AREA));
                    if(area.equals(b)){
                        no_of_consumers_for_b=cursor_for_remaining.getInt(cursor_for_remaining.getColumnIndex(DbHtcDownloadStatus.NO_OF_CONSUMERS));
                        break;
                    }
                }while(cursor_for_remaining.moveToNext());
                //Toast.makeText(MyStatus.this,"Meter Reading done for "+String.valueOf(newCur.getCount()),Toast.LENGTH_SHORT).show();
                Integer no=newCur.getCount();
                if(no > 0){

                    int no_of_con_meter_Reading_Success=0;
                    newCur.moveToFirst();
                    do{
                        String KW_MM=null,KWH_CM=null;
                        KW_MM=newCur.getString(newCur.getColumnIndex(DbHTCReading.KW_MM));
                        KWH_CM=newCur.getString(newCur.getColumnIndex(DbHTCReading.KWH_CM));

                        if((KW_MM != null) && (KWH_CM != null)){
                            ++no_of_con_meter_Reading_Success;
                            // newCur.close();
                            //sqLiteDatabase.close();

                        }else{
                            Log.d("LogMessage:", b+": either KW_MM or KWH_CM is not available or both");
                            //newCur.close();
                            //sqLiteDatabase.close();
                        }

                    }while (newCur.moveToNext());

                    PojoDownload pojoDownload1=new PojoDownload();
                    pojoDownload1.setArea_code(b);
                    pojoDownload1.setConumers(no_of_con_meter_Reading_Success);

                    pojoDownload1.setNo_of_met_read_nc(no_of_consumers_for_b-no_of_con_meter_Reading_Success);

                    pojoDownload1.setNo_of_Main_Lock_status(no_of_Main_LOCK_status);
                    pojoDownload1.setNo_of_Check_Lock_status(no_of_Check_LOCK_status);
                    pojoDownload1.setNo_of_Main_NO_Meter(no_of_Main_NO_METER);
                    pojoDownload1.setNo_of_Check_NO_Meter(no_of_Check_NO_METER);
                    pojoDownload1.setNo_of_Main_Power_Off(no_of_Main_POWER_OFF);
                    pojoDownload1.setNo_of_Check_Power_Off(no_of_Check_POWER_OFF);
                    pojoDownload1.setNo_of_Main_NO_DISPLAY(no_of_Main_NO_DISPLAY);
                    pojoDownload1.setNo_of_Check_NO_DISPLAY(no_of_Check_NO_DISPLAY);


                    arrayList_myReading.add(pojoDownload1);

                    newCur.close();
                    sqLiteDatabase.close();

                }else{
                    newCur.close();
                    sqLiteDatabase.close();
                }
                //newCur.close();
                // sqLiteDatabase.close();
                //newCur.close();
            }
        }

        listDataChild.put(listDataHeader.get(0),arrayList_myDownload);
        listDataChild.put(listDataHeader.get(1),arrayList_myReading);
        listDataChild.put(listDataHeader.get(2),arrayList_no_of_HT_Lock);
        listDataChild.put(listDataHeader.get(3),arrayList_no_of_LT_Lock);


    }
}
