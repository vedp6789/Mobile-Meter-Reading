package com.general.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by comp on 14/01/2015.
 */
public class DbLtpDownloadStatus extends SQLiteOpenHelper {
    public static String DB_NAME="LTP_DOWNLOAD_STATUS_DB";
    public static String TB_NAME="LTP_DOWNLOAD_STATUS_TB";
    public static int DB_VERSION=1;
    public static String ID="_id";
    public static String AREA="area";
    public static String NO_OF_CONSUMERS="no_of_cons";
    public static String RECORD_CREATION_DATE="download_date";
    public static String AREA_FOR_MONTH="month";
    public static String AREA_FOR_YEAR="year";
    public String create_query="CREATE TABLE "+TB_NAME+" ( "+ID +" INTEGER PRIMARY KEY AUTOINCREMENT , "+AREA +" TEXT , "+NO_OF_CONSUMERS+" INTEGER , "+RECORD_CREATION_DATE+" TEXT , "+AREA_FOR_MONTH+" TEXT , "+AREA_FOR_YEAR+ " TEXT )";


    public DbLtpDownloadStatus(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(create_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TB_NAME);
        onCreate(sqLiteDatabase);
    }

    public Boolean deleteDownloadedRecord(String area){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL("DELETE FROM "+TB_NAME+ " WHERE "+ AREA +" = "+" '"+area+"' ");
        database.close();
        return true;
    }

    public Cursor getAll(){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+AREA+" , "+NO_OF_CONSUMERS+" , "+RECORD_CREATION_DATE+" FROM "+ TB_NAME;
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        return cursor;
    }
    public Boolean updateRecord(ContentValues contentValues,String s){
        Boolean aBoolean=false;
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        int i=sqLiteDatabase.update(TB_NAME,contentValues,AREA+ " == "+"'"+s+"'",null);
        if(i>0){
            aBoolean=true;
        }
        return aBoolean;

    }

    public Boolean insertRecord(ContentValues contentValues){
        Boolean aBoolean=false;
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();

        long i=sqLiteDatabase.insert(TB_NAME,null,contentValues);
        sqLiteDatabase.close();
        if(i>0){
            aBoolean=true;
        }
        return aBoolean;
    }
    public String[] getMonthAndYear(String area){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+AREA_FOR_MONTH+" , "+AREA_FOR_YEAR+" FROM "+TB_NAME+" WHERE "+AREA+" = "+"'"+area+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        cursor.moveToFirst();
        String []a={cursor.getString(cursor.getColumnIndex(DbLtpDownloadStatus.AREA_FOR_MONTH)),cursor.getString(cursor.getColumnIndex(DbLtpDownloadStatus.AREA_FOR_YEAR))};
        cursor.close();
        sqLiteDatabase.close();
        return a;
    }
}
