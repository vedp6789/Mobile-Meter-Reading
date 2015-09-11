package com.general.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fragments.FDialogDownload;
import com.fragments.HTC_Fragment.FrDiHtcDownload;
import com.general.vvvv.cmr.MyReading;
import com.general.vvvv.cmr.htc.HTCActivityCMReadingFromHTCStatus;
import com.general.vvvv.cmr.htc.HTCActivityMMReadingFromHTCStatus;
import com.general.vvvv.cmr.htc.HtcActivityReading;
import com.general.vvvv.cmr.htc.HtcActivityReadingForConsumerList;

/**
 * Created by comp on 14/01/2015.
 */
public class DbHTCReading extends SQLiteOpenHelper {
    public static String DB_NAME="HTC_Reading";//DATABASE NAME
    public static int DB_VERSION=14;

    static String TB_NAME="HTC_Meter_Reading";//TABLE NAME

    //-----------------------------HTC_Meter_Reading TABLE FIELDS--------------------------------
    public static String S_NO="_id";
    public static String AREA="AREA";
    public static String HTC_NO="HTC_NO";
    public static String NAME="NAME";
    public static String REMARKS_MM="REMARKS_MM";
    public static String STATUS_MM="STATUS_MM";
    public static String METER_TYPE_MM="METER_TYPE_MM";
    public static String REMARKS_CM="REMARKS_CM";
    public static String STATUS_CM="STATUS_CM";
    public static String METER_TYPE_CM="METER_TYPE_CM";
    public static String DATE="DATE";
    public static String REMARK1_MM="REMARK1_MM";
    public static String REMARK1_CM="REMARK1_CM";
    public static String FEEDER_LOCATION="FEEDER_LOCATION";
    public static String USER="USER";
    public static String IMAGE="IMAGE";
    public static String KW_MM="KW_MM";// MM stands for Main Meter
    public static String KVA_MM="KVA_MM";// MM stands for Main Meter
    public static String KWH_MM="KWH_MM";// MM stands for Main Meter
    public static String KVAH_MM="KVAH_MM";// MM stands for Main Meter
    public static String KVARH_MM="KVARH_MM";// MM stands for Main Meter
    public static String ZONE_1_MM="ZONE_1_MM";// MM stands for Main Meter
    public static String ZONE_2_MM="ZONE_2_MM";// MM stands for Main Meter
    public static String ZONE_3_MM="ZONE_3_MM";// MM stands for Main Meter
    public static String KW_CM="KW_CM";//CM stands for Check Meter
    public static String KVA_CM="KVA_CM";//CM stands for Check Meter
    public static String KWH_CM="KWH_CM";//CM stands for Check Meter
    public static String KVAH_CM="KVAH_CM";//CM stands for Check Meter
    public static String KVARH_CM="KVARH_CM";//CM stands for Check Meter
    //--------------------------------------------------------------------------------------------------------

    String create_query="CREATE TABLE "+TB_NAME+" ( "+S_NO+" INTEGER PRIMARY KEY AUTOINCREMENT, "+AREA+" TEXT, "+HTC_NO+" INTEGER, "+NAME+" TEXT, "+KW_MM+" TEXT, "+KW_CM+" TEXT, "+KVA_MM+" TEXT, "+KVA_CM+" TEXT, "+KWH_MM+" TEXT, "+KWH_CM+" TEXT, "+KVAH_MM+" TEXT, "+KVAH_CM+" TEXT, "+KVARH_MM+" TEXT, "+KVARH_CM+" TEXT, "+ZONE_1_MM+" TEXT, "+ZONE_2_MM+" TEXT, "+ZONE_3_MM+" TEXT, "+REMARKS_MM+" TEXT, "+REMARKS_CM+" TEXT, "+STATUS_MM+" TEXT, "+STATUS_CM+" TEXT, "+METER_TYPE_MM+" TEXT, "+METER_TYPE_CM+" TEXT, "+REMARK1_MM+" TEXT, "+REMARK1_CM+" TEXT, "+DATE+" TEXT, "+FEEDER_LOCATION+" TEXT, " +USER+" TEXT, " +IMAGE+" BLOB )";

