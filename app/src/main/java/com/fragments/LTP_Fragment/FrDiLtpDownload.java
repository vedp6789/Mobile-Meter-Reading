package com.fragments.LTP_Fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fragments.Nullable;
import com.general.SQLite.DbConnection;
import com.general.SQLite.DbHTCReading;
import com.general.SQLite.DbHtcDownloadStatus;
import com.general.SQLite.DbLTPReading;
import com.general.SQLite.DbLogin;
import com.general.SQLite.DbLtpDownloadStatus;
import com.general.SQLite.DownloadStatusDb;
import com.general.vvvv.cmr.R;
import com.remote_connection.InternetConnectivity;

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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by comp on 14/01/2015.
 */
public class FrDiLtpDownload extends DialogFragment implements View.OnClickListener {
    EditText eT_area;
    TextView tv_down_stat,tv_area_for;
    Spinner sp_month,sp_year;
    String s_month="Month",s_year="Year";
    Button b_download, b_delete, b_cancel;
    ProgressDialog pDialog;
    String area = null;
    JSONArray master_detail_LTC_respnse = null;
    AlertDialog.Builder alertDialog;
    AlertDialog.Builder alertDialogDownload;
    DbLTPReading dbLTPReading;
    SQLiteDatabase sqLiteDatabase;

    DbLtpDownloadStatus dbLtpDownloadStatus;
    SQLiteDatabase liteDatabase;

    public static boolean db_update = false;
    File f2_image_directory;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_dd_dialog:

