package com.general.SQLite;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.fragments.FDialogDownload;
import com.general.vvvv.cmr.MyReading;
import com.general.vvvv.cmr.MyReadingByBookNo;
import com.general.vvvv.cmr.MyReadingByCGL;

/**
 * Created by vvvv on 24-08-2014.
 */
public class DbConnection extends SQLiteOpenHelper {

    public static String DB_NAME="Daman_Reading";//DATABASE NAME
    public static int DB_VERSION=12;

    static String TB_NAME="Consumer_Meter_Reading";//TABLE NAME

//-----------------------------Consumer_Meter_Reading TABLE FIELDS--------------------------------
    public static String S_NO="S_NO";
    public static String BOOK_NO="BOOK_NO";

    public static String ROUTE_NO="ROUTE_NO";
    public static String CGL_NO="CGL_NO";

    public static String SC_NO="SC_NO";
    //public static String OLD_SC_NO="OLD_SC_NO";
    public static String NAME_CONSUMER="NAME_CONSUMER";
    //public static String ADDRESS="ADDRESS";
    //public static String CONTACT_NO="CONTACT_NO";
    public static String METER_NO="METER_NO";
    //public static String SUBSTATIONNAME="SUBSTATIONNAME";
    //public static String FEEDERNAME="FEEDERNAME";
    public static String METER_READING="METER_READING";
    public static String REMARKS="REMARKS";
    public static String STATUS="STATUS";
    public static String METER_TYPE="METER_TYPE";
    public static String DATE="DATE";
    public static String REMARK1="REMARK1";
    //public static String METER_LOCATION="METER_LOCATION";
    public static String FEEDER_LOCATION="FEEDER_LOCATION";
    public static String USER="USER";
    public static String IMAGE="IMAGE";
//------------------------------------------------------------------------------
    boolean master_detail_insertion=false;
    String createConsumer_Meter_Reading="CREATE TABLE "+TB_NAME+" ( "+S_NO+" INTEGER, "+BOOK_NO+" TEXT, "+ROUTE_NO+" INTEGER, "+CGL_NO+" TEXT, "+SC_NO+" INTEGER, " +NAME_CONSUMER+" TEXT, " +METER_NO+" TEXT, " +METER_READING+" TEXT, " +REMARKS+" TEXT, " +STATUS+" TEXT, "+METER_TYPE+" TEXT, " +REMARK1+" TEXT, "+DATE+" TEXT, "+FEEDER_LOCATION+" TEXT, " +USER+" TEXT, " +IMAGE+" BLOB )";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DbConnection(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DB_NAME, null, DB_VERSION, errorHandler);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createConsumer_Meter_Reading);
    Log.d(TB_NAME+"  table created", "Successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TB_NAME);
        onCreate(sqLiteDatabase);
    }
    public Boolean insertMasterDetail(ContentValues values){
        boolean b=false;
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        long l=sqLiteDatabase.insert(TB_NAME,null,values);
        if(l>0){
            b=true;
            FDialogDownload.db_update=true;
        }

        sqLiteDatabase.close();
        return b;
    }
    public Boolean updateReadingDetails(ContentValues contentValues,int consumer_code){
        Boolean b=false;
        SQLiteDatabase liteDatabase=getWritableDatabase();
        int c_code=consumer_code;
        long l=liteDatabase.update(TB_NAME,contentValues,SC_NO+" == "+c_code ,null);
        if(l>0){
            MyReading.readingUpdate=true;
            b=true;
        }
        liteDatabase.close();
        return b;
    }
    public void updateReadingDetailsByBook(ContentValues contentValues,int consumer_code){
        SQLiteDatabase liteDatabase=getWritableDatabase();
        int c_code=consumer_code;
        long l=liteDatabase.update(TB_NAME,contentValues,SC_NO+" == "+c_code ,null);
        if(l>0){
            MyReadingByBookNo.readingUpdate=true;
        }
        liteDatabase.close();

    }
    public String updateReadingDetailsByCGL(ContentValues contentValues,int consumer_code){
        SQLiteDatabase liteDatabase=getWritableDatabase();
        int c_code=consumer_code;
        long l=liteDatabase.update(TB_NAME,contentValues,SC_NO+" == "+c_code ,null);
        if(l>0){
            MyReadingByCGL.readingUpdate=true;
        }
        liteDatabase.close();
        return "from DbConnection";
    }

   /* public Cursor returnImage(int  consumer_id){
        SQLiteDatabase sqLiteDatabase =  getReadableDatabase();
        String rq="SELECT " +IMAGE+ " FROM "+TB_NAME+" WHERE "+ SC_NO +" = "+consumer_id;
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //Cursor cursor = sqLiteDatabase.query(TB_NAME,new String[]{IMAGE}, SC_NO = consumer_id,null,null,null,null);
        return cursor;
    }*/

    public Cursor myDownloadDeleteHelper(String route_no){
        SQLiteDatabase liteDatabase=this.getWritableDatabase();
        //Cursor c=liteDatabase.query(TB_NAME,new String[]{BOOK_NO},BOOK_NO +" = "+route_no,null,null,null,null);
        String rq="SELECT BOOK_NO FROM "+TB_NAME+" WHERE "+BOOK_NO+" = "+"'"+route_no+"'";
        Cursor c=liteDatabase.rawQuery(rq,null);

        return c;

    }
    public Cursor getMasterDetailForBookNo(String book_no,SQLiteDatabase  sqLiteDatabase){
        Cursor cursor;
        String qs="SELECT "+SC_NO+" , "+BOOK_NO+" , "+ROUTE_NO+" , "+CGL_NO+" , "+METER_NO+" , "+NAME_CONSUMER+" FROM "+TB_NAME+ " WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS +" IS NULL"+ " ORDER BY "+ROUTE_NO+ " ASC";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        //cursor=sqLiteDatabase.query(TB_NAME,new String[]{SC_NO,BOOK_NO,ROUTE_NO,CGL_NO,METER_NO,NAME_CONSUMER},BOOK_NO +"=?"+" AND "+STATUS +"=?",new String[]{book_no,null},null,null,ROUTE_NO);
        return  cursor;
    }
    public Cursor getMasterDetailByCGLNo(String book_no,SQLiteDatabase  sqLiteDatabase){
        Cursor cursor;
        String qs="SELECT "+SC_NO+" , "+BOOK_NO+" , "+ROUTE_NO+" , "+CGL_NO+" , "+METER_NO+" , "+NAME_CONSUMER+" FROM "+TB_NAME+ " WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS +" IS NULL"+ " ORDER BY "+ROUTE_NO+ " ASC";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        //cursor=sqLiteDatabase.query(TB_NAME,new String[]{SC_NO,BOOK_NO,ROUTE_NO,CGL_NO,METER_NO,NAME_CONSUMER},BOOK_NO +"=?"+" AND "+STATUS +"=?",new String[]{book_no,null},null,null,ROUTE_NO);
        return  cursor;
    }
    public Cursor getDetailsOfCGL(String cgl,SQLiteDatabase sqLiteDatabase){
        Cursor cursor;
        String qs="SELECT "+BOOK_NO+" , "+METER_READING+" , "+STATUS+" FROM "+TB_NAME+" WHERE "+CGL_NO+" == "+"'"+cgl+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
       // cursor.moveToFirst();
        return cursor;
    }
    public Cursor getDetailsOfConn(String conn,SQLiteDatabase sqLiteDatabase){
        Cursor cursor;
        String qs="SELECT "+BOOK_NO+" , "+METER_READING+" , "+STATUS+" FROM "+TB_NAME+" WHERE "+SC_NO+" == "+Integer.parseInt(conn);
        cursor=sqLiteDatabase.rawQuery(qs,null);
        // cursor.moveToFirst();
        return cursor;
    }


    public Integer getNotYetReadingCompleted(String book_no){
        int c=0;
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+METER_READING +" IS NULL";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return c;
    }
    public Integer getNoOfNoMeter(String book_no){
        int c=0;
        String a="NO METER";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return  c;

    }
    public Integer getNoOfNoPowerOff(String book_no){
        int c=0;
        String a="POWER OFF";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return  c;

    }
    public Integer getNoOfNoDisplay(String book_no){
        int c=0;
        String a="NO DISPLAY";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return  c;

    }
    public Integer getNoOfLockStatus(String book_no){
        int c=0;
        String a="LOCK";
        String qs="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        c=cursor.getCount();
        sqLiteDatabase.close();
        return c;
    }
    public Cursor getDetailsOfLockReading(String book_no){
        Cursor cursor;
        String a="LOCK";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+BOOK_NO+" , "+SC_NO+" , "+ROUTE_NO+" FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfNO_METER_Reading(String book_no){
        Cursor cursor;
        String a="NO METER";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+BOOK_NO+" , "+SC_NO+" , "+ROUTE_NO+" FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfPOWER_OFF_Reading(String book_no){
        Cursor cursor;
        String a="POWER OFF";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+BOOK_NO+" , "+SC_NO+" , "+ROUTE_NO+" FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getDetailsOfNoDisplayReading(String book_no){
        Cursor cursor;
        String a="NO DISPLAY";
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+BOOK_NO+" , "+SC_NO+" , "+ROUTE_NO+" FROM "+TB_NAME+" WHERE "+BOOK_NO+" == "+"'"+book_no+"'"+" AND "+STATUS+" == "+"'"+a+"'";
        cursor=sqLiteDatabase.rawQuery(qs,null);
        return cursor;
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public String getMeterStatusValuePreviouslyCommited(int scNO){
        String met_status=null;
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String qs="SELECT "+STATUS+" FROM "+TB_NAME+" WHERE "+SC_NO+" == "+scNO;
        Cursor cursor=sqLiteDatabase.rawQuery(qs,null);
        cursor.moveToFirst();
        met_status=cursor.getString(cursor.getColumnIndex(STATUS));
        if(met_status != null && !met_status.isEmpty()){

        }else{
            met_status=null;
        }
        return met_status;
    }
    public Cursor getMasterDetailForConnectionID(int conn_id,SQLiteDatabase sqLiteDatabase){
        Cursor c;
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+SC_NO+" = "+conn_id;
        c=sqLiteDatabase.rawQuery(rq,null);
        return c;
    }
    public Boolean bookCodeValidation(String book_no,SQLiteDatabase sqLiteDatabase){
        boolean b=false;

        String rq="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" = "+"'"+book_no+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        if(cursor.getCount() > 0){
            b=true;
        }
        sqLiteDatabase.close();
    return b;
    }
    public Boolean CGL_NoValidation(String cgl_no, SQLiteDatabase sqLiteDatabase){
        boolean b=false;

        String rq="SELECT * FROM "+TB_NAME+" WHERE "+CGL_NO+" = "+"'"+cgl_no+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        if(cursor.getCount() > 0){
            b=true;
        }
        sqLiteDatabase.close();
        return b;

    }
    public Boolean Conn_NoValidation(String conn, SQLiteDatabase sqLiteDatabase){
        boolean b=false;

        String rq="SELECT * FROM "+TB_NAME+" WHERE "+SC_NO+" = "+Integer.parseInt(conn);
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        if(cursor.getCount() > 0){
            b=true;
        }
        sqLiteDatabase.close();
        return b;

    }
    public Boolean deleteDownloadedData(String route_no){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL("DELETE FROM "+TB_NAME+ " WHERE "+ BOOK_NO +" = "+" '"+route_no+"' ");
        database.close();
        return true;
    }
    public Cursor retriveDataByBook(String route_no){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" = "+"'"+route_no+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }

    public Cursor retriveDataByRoute(String route_no){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT * FROM "+TB_NAME+" WHERE "+BOOK_NO+" = "+"'"+route_no+"'"+" AND "+STATUS +" IS NOT NULL";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }

    public Cursor getCountOfConsumers(String s){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+SC_NO+" FROM "+TB_NAME+" WHERE "+BOOK_NO+" = "+"'"+s+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        return cursor;

    }
    public Cursor getSuccussfulReading(String book){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+SC_NO+" , "+METER_READING+" , "+NAME_CONSUMER+" FROM "+TB_NAME+" WHERE "+ BOOK_NO+" = "+"'"+book+"'";//+" AND "+METER_READING +" IS NOT NULL";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        //sqLiteDatabase.close();
        return cursor;
    }


    public void deleteMeterReadingBySC_NO(int connection_code,String book){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String q="DELETE FROM "+ TB_NAME+" WHERE "+BOOK_NO+" = "+"'"+book+"'"+" AND "+SC_NO+" = "+connection_code;
        sqLiteDatabase.execSQL(q);

    }



}