    public DbHTCReading(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(create_query);
        Log.i(TB_NAME + "  table created", "Successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TB_NAME);
        onCreate(sqLiteDatabase);
    }

    public Boolean insertMasterHtcData(ContentValues values){
        boolean b=false;
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        long l=sqLiteDatabase.insert(TB_NAME,null,values);
        if(l>0){
            b=true;
            FrDiHtcDownload.db_update=true;
        }

        sqLiteDatabase.close();
        return b;
    }
    public Boolean deleteDownloadedData(String area){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL("DELETE FROM "+TB_NAME+ " WHERE "+ AREA +" = "+" '"+area+"' ");
        database.close();
        return true;
    }

    public Cursor myDownloadDeleteHelper(String area){
        SQLiteDatabase liteDatabase=this.getWritableDatabase();
        //Cursor c=liteDatabase.query(TB_NAME,new String[]{BOOK_NO},BOOK_NO +" = "+route_no,null,null,null,null);
        String rq="SELECT AREA FROM "+TB_NAME+" WHERE "+AREA+" = "+"'"+area+"'";
        Cursor c=liteDatabase.rawQuery(rq,null);

        return c;

    }

    public Cursor getCountOfConsumers(String s){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" = "+"'"+s+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        return cursor;

    }

    public Boolean consumerIdValidation(int htc_no,SQLiteDatabase sqLiteDatabase) {
        boolean b = false;

        String rq = "SELECT * FROM " + TB_NAME + " WHERE " + HTC_NO + " = " + htc_no;
        Cursor cursor = sqLiteDatabase.rawQuery(rq, null);
        if (cursor.getCount() > 0) {
            b = true;
        }
        sqLiteDatabase.close();
        return b;
    }

    public String[] getConsumerMasterData(int consumer_id){

        SQLiteDatabase sqLiteDatabase= getReadableDatabase();
        String rq="SELECT "+AREA+" , "+NAME+" FROM "+TB_NAME+" WHERE "+HTC_NO+" = "+consumer_id;
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        cursor.moveToFirst();
        String area=cursor.getString(cursor.getColumnIndex(DbHTCReading.AREA));
        String name=cursor.getString(cursor.getColumnIndex(DbHTCReading.NAME));
        String [] data={area,name};
        return  data;
    }

    public Boolean updateReadingDetails(ContentValues contentValues,int consumer_id){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();

        long l=liteDatabase.update(TB_NAME,contentValues,HTC_NO+" == "+consumer_id ,null);
        if(l>0){
            HtcActivityReading.readingUpdate=true;
            b=true;
        }
        liteDatabase.close();
        return b;
    }

    public Boolean updateReadingDetails1(ContentValues contentValues,int consumer_id){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();

        long l=liteDatabase.update(TB_NAME,contentValues,HTC_NO+" == "+consumer_id ,null);
        if(l>0){
            HtcActivityReadingForConsumerList.readingUpdate=true;
            b=true;
        }
        liteDatabase.close();
        return b;
    }
    public Boolean updateReadingDetails2(ContentValues contentValues,int consumer_id){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();

        long l=liteDatabase.update(TB_NAME,contentValues,HTC_NO+" == "+consumer_id ,null);
        if(l>0){
            HTCActivityMMReadingFromHTCStatus.readingUpdate=true;
            b=true;
        }
        liteDatabase.close();
        return b;
    }
    public Boolean updateReadingDetails3(ContentValues contentValues,int consumer_id){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();

        long l=liteDatabase.update(TB_NAME,contentValues,HTC_NO+" == "+consumer_id ,null);
        if(l>0){
            HTCActivityCMReadingFromHTCStatus.readingUpdate=true;
            b=true;
        }
        liteDatabase.close();
        return b;
    }

    public Cursor retriveDataByArea(String area_code){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" = "+"'"+area_code+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }

    public Cursor retriveDataByAreaCode(String area_code){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" = "+"'"+area_code+"'"+" AND "+STATUS_MM +" IS NOT NULL";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }

    public Integer getNotYetReadingCompleted(String area_code){
        int c=0;
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+KW_MM +" IS NULL"+" AND "+KWH_CM+" IS NULL";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return c;
    }
    public Integer getNotYetReadingCompleted1(String area_code){
        int c=0;
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+KW_MM +" IS NULL"+" OR "+KWH_CM+" IS NULL";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return c;
    }

    public int getNoOfMainLockStatus(String area){
        int i=0;
        String a="LOCK";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();

        return i;
    }
    public int getNoOfMainNoMeter(String area){
        int i=0;
        String a="NO METER";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();

        return i;
    }
    public int getNoOfMainPowerOff(String area){
        int i=0;
        String a="POWER OFF";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();
        return i;
    }
    public int getNoOfMainNoDisplay(String area){
        int i=0;
        String a="NO DISPLAY";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();
        return i;
    }

    public int getNoOfCheckLockStatus(String area){
        int i=0;
        String a="LOCK";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();

        return i;
    }
    public int getNoOfCheckNoMeter(String area){
        int i=0;
        String a="NO METER";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();

        return i;
    }
    public int getNoOfCheckPowerOff(String area){
        int i=0;
        String a="POWER OFF";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();
        return i;
    }
    public int getNoOfCheckNoDisplay(String area){
        int i=0;
        String a="NO DISPLAY";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        i=cursor.getCount();
        sqLiteDatabase.close();
        return i;
    }

    public Cursor getDetailsOfMainLockReading(String area_code){
        Cursor cursor;
        String a="LOCK";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfMain_NO_METER_Reading(String area_code){
        Cursor cursor;
        String a="NO METER";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfMain_POWER_OFF_Reading(String area_code){
        Cursor cursor;
        String a="POWER OFF";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfMainNoDisplayReading(String area_code){
        Cursor cursor;
        String a="NO DISPLAY";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_MM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfCheckLockReading(String area_code){
        Cursor cursor;
        String a="LOCK";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfCheck_NO_METER_Reading(String area_code){
        Cursor cursor;
        String a="NO METER";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfCheck_POWER_OFF_Reading(String area_code){
        Cursor cursor;
        String a="POWER OFF";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfCheckNoDisplayReading(String area_code){
        Cursor cursor;
        String a="NO DISPLAY";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+HTC_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS_CM+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }

    public Cursor getSuccussfulReading(String area_code){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+HTC_NO+" , "+KW_MM+" , "+KWH_CM+" , "+NAME+" FROM "+TB_NAME+" WHERE "+ AREA+" == "+"'"+area_code+"'";//+" AND "+METER_READING +" IS NOT NULL";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }

    public Cursor getMasterDetailForConnectionID(int htc_no,SQLiteDatabase sqLiteDatabase){
        Cursor c;
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+HTC_NO+" == "+htc_no;
        c=sqLiteDatabase.rawQuery(rq,null);
        return c;
    }

    public Boolean areaValidation(String area,SQLiteDatabase sqLiteDatabase){
        boolean b=false;
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'";
        Cursor c=sqLiteDatabase.rawQuery(rq,null);
        if(c.getCount() > 0){
            b=true;
        }
        c.close();
        sqLiteDatabase.close();
        return b;
    }

    public Boolean noOfConForReading(String area){
        boolean b=false;
        Log.d("check 2","222222222222");
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT  * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+ " AND "+STATUS_MM+" IS NULL"+" AND "+STATUS_CM+" IS NULL";
        Cursor c=sqLiteDatabase.rawQuery(rq,null);
        if(c.getCount() > 0){
            b=true;
        }
        c.close();
        sqLiteDatabase.close();
        return b;
    }
    public Cursor getListOfConsumers(String area_code,SQLiteDatabase sqLiteDatabase){
        Log.d("check 1","1111111111111111");
        String rq="SELECT  * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+ " AND "+STATUS_MM+" IS NULL"+" AND "+STATUS_CM+" IS NULL";
        Cursor c=sqLiteDatabase.rawQuery(rq,null);
        return c;
    }

}
