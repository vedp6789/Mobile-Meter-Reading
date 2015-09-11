package com.general.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by comp on 14/09/2014.
 */
public class DownloadStatusDb extends SQLiteOpenHelper{
    public static String DB_NAME="DOWNLOAD_STATUS_DB";
    public static String TB_NAME="DOWNLOAD_STATUS_TB";
    public static int DB_VERSION=14;
    public static String ID="_id";
    public static String BOOK_NO="book_no";
    public static String NO_OF_CONSUMERS="no_of_cons";
    public static String RECORD_CREATION_DATE="date_modified";
    public static String BOOK_NO_MONTH="book_moth";
    public static String BOOK_NO_YEAR="book_year";
    public String create_query="CREATE TABLE "+TB_NAME+" ( "+ID +" INTEGER PRIMARY KEY AUTOINCREMENT , "+BOOK_NO +" TEXT , "+NO_OF_CONSUMERS+" INTEGER , "+RECORD_CREATION_DATE+" TEXT , "+BOOK_NO_MONTH+" TEXT , "+BOOK_NO_YEAR+ " TEXT )";

    public DownloadStatusDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL(create_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TB_NAME);
        onCreate(db);
    }

    public Cursor getAll(){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+BOOK_NO+" , "+NO_OF_CONSUMERS+" , "+RECORD_CREATION_DATE+" FROM "+ TB_NAME;
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        return cursor;
    }
    public String[] getMonthAndYear(String book){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        String rq="SELECT "+BOOK_NO_MONTH+" , "+BOOK_NO_YEAR+" FROM "+TB_NAME+" WHERE "+BOOK_NO+" = "+"'"+book+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(rq,null);
        cursor.moveToFirst();
        String []a={cursor.getString(cursor.getColumnIndex(DownloadStatusDb.BOOK_NO_MONTH)),cursor.getString(cursor.getColumnIndex(DownloadStatusDb.BOOK_NO_YEAR))};
        cursor.close();
        sqLiteDatabase.close();
        return a;
    }
    public Boolean updateRecord(ContentValues contentValues,String s){
        Boolean aBoolean=false;
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        int i=sqLiteDatabase.update(TB_NAME,contentValues,BOOK_NO+ " == "+"'"+s+"'",null);
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

    public Boolean deleteDownloadedRecord(String route_no){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL("DELETE FROM "+TB_NAME+ " WHERE "+ BOOK_NO +" = "+" '"+route_no+"' ");
        database.close();
        return true;
    }
}