                db_update = false;
                sqLiteDatabase = dbLTPReading.getWritableDatabase();
                liteDatabase = dbLtpDownloadStatus.getWritableDatabase();
                boolean allowDownload = true;
                if (eT_area.getText().length() != 0) {
                    area=eT_area.getText().toString().toUpperCase();
                    if((!s_month.equals("Month"))){
                        if(!s_year.equals("Year")){
                            //.makeText(MyDownload.this,"not null",Toast.LENGTH_SHORT).show();
                            InternetConnectivity internetConnectivity = new InternetConnectivity();

                            Boolean aBoolean = internetConnectivity.checkInternetConnection(getActivity());
                            if (aBoolean) {
                                Cursor cursor = dbLtpDownloadStatus.getAll();
                                if (cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    do {
                                        String area = cursor.getString(cursor.getColumnIndex(DbHtcDownloadStatus.AREA));
                                        if (area.equals(eT_area.getText().toString().toUpperCase())) {
                                            allowDownload = false;
                                        }
                                    } while (cursor.moveToNext());
                                    if (allowDownload) {
                                        DownloadMasterDetail downloadMasterDetail = new DownloadMasterDetail();
                                        downloadMasterDetail.execute(eT_area.getText().toString().toUpperCase());
                                    } else {
                                        alertDialogDownload.show();
                                    }
                                } else {
                                    DownloadMasterDetail downloadMasterDetail = new DownloadMasterDetail();
                                    downloadMasterDetail.execute(eT_area.getText().toString().toUpperCase());
                                }


                            } else {
                                Toast.makeText(getActivity(), "No internet connectivity found !", Toast.LENGTH_SHORT).show();
                            }
                            liteDatabase.close();
                            sqLiteDatabase.close();
                        }else{
                            Toast.makeText(getActivity(),"Select year for this Area.",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(getActivity(),"Select month for this Area.",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity(), "Insert Area code", Toast.LENGTH_SHORT).show();
                    liteDatabase.close();
                    sqLiteDatabase.close();
                }
                break;
            case R.id.b_dialog_delete:

                sqLiteDatabase = dbLTPReading.getWritableDatabase();
                liteDatabase = dbLtpDownloadStatus.getWritableDatabase();
                if (eT_area.getText().length() != 0) {
                    area=eT_area.getText().toString().toUpperCase();
                    Cursor c = dbLTPReading.myDownloadDeleteHelper(eT_area.getText().toString().toUpperCase());
                    if (c.getCount() > 0) {
                        //Toast.makeText(getActivity(),"You are going to delete record of this route.",Toast.LENGTH_LONG).show();
                        alertDialog.show();

                    } else {
                        Toast.makeText(getActivity(), "This Area is not available in local database.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Insert Area to delete record.", Toast.LENGTH_SHORT).show();
                }
                liteDatabase.close();
                sqLiteDatabase.close();
                //sqLiteDatabase.close();
                break;
            case R.id.b_downloadDialogCancel:

                dismiss();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyDbErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        area=null;
        db_update=false;
        setRetainInstance(true);
        dbLTPReading = new DbLTPReading(getActivity(), DbLTPReading.DB_NAME, null, DbLTPReading.DB_VERSION);
        sqLiteDatabase = dbLTPReading.getWritableDatabase();

        dbLtpDownloadStatus = new DbLtpDownloadStatus(getActivity(), DbLtpDownloadStatus.DB_NAME, null, DbLtpDownloadStatus.DB_VERSION);
        liteDatabase = dbLtpDownloadStatus.getWritableDatabase();


        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Confirm Delete...");
        alertDialog.setMessage("Are you sure you want to delete this?");
        alertDialog.setIcon(R.drawable.ia);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dbConnection.deleteDownloadedData(eT_route_no.getText().toString());
                //  downloadStatusDb.deleteDownloadedRecord(eT_route_no.getText().toString());
                boolean b = dbLTPReading.deleteDownloadedData(eT_area.getText().toString().toUpperCase());
                boolean b1 = dbLtpDownloadStatus.deleteDownloadedRecord(eT_area.getText().toString().toUpperCase());
                if (b & b1) {
                    tv_down_stat.setText("Record get deleted.");


                    /*///////           Image  Not required to delete
                    String dpath2="/MMR/LTP"+String.valueOf(eT_area.getText().toString().toUpperCase());
                    f2_image_directory = new File(Environment.getExternalStorageDirectory() + dpath2);
                    if(f2_image_directory.isDirectory()){
                        for (File child : f2_image_directory.listFiles()) {

                            child.delete();
                        }
                        f2_image_directory.delete();
                    }
                    */////////////////


                    //Toast.makeText(getActivity(),"Record related to Book No. "+eT_route_no.getText().toString()+" got deleted",Toast.LENGTH_SHORT).show();
                } else {
                    tv_down_stat.setText("Record not get deleted.");
                }
                Toast.makeText(getActivity(), "Record related to area " + eT_area.getText().toString().toLowerCase() + " got deleted", Toast.LENGTH_SHORT).show();
                // DownloadMasterDetail downloadMasterDetail=new DownloadMasterDetail();
                //downloadMasterDetail.execute(eT_route_no.getText().toString());
                sqLiteDatabase.close();
                liteDatabase.close();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sqLiteDatabase.close();
                liteDatabase.close();
                Toast.makeText(getActivity(), "You clicked on NO", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogDownload = new AlertDialog.Builder(getActivity());
        alertDialogDownload.setTitle("Download..");
        alertDialogDownload.setMessage("Area is already downloaded.");
        alertDialogDownload.setPositiveButton("Delete & Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean b = dbLTPReading.deleteDownloadedData(eT_area.getText().toString().toUpperCase());
                boolean b1 = dbLtpDownloadStatus.deleteDownloadedRecord(eT_area.getText().toString().toUpperCase());
                if (b & b1) {
                    Toast.makeText(getActivity(), "Record related to area " + eT_area.getText().toString().toUpperCase() + " got deleted", Toast.LENGTH_SHORT).show();
                    tv_down_stat.setText("Record get deleted.");
                    /*//// Image not reqd to delete
                    String dpath2="/MMR/LTP"+String.valueOf(eT_area.getText().toString().toUpperCase());
                    f2_image_directory = new File(Environment.getExternalStorageDirectory() + dpath2);
                    if(f2_image_directory.isDirectory()){
                        for (File child : f2_image_directory.listFiles()) {

                            child.delete();
                        }
                        f2_image_directory.delete();
                    }
                    */////////////////////
                } else {
                    tv_down_stat.setText("Record not get deleted.");
                }

                //Toast.makeText(getActivity(),"Record related to Book No. "+eT_route_no.getText().toString()+" got deleted",Toast.LENGTH_SHORT).show();
                DownloadMasterDetail downloadMasterDetail = new DownloadMasterDetail();
                tv_down_stat.setText("Trying to download...");
                downloadMasterDetail.execute(eT_area.getText().toString().toUpperCase());
                sqLiteDatabase.close();
                liteDatabase.close();
            }
        });
        alertDialogDownload.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sqLiteDatabase.close();
                liteDatabase.close();
                Toast.makeText(getActivity(), "You clicked on Cancel", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_d_download_ltp, container, false);
        eT_area = (EditText) v.findViewById(R.id.et_dd_area);
        b_delete = (Button) v.findViewById(R.id.b_dialog_delete);
        b_download = (Button) v.findViewById(R.id.b_dd_dialog);
        b_cancel = (Button) v.findViewById(R.id.b_downloadDialogCancel);
        tv_down_stat = (TextView) v.findViewById(R.id.tv_download_status);
        tv_area_for= (TextView) v.findViewById(R.id.tv_MMR_for);
        sp_month= (Spinner) v.findViewById(R.id.sp_month);
        sp_year= (Spinner) v.findViewById(R.id.sp_year);

        b_download.setOnClickListener(this);
        b_delete.setOnClickListener(this);
        b_cancel.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getActivity(),R.array.month,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_month.setAdapter(adapter);
        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_month=sp_month.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(getActivity(),R.array.year,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_year.setAdapter(adapter2);
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_year=sp_year.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.download_dialog_title));
        dialog.setCanceledOnTouchOutside(false);

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        // sqLiteDatabase.close();
        // liteDatabase.close();
    }

    public class DownloadMasterDetail extends AsyncTask<String, Void, Integer> {
        String response1 = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
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
        protected Integer doInBackground(String... strings) {
            Integer result = 0;
            Integer no_data_found_for_this_book = 3;
            Integer user_not_found = 1;
            Integer unfortunate_fatal = 4;
            Integer success = 2;
            Boolean aBoolean = false;

            DbLogin dbLogin = new DbLogin(getActivity(), DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
            SQLiteDatabase sqLiteDatabase1 = dbLogin.getReadableDatabase();
            Cursor cursor = dbLogin.getData(sqLiteDatabase1);
            cursor.moveToFirst();
            String imei = cursor.getString(cursor.getColumnIndex(DbLogin.IMEI)).toString();
            System.out.println(imei);
            sqLiteDatabase1.close();
            //String url="http://10.0.2.2:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            //String url="http://117.239.214.2:8087/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            //String url="http://117.239.214.4:8087/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            // String url="http://1.22.91.107:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            //  String url="http://192.168.0.111:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            //String url = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            //String url = "http://192.168.1.14:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            //192.168.1.14
            //   String url="http://192.168.0.102:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;

            // String url="http://192.168.1.2:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            //String url = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;  // Rajiv sir comp ip
            //   String url="http://192.168.43.80:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;  // Rajiv sir comp ip
            String url="http://10.0.2.2:8080/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;

            //String url = "http://192.168.0.100:8081/WSDemo1/resttest/invoke/ltp/"+strings[0]+"/"+imei;
            try {
                HttpGet httpGet = new HttpGet(url);
                HttpParams httpParams = new BasicHttpParams();
                DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);
                HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                response1 = EntityUtils.toString(httpEntity);
                //JSONObject rootobj=new JSONObject(response1);
                String jsonString = response1;
                Log.d("Response:", "....---------" + jsonString);
                if (jsonString != null) {
                    // jsonString=null;
                    try {
                        JSONArray jsonArray = new JSONArray(jsonString);
                        JSONObject getUserObject = jsonArray.getJSONObject(0);
                        String USER = getUserObject.getString("USER");
                        if (USER.equals("ACTIVE")) {
                            JSONArray jsonArray1=jsonArray.getJSONArray(1);
                            //JSONObject jsonObject = jsonArray.getJSONObject(1);
                            //JSONArray jsona = jsonObject.getJSONArray("MASTER_DETAIL");
                            //if (jsona.length() > 0) {
                            if (jsonArray1.length() > 0) {
                                //JSONObject jsono = jsona.getJSONObject(0);
                                //book_no = jsono.getString("BOOK_NO");
                                //master_detail_array_respnse = jsonObject.getJSONArray("MASTER_DETAIL");
                                //for (int i = 0; i < master_detail_array_respnse.length(); i++) {
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    //JSONObject c = master_detail_array_respnse.getJSONObject(i);
                                    JSONObject c = jsonArray1.getJSONObject(i);
                                    ContentValues contentValues = new ContentValues();
                                    //contentValues.put(DbConnection.S_NO, Integer.parseInt(c.getString("S_NO")));
                                    contentValues.put(DbLTPReading.AREA,area);
                                    contentValues.put(DbLTPReading.LTP_NO, c.getInt("LTP_NO"));
                                    contentValues.put(DbLTPReading.NAME, c.getString("NAME"));
                                    contentValues.put(DbLTPReading.CGL_NO,c.getString("CGL_NO"));
                                    //contentValues.put(DbConnection.CGL_NO, c.getString("CGL_NO"));
                                    //contentValues.put(DbConnection.SC_NO, Integer.parseInt(c.getString("SC_NO")));
                                    //contentValues.put(DbConnection.NAME_CONSUMER, c.getString("NAME_CONSUMER"));
                                    //contentValues.put(DbConnection.ADDRESS,c.getString("ADDRESS"));
                                    //contentValues.put(DbConnection.CONTACT_NO,c.getString("CONTACT_NO"));
                                    //contentValues.put(DbConnection.METER_NO, c.getString("METER_NO"));
                                    //contentValues.put(DbConnection.SUBSTATIONNAME,c.getString("SUBSTATIONNAME"));
                                    // contentValues.put(DbConnection.FEEDERNAME,c.getString("FEEDERNAME"));
                                    aBoolean = dbLTPReading.insertMasterLtpData(contentValues);
                                }
                                if (aBoolean) {
                                    result = success;
                                    publishProgress();
                                }
                            } else {
                                result = no_data_found_for_this_book;
                            }
                        } else {
                            result = user_not_found;
                        }

                        //  JSONObject jsonObject = new JSONObject(jsonString);
                        //  JSONArray jsona = jsonObject.getJSONArray("MASTER_DETAIL");


                        //aBoolean=true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonString = null;
                } else {
                    result = unfortunate_fatal;
                    //Toast.makeText(getActivity(),"Download: Couldn't get any data from server.",Toast.LENGTH_LONG).show();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
/*public String InputStreamToString(InputStream is){
            String resp=null;
       return resp;
        }*/

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer.equals(0)) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    // eT_route_no.setText("");
                    //  eT_route_no.clearFocus();
                    Toast.makeText(getActivity(), "Fatal termination" + "\n" + "Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
            if (integer.equals(4)) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    // eT_route_no.setText("");
                    //  eT_route_no.clearFocus();
                    Toast.makeText(getActivity(), "Network fatal " + "\n" + "Check your server side program!", Toast.LENGTH_SHORT).show();
                }
            }
            if (integer.equals(1)) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    // eT_route_no.setText("");
                    //  eT_route_no.clearFocus();
                    Toast.makeText(getActivity(), "Authorization Denied" + "\n" + "Contact software provider!", Toast.LENGTH_SHORT).show();
                }
            }
            if (integer.equals(3)) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    // eT_route_no.setText("");
                    //  eT_route_no.clearFocus();
                    Toast.makeText(getActivity(), "No data found for this Area code" + "\n" + "Crosscheck your Area code.!", Toast.LENGTH_SHORT).show();
                }
            }

            if (integer.equals(2) & db_update) {
                tv_down_stat.setText("Last download completed.");
                // Toast.makeText(getActivity(),"Local Device Updated!",Toast.LENGTH_SHORT).show();
            } else {
                tv_down_stat.setText("Last download not completed.");
            }
            if (pDialog != null) {
                pDialog.dismiss();
            }
            ModifyDownloadStatusTable modifyStatusTable = new ModifyDownloadStatusTable();
            modifyStatusTable.modify(db_update, area);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (pDialog != null) {
                pDialog.dismiss();

            }
        }
    }

    class ModifyDownloadStatusTable {
        public ModifyDownloadStatusTable() {
            allow = false;
            statusDb = false;
        }


        DbLtpDownloadStatus dbLtpDownloadStatus;
        SQLiteDatabase liteDatabase;

        public boolean allow, statusDb;

        public void modify(Boolean aBoolean, String s) {
            if (aBoolean) {

                dbLtpDownloadStatus = new DbLtpDownloadStatus(getActivity(), DbLtpDownloadStatus.DB_NAME, null, DbLtpDownloadStatus.DB_VERSION);
                liteDatabase = dbLtpDownloadStatus.getWritableDatabase();

                Cursor cursor = dbLTPReading.getCountOfConsumers(s);
                int no_of_consumers_of_this_area_no = cursor.getCount();
                Cursor cursor1 = dbLtpDownloadStatus.getAll();
                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    do {
                        String temp_st = cursor1.getString(cursor1.getColumnIndex(DbHtcDownloadStatus.AREA));
                        if (temp_st.equals(s)) {
                            allow = true;
                        }
                    } while (cursor1.moveToNext());
                }

                liteDatabase.close();

                SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy_HHmmss");/////////////////////Current Time ///////////////////////////
                String currentDateandTime1 = sdf1.format(new Date());
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbLtpDownloadStatus.NO_OF_CONSUMERS, no_of_consumers_of_this_area_no);
                contentValues.put(DbLtpDownloadStatus.RECORD_CREATION_DATE, currentDateandTime1);
                contentValues.put(DbLtpDownloadStatus.AREA_FOR_MONTH,s_month);
                contentValues.put(DbLtpDownloadStatus.AREA_FOR_YEAR,s_year);
                if (allow) {

                    statusDb = dbLtpDownloadStatus.updateRecord(contentValues, s);
                } else {
                    contentValues.put(DbLtpDownloadStatus.AREA, s);
                    statusDb = dbLtpDownloadStatus.insertRecord(contentValues);
                }

                if (statusDb) {
                    Toast.makeText(getActivity(), "Status database modified.", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
}
