package com.general.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fragments.FDialogDownload;
import com.fragments.LTP_Fragment.FrDiLtpDownload;
import com.general.vvvv.cmr.htc.HtcActivityReading;
import com.general.vvvv.cmr.ltp.LtpActivityReading;
import com.general.vvvv.cmr.ltp.LtpActivityReadingForConsumerList;
import com.general.vvvv.cmr.ltp.LtpActivityReadingFromLtpStatus;

/**
 * Created by comp on 14/01/2015.
 */
public class DbLTPReading extends SQLiteOpenHelper{
    public static String DB_NAME="LTP_Reading";//DATABASE NAME
    public static int DB_VERSION=2;

    static String TB_NAME="LTP_Meter_Reading";//TABLE NAME

    //-----------------------------HTC_Meter_Reading TABLE FIELDS--------------------------------
    public static String S_NO="_id";
    public static String AREA="AREA";
    public static String LTP_NO="LTP_NO";
    public static String NAME="NAME";
    public static String CGL_NO="CGL_NO";
    public static String REMARKS="REMARKS";
    public static String STATUS="STATUS";
    public static String METER_TYPE="METER_TYPE";
    public static String DATE="DATE";
    public static String REMARK1="REMARK1";
    public static String FEEDER_LOCATION="FEEDER_LOCATION";
    public static String USER="USER";
    public static String IMAGE="IMAGE";
    public static String KW="KW";
    public static String KVA="KVA";
    public static String KWH="KWH";
    public static String KVAH="KVAH";
    public static String KVARH="KVARH";
    public static String ZONE_1="ZONE_1";
    public static String ZONE_2="ZONE_2";
    public static String ZONE_3="ZONE_3";
    //--------------------------------------------------------------------------------------------------------

    String create_query="CREATE TABLE "+TB_NAME+" ( "+S_NO+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CGL_NO+" TEXT, "+AREA+" TEXT, "+LTP_NO+" INTEGER, "+NAME+" TEXT, "+KW+" TEXT, "+KVA+" TEXT, "+KWH+" TEXT, "+KVAH+" TEXT, "+KVARH+" TEXT, "+ZONE_1+" TEXT, "+ZONE_2+" TEXT, "+ZONE_3+" TEXT, "+REMARKS+" TEXT, " +STATUS+" TEXT, "+METER_TYPE+" TEXT, " +REMARK1+" TEXT, "+DATE+" TEXT, "+FEEDER_LOCATION+" TEXT, " +USER+" TEXT, " +IMAGE+" BLOB )";

    public DbLTPReading(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
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

    public Boolean insertMasterLtpData(ContentValues values){
        boolean b=false;
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        long l=sqLiteDatabase.insert(TB_NAME,null,values);
        if(l>0){
            b=true;
            FrDiLtpDownload.db_update=true;
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
        String rq="SELECT "+LTP_NO+" FROM "+TB_NAME+" WHERE "+AREA+" = "+"'"+s+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        return cursor;

    }

    public Boolean consumerIdValidation(int ltp_no,SQLiteDatabase sqLiteDatabase) {
        boolean b = false;

        String rq = "SELECT * FROM " + TB_NAME + " WHERE " + LTP_NO + " = " + ltp_no;
        Cursor cursor = sqLiteDatabase.rawQuery(rq, null);
        if (cursor.getCount() > 0) {
            b = true;
        }
        sqLiteDatabase.close();
        return b;
    }
    public String[] getConsumerMasterData(int consumer_id){
        Log.i("consumer id:"+consumer_id,"from DbLtpReading getConsumerMasterData");
        SQLiteDatabase sqLiteDatabase= getReadableDatabase();
        String rq="SELECT "+AREA+" , "+NAME+" , "+CGL_NO+" FROM "+TB_NAME+" WHERE "+LTP_NO+" = "+consumer_id;
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        Log.i("count:"+cursor.getCount(),"from DbLtpReading getConsumerMasterData");
        cursor.moveToFirst();
        String area=cursor.getString(cursor.getColumnIndex(DbLTPReading.AREA));
        String name=cursor.getString(cursor.getColumnIndex(DbLTPReading.NAME));
        String cgl=cursor.getString(cursor.getColumnIndex(DbLTPReading.CGL_NO));
        String [] data={area,name,cgl};
        return  data;
    }
    public Boolean updateReadingDetails(ContentValues contentValues,int consumer_id){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();

        long l=liteDatabase.update(TB_NAME,contentValues,LTP_NO+" == "+consumer_id ,null);
        if(l>0){
            LtpActivityReading.readingUpdate=true;
            b=true;
        }
        liteDatabase.close();
        return b;
    }
    public Boolean updateReadingDetails1(ContentValues contentValues,int consumer_id){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();

        long l=liteDatabase.update(TB_NAME,contentValues,LTP_NO+" == "+consumer_id ,null);
        if(l>0){
            LtpActivityReadingForConsumerList.readingUpdate=true;
            b=true;
        }
        liteDatabase.close();
        return b;
    }


    public Boolean updateReadingDetails2(ContentValues contentValues,int consumer_id){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();

        long l=liteDatabase.update(TB_NAME,contentValues,LTP_NO+" == "+consumer_id ,null);
        if(l>0){
            LtpActivityReadingFromLtpStatus.readingUpdate=true;
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
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" = "+"'"+area_code+"'"+" AND "+STATUS +" IS NOT NULL";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }

    public Integer getNotYetReadingCompleted(String area_code){
        int c=0;
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+KW +" IS NULL";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return c;
    }

    public Integer getNoOfLockStatus(String area_code){
        int c=0;
        String a="LOCK";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return c;
    }

    public Integer getNoOfNoMeter(String area_code){
        int c=0;
        String a="NO METER";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return  c;

    }
    public Integer getNoOfNoPowerOff(String area_code){
        int c=0;
        String a="POWER OFF";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return  c;

    }
    public Integer getNoOfNoDisplay(String area_code){
        int c=0;
        String a="NO DISPLAY";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return  c;

    }

    public Cursor getDetailsOfLockReading(String area_code){
        Cursor cursor;
        String a="LOCK";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+LTP_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfNO_METER_Reading(String area_code){
        Cursor cursor;
        String a="NO METER";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+LTP_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfPOWER_OFF_Reading(String area_code){
        Cursor cursor;
        String a="POWER OFF";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+LTP_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfNoDisplayReading(String area_code){
        Cursor cursor;
        String a="NO DISPLAY";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+AREA+" , "+LTP_NO+" FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }

    public Cursor getSuccussfulReading(String area_code){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+LTP_NO+" , "+KW+" , "+NAME+" FROM "+TB_NAME+" WHERE "+ AREA+" == "+"'"+area_code+"'";//+" AND "+METER_READING +" IS NOT NULL";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }

    public Cursor getMasterDetailForConnectionID(int ltp_no,SQLiteDatabase sqLiteDatabase){
        Cursor c;
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+LTP_NO+" == "+ltp_no;
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
        String rq="SELECT  * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area+"'"+ " AND "+STATUS+" IS NULL";
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
        String rq="SELECT  * FROM "+TB_NAME+" WHERE "+AREA+" == "+"'"+area_code+"'"+ " AND "+STATUS+" IS NULL";
        Cursor c=sqLiteDatabase.rawQuery(rq,null);
        return c;
    }
}
