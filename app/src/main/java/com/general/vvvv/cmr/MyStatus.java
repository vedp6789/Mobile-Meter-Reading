package com.general.vvvv.cmr;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.general.SQLite.DbConnection;
import com.general.SQLite.DownloadStatusDb;
import com.general.adapter.ExpandableAdapter;
import com.general.adapter.PojoDownload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by comp on 12/09/2014.
 */
public class MyStatus extends ActionBarActivity {
    ExpandableAdapter expandableAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String,ArrayList<PojoDownload>> listDataChild;
    static Boolean download_data=false;

    DownloadStatusDb downloadStatusDb;
    SQLiteDatabase liteDatabase1;

    DbConnection dbConnection;
    SQLiteDatabase sqLiteDatabase;

    ArrayList<PojoDownload> arrayList_myDownload=null;
    ArrayList<PojoDownload> arrayList_myReading=null;
    ArrayList<PojoDownload> arrayList_no_of_Lock=null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyDbErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        dbConnection=new DbConnection(MyStatus.this,DbConnection.DB_NAME,null,DbConnection.DB_VERSION,new MyDbErrorHandler());
        sqLiteDatabase=dbConnection.getWritableDatabase();

        downloadStatusDb=new DownloadStatusDb(MyStatus.this,DownloadStatusDb.DB_NAME,null,DownloadStatusDb.DB_VERSION);
        liteDatabase1=downloadStatusDb.getWritableDatabase();

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        prepareListData();

        expandableAdapter=new ExpandableAdapter(this,listDataHeader,listDataChild);
        expandableListView.setAdapter(expandableAdapter);

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
                int groupP=groupPosition;
                int childP=childPosition;
             //  Toast.makeText(getApplicationContext(),"GroupPosition: \t"+String.valueOf(groupP)+"\nChildPosition:\t"+String.valueOf(childP),Toast.LENGTH_LONG).show();

