package com.general.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by comp on 17/09/2014.
 */
public class DbLogin extends SQLiteOpenHelper {
    public static String DB_NAME="LoginDatabase";
    public static int DB_VERSION=9;
    public static String TB_NAME="daman_mobile_login";
    public static String  ID="_id";
    public static String USER_ID="user_id";
    public static String LICENSE_NO="license_no";
    public static String IMEI="imei_no";

    public static String cq="CREATE TABLE "+ TB_NAME+" ( "+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_ID+" INTEGER, "+LICENSE_NO+" TEXT, "+IMEI+" TEXT )";

    public DbLogin(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL(cq);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS "+TB_NAME);
         onCreate(db);
    }
    public boolean insertData(ContentValues cv,SQLiteDatabase db){
        boolean b=false;
        long l=db.insert(TB_NAME,null,cv);
        if(l > 0){
            b=true;
        }
        return b;
    }
    public Cursor getData(SQLiteDatabase db){
        String qs="SELECT "+USER_ID+" , "+LICENSE_NO+" , "+IMEI+" FROM "+TB_NAME;
        Cursor cursor=db.rawQuery(qs,null);
        return cursor;
    }
    public Cursor getUserId(SQLiteDatabase db){
        String qs="SELECT "+USER_ID+" FROM "+TB_NAME;
        Cursor cursor=db.rawQuery(qs,null);
        return cursor;
    }



}
