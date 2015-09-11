package com.general.vvvv.cmr;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.general.SQLite.DbConnection;
import com.general.SQLite.DownloadStatusDb;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vvvv on 25-08-2014.
 */
public class MyDownload extends ActionBarActivity{
    EditText eT_route_no;
    Button b_download,b_delete;
    ProgressDialog pDialog;
    String book_no=null;
    JSONArray master_detail_array_respnse=null;
    DbConnection dbConnection;
    SQLiteDatabase sqLiteDatabase;

    DownloadStatusDb downloadStatusDb;
    SQLiteDatabase liteDatabase;

    public static boolean db_update=false;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyDbErrorHandler implements DatabaseErrorHandler{
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    }
    //SQLiteOpenHelper dbHelper= new SQLiteOpenHelper()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initialise();

        dbConnection=new DbConnection(this,DbConnection.DB_NAME,null,DbConnection.DB_VERSION,new MyDbErrorHandler());
        sqLiteDatabase=dbConnection.getWritableDatabase();

        downloadStatusDb =new DownloadStatusDb(this,DownloadStatusDb.DB_NAME,null,DownloadStatusDb.DB_VERSION);
        liteDatabase=downloadStatusDb.getWritableDatabase();

        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(MyDownload.this);
        alertDialog.setTitle("Confirm Delete...");
        alertDialog.setMessage("Are you sure you want to delete this?");
        alertDialog.setIcon(R.drawable.ia);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbConnection.deleteDownloadedData(eT_route_no.getText().toString());
                downloadStatusDb.deleteDownloadedRecord(eT_route_no.getText().toString());
                Toast.makeText(MyDownload.this,"Record related to Route number "+eT_route_no.getText().toString()+" got deleted",Toast.LENGTH_SHORT).show();
                sqLiteDatabase.close();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sqLiteDatabase.close();
                Toast.makeText(MyDownload.this,"You clicked on NO",Toast.LENGTH_SHORT).show();
            }
        });


        b_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        if(eT_route_no.getText().length()!=0){
        //.makeText(MyDownload.this,"not null",Toast.LENGTH_SHORT).show();
        DownloadMasterDetail downloadMasterDetail=new DownloadMasterDetail();
        downloadMasterDetail.execute(eT_route_no.getText().toString());
        }else{
                  Toast.makeText(MyDownload.this,"Insert Route Number",Toast.LENGTH_SHORT).show();
        }
            }
        });

        b_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               if(eT_route_no.getText().length()!=0){
                   Cursor c=dbConnection.myDownloadDeleteHelper(eT_route_no.getText().toString());
                   if(c.getCount()>0){
                       Toast.makeText(MyDownload.this,"You are going to delete record of this route.",Toast.LENGTH_LONG).show();
                       alertDialog.show();

                   }
                   else{
                       Toast.makeText(MyDownload.this,"This Route number is not available in local database.",Toast.LENGTH_SHORT).show();
                   }
               }else{
                   Toast.makeText(MyDownload.this,"Insert Route number to delete record.",Toast.LENGTH_SHORT).show();
               }
             sqLiteDatabase.close();
            }
        });
    }
    public void initialise(){
        eT_route_no=(EditText)findViewById(R.id.et_route_no);
        b_delete=(Button)findViewById(R.id.b_delete_master_detail);
        b_download=(Button)findViewById(R.id.b_download_master_detail);
    }


    public class DownloadMasterDetail extends AsyncTask<String,Void,Boolean> {
        String response1=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog=new ProgressDialog(MyDownload.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(false);
                }
            });
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
        Boolean aBoolean=false;
            String url="http://117.239.214.2:8087/WSDemo1/resttest/invoke/masterdetail/"+strings[0];
            //String url="http://10.0.2.2:8080/WSDemo1/resttest/invoke/masterdetail/"+strings[0];
            try{
                HttpGet httpGet=new HttpGet(url);
                HttpParams httpParams=new BasicHttpParams();
                DefaultHttpClient defaultHttpClient=new DefaultHttpClient(httpParams);
                HttpResponse httpResponse=defaultHttpClient.execute(httpGet);
                HttpEntity httpEntity=httpResponse.getEntity();
                response1= EntityUtils.toString(httpEntity);
                //JSONObject rootobj=new JSONObject(response1);
                String jsonString=response1;
                Log.d("Response:","....:::::"+jsonString);
                if(jsonString!=null) {try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsona = jsonObject.getJSONArray("MASTER_DETAIL");
                    JSONObject jsono=jsona.getJSONObject(0);
                    book_no=jsono.getString("BOOK_NO");
                    master_detail_array_respnse=jsonObject.getJSONArray("MASTER_DETAIL");
                    for(int i=0;i<master_detail_array_respnse.length();i++){
                        JSONObject c=master_detail_array_respnse.getJSONObject(i);
                        ContentValues contentValues=new ContentValues();
                        contentValues.put(DbConnection.S_NO,Integer.parseInt(c.getString("S_NO")));
                        contentValues.put(DbConnection.BOOK_NO,c.getString("BOOK_NO"));
                        contentValues.put(DbConnection.ROUTE_NO,c.getString("ROUTE_NO"));
                        contentValues.put(DbConnection.CGL_NO,c.getString("CGL_NO"));
                        contentValues.put(DbConnection.SC_NO,Integer.parseInt(c.getString("SC_NO")));
                        contentValues.put(DbConnection.NAME_CONSUMER,c.getString("NAME_CONSUMER"));
                        //contentValues.put(DbConnection.ADDRESS,c.getString("ADDRESS"));
                        //contentValues.put(DbConnection.CONTACT_NO,c.getString("CONTACT_NO"));
                        contentValues.put(DbConnection.METER_NO,c.getString("METER_NO"));
                        //contentValues.put(DbConnection.SUBSTATIONNAME,c.getString("SUBSTATIONNAME"));
                        //contentValues.put(DbConnection.FEEDERNAME,c.getString("FEEDERNAME"));
                        dbConnection.insertMasterDetail(contentValues);

                    }
                    aBoolean=true;
                    publishProgress();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
                else{
                    Toast.makeText(MyDownload.this,"Download: Couldn't get any data from server.",Toast.LENGTH_LONG).show();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return aBoolean;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
            }
/*public String InputStreamToString(InputStream is){
            String resp=null;
       return resp;
        }*/

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean.booleanValue())
            if(pDialog.isShowing()){
                pDialog.dismiss();
                eT_route_no.setText("");
                eT_route_no.clearFocus();

            }
            if(aBoolean & db_update){
                Toast.makeText(MyDownload.this,"Local Device Updated!",Toast.LENGTH_SHORT).show();
            }
            if (pDialog != null) {
                pDialog.dismiss();
            }
            ModifyDownloadStatusTable modifyStatusTable=new ModifyDownloadStatusTable();
            modifyStatusTable.modify(db_update,book_no);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (pDialog != null) {
                pDialog.dismiss();

            }
        }
    }
    class ModifyDownloadStatusTable{
        DownloadStatusDb downloadStatusDb;
        SQLiteDatabase liteDatabase;
        boolean allow=false,statusDb=false;
        public void modify(Boolean aBoolean,String s){
            if(aBoolean){
                downloadStatusDb=new DownloadStatusDb(MyDownload.this,DownloadStatusDb.DB_NAME,null,DownloadStatusDb.DB_VERSION);
                liteDatabase=downloadStatusDb.getWritableDatabase();

                Cursor cursor=dbConnection.getCountOfConsumers(s);
                int no_of_consumers_of_this_book_no=cursor.getCount();
                Cursor cursor1=downloadStatusDb.getAll();
                if(cursor1.getCount() >0){
                    cursor1.moveToFirst();
                    do{
                        String temp_st=cursor1.getString(cursor1.getColumnIndex(DownloadStatusDb.BOOK_NO));
                        if(temp_st.equals(s)){
                            allow=true;
                        }
                    }while (cursor1.moveToNext());
                }

                liteDatabase.close();

                SimpleDateFormat sdf1=new SimpleDateFormat("ddMMyyyy_HHmmss");/////////////////////Current Time ///////////////////////////
                String currentDateandTime1= sdf1.format(new Date());
                ContentValues contentValues=new ContentValues();
                contentValues.put(DownloadStatusDb.NO_OF_CONSUMERS,no_of_consumers_of_this_book_no);
                contentValues.put(DownloadStatusDb.RECORD_CREATION_DATE,currentDateandTime1);
                if(allow){

                    statusDb=downloadStatusDb.updateRecord(contentValues,s);
                }else {
                    contentValues.put(DownloadStatusDb.BOOK_NO,s);
                    statusDb=downloadStatusDb.insertRecord(contentValues);
                }

                if(statusDb){
                    Toast.makeText(MyDownload.this,"Status database modified.",Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

}