                if(groupP == 2){
                  //  Toast.makeText(getApplicationContext(),"GroupPosition:..... \t"+String.valueOf(groupP)+"\nChildPosition:\t"+String.valueOf(childP),Toast.LENGTH_LONG).show();
                PojoDownload pD = arrayList_no_of_Lock.get(childP);
                   int s_Sc_no= pD.getSc_no();//lock Sc_no/connection Code get retrieved
                  //  Toast.makeText(getApplicationContext(),"SC:..... \t"+String.valueOf(s_Sc_no),Toast.LENGTH_LONG).show();
                    //Intent launch1=new Intent(MyStatus.this,MyReading.class);
                    //launch1.putExtra("C_code",s_Sc_no);
                    //startActivity(launch1);


                    SQLiteDatabase sqLiteDatabase1=dbConnection.getReadableDatabase();
                    Cursor c=dbConnection.getMasterDetailForConnectionID(s_Sc_no,sqLiteDatabase1);
                    if(c.getCount()>0) {
                        //Toast.makeText(getActivity(),"Search successful.",Toast.LENGTH_SHORT).show();

                        c.moveToFirst();
                        Bundle bundle = new Bundle();
                        bundle.putInt("C_code", s_Sc_no);
                        bundle.putInt("C_code", Integer.parseInt(c.getString(c.getColumnIndex(DbConnection.SC_NO))));
                        bundle.putString("Meter_no", c.getString(c.getColumnIndex(DbConnection.METER_NO)));
                        bundle.putString("Book_no", c.getString(c.getColumnIndex(DbConnection.BOOK_NO)));
                        bundle.putInt("Route_no", Integer.parseInt(c.getString(c.getColumnIndex(DbConnection.ROUTE_NO))));
                        bundle.putString("Cgl_no", c.getString(c.getColumnIndex(DbConnection.CGL_NO)));
                        bundle.putString("C_name", c.getString(c.getColumnIndex(DbConnection.NAME_CONSUMER)));
                        // bundle.putString("Address",c.getString(c.getColumnIndex(DbConnection.ADDRESS)));
                        // bundle.putString("Mobile_no",c.getString(c.getColumnIndex(DbConnection.CONTACT_NO)));
                        //bundle.putString("Feeder_name",c.getString(c.getColumnIndex(DbConnection.FEEDERNAME)));


                        Intent intent = new Intent(MyStatus.this, MyReading.class);
                        intent.putExtras(bundle);
                       // dismiss();
                        startActivity(intent);
                        finish();
                        sqLiteDatabase1.close();
                    }else{
                        Toast.makeText(MyStatus.this, "Consumer code either not valid or not get downloaded.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void prepareListData(){

         List<String> book_no=new ArrayList<String>();
         arrayList_myDownload=new ArrayList<PojoDownload>();
        arrayList_myReading=new ArrayList<PojoDownload>();
        arrayList_no_of_Lock=new ArrayList<PojoDownload>();

        listDataHeader=new ArrayList<String>();
        listDataChild=new HashMap<String, ArrayList<PojoDownload>>();

        listDataHeader.add("My Downloads");
        listDataHeader.add("Meter Reading");
        listDataHeader.add("Incomplete Meter Reading");
        //listDataHeader.add("Coming Soon..");


        Cursor cursor=downloadStatusDb.getAll();
        Log.d("getAll method cursor size",String.valueOf(cursor.getCount()));

        if(cursor.getCount() > 0){

            download_data=true;
            cursor.moveToFirst();
            do{
                PojoDownload pojoDownload=new PojoDownload();
                String book_no_d=cursor.getString(cursor.getColumnIndex(DownloadStatusDb.BOOK_NO));
                book_no.add(book_no_d);
                pojoDownload.setBook_name(book_no_d);
                pojoDownload.setConumers(cursor.getInt(cursor.getColumnIndex(DownloadStatusDb.NO_OF_CONSUMERS)));

                String date=cursor.getString(cursor.getColumnIndex(DownloadStatusDb.RECORD_CREATION_DATE));
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
        if(book_no.size() > 0){

           for (int i=0;i < book_no.size();i++){
               Log.d("book_no size",String.valueOf(book_no.size()));
               String b=book_no.get(i);

               int meter_reading_not_completed=0;
               meter_reading_not_completed=dbConnection.getNotYetReadingCompleted(b.toString());
               int no_of_LOCK_status=0;
               no_of_LOCK_status=dbConnection.getNoOfLockStatus(b.toString());
               int no_of_NO_METER=0;
               int no_of_POWER_OFF=0;
               int no_of_NO_DISPLAY=0;
               no_of_NO_METER=dbConnection.getNoOfNoMeter(b.toString());
               no_of_POWER_OFF=dbConnection.getNoOfNoPowerOff(b.toString());
               no_of_NO_DISPLAY=dbConnection.getNoOfNoDisplay(b.toString());
               Cursor cursor_no_of_Lock_stat=dbConnection.getDetailsOfLockReading(b.toString());
               Cursor cursor_no_of_NO_METER=dbConnection.getDetailsOfNO_METER_Reading(b.toString());
               Cursor cursor_no_of_POWER_OFF=dbConnection.getDetailsOfPOWER_OFF_Reading(b.toString());
               Cursor cursor_no_of_NO_DISPLAY=dbConnection.getDetailsOfNoDisplayReading(b.toString());

                              //PojoDownload pojoDownload_Lock= new PojoDownload();

               if (cursor_no_of_Lock_stat.getCount() > 0){
                   cursor_no_of_Lock_stat.moveToFirst();
                   do{
                       PojoDownload pojoDownload_Lock= new PojoDownload();
                       pojoDownload_Lock.setBook_name(b.toString());
                       pojoDownload_Lock.setSc_no(cursor_no_of_Lock_stat.getInt(cursor_no_of_Lock_stat.getColumnIndex(DbConnection.SC_NO)));
                       pojoDownload_Lock.setRoute_no(cursor_no_of_Lock_stat.getInt(cursor_no_of_Lock_stat.getColumnIndex(DbConnection.ROUTE_NO)));
                       arrayList_no_of_Lock.add(pojoDownload_Lock);
                   }while (cursor_no_of_Lock_stat.moveToNext());
               }

               if (cursor_no_of_NO_METER.getCount() > 0){
                   cursor_no_of_NO_METER.moveToFirst();
                   do{
                       PojoDownload pojoDownload_Lock= new PojoDownload();
                       pojoDownload_Lock.setBook_name(b.toString());
                       pojoDownload_Lock.setSc_no(cursor_no_of_NO_METER.getInt(cursor_no_of_NO_METER.getColumnIndex(DbConnection.SC_NO)));
                       pojoDownload_Lock.setRoute_no(cursor_no_of_NO_METER.getInt(cursor_no_of_NO_METER.getColumnIndex(DbConnection.ROUTE_NO)));
                       arrayList_no_of_Lock.add(pojoDownload_Lock);
                   }while (cursor_no_of_NO_METER.moveToNext());
               }
               if (cursor_no_of_POWER_OFF.getCount() > 0){
                   cursor_no_of_POWER_OFF.moveToFirst();
                   do{
                       PojoDownload pojoDownload_Lock= new PojoDownload();
                       pojoDownload_Lock.setBook_name(b.toString());
                       pojoDownload_Lock.setSc_no(cursor_no_of_POWER_OFF.getInt(cursor_no_of_POWER_OFF.getColumnIndex(DbConnection.SC_NO)));
                       pojoDownload_Lock.setRoute_no(cursor_no_of_POWER_OFF.getInt(cursor_no_of_POWER_OFF.getColumnIndex(DbConnection.ROUTE_NO)));
                       arrayList_no_of_Lock.add(pojoDownload_Lock);
                   }while (cursor_no_of_POWER_OFF.moveToNext());
               }
               if (cursor_no_of_NO_DISPLAY.getCount() > 0){
                   cursor_no_of_NO_DISPLAY.moveToFirst();
                   do{
                       PojoDownload pojoDownload_Lock= new PojoDownload();
                       pojoDownload_Lock.setBook_name(b.toString());
                       pojoDownload_Lock.setSc_no(cursor_no_of_NO_DISPLAY.getInt(cursor_no_of_NO_DISPLAY.getColumnIndex(DbConnection.SC_NO)));
                       pojoDownload_Lock.setRoute_no(cursor_no_of_NO_DISPLAY.getInt(cursor_no_of_NO_DISPLAY.getColumnIndex(DbConnection.ROUTE_NO)));
                       arrayList_no_of_Lock.add(pojoDownload_Lock);
                   }while (cursor_no_of_NO_DISPLAY.moveToNext());
               }


               Log.d("b:",b);
               //Toast.makeText(MyStatus.this,"Downlaoaded book_no :"+b,Toast.LENGTH_SHORT).show();
               Cursor newCur=dbConnection.getSuccussfulReading(b);
               //Toast.makeText(MyStatus.this,"Meter Reading done for "+String.valueOf(newCur.getCount()),Toast.LENGTH_SHORT).show();
               Integer no=newCur.getCount();
               if(no > 0){

                   int no_of_con_meter_Reading_Success=0;
                   newCur.moveToFirst();
                   do{
                       String meter_reading=null;
                       meter_reading=newCur.getString(newCur.getColumnIndex(DbConnection.METER_READING));
                       if(meter_reading != null){
                           ++no_of_con_meter_Reading_Success;
                          // newCur.close();
                           //sqLiteDatabase.close();

                       }else{
                           Log.d("LogMessage:", b+": not found any meter reading");
                           //newCur.close();
                           //sqLiteDatabase.close();
                       }

                   }while (newCur.moveToNext());

                   PojoDownload pojoDownload1=new PojoDownload();
                   pojoDownload1.setBook_name(b);
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
        listDataChild.put(listDataHeader.get(2),arrayList_no_of_Lock);


    }
}
