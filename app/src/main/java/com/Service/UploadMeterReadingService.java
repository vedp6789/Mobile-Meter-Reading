package com.Service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.general.SQLite.DbConnection;
import com.general.SQLite.DbLogin;
import com.general.SQLite.DownloadStatusDb;
import com.general.vvvv.cmr.ElectricityDeptDaman;
import com.general.vvvv.cmr.MyActivity;
import com.general.vvvv.cmr.R;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Dell on 28-11-2014.
 */
public class UploadMeterReadingService extends Service {
    DbConnection dbConnection;
    DownloadStatusDb downloadStatusDb;
    SQLiteDatabase sqLiteDatabase2;
    Context context = this;
    Cursor c1;
    static int counter;
    NotificationCompat.Builder builder;
    static NotificationManager notificationManager;
    static String month,year;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        dbConnection = new DbConnection(context, DbConnection.DB_NAME, null, DbConnection.DB_VERSION, new MyDbErrorHandler());
        downloadStatusDb = new DownloadStatusDb(getApplicationContext(),DownloadStatusDb.DB_NAME,null,DownloadStatusDb.DB_VERSION);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyDbErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);
        Toast.makeText(getApplicationContext(), "Trying to upload...", Toast.LENGTH_LONG).show();
        final String book = intent.getStringExtra("BOOK");
        c1 = dbConnection.retriveDataByRoute(book);



        ///*/////////////////////////////////   Commented as month and year is not required to send ////////////////////

        String month_year []=downloadStatusDb.getMonthAndYear(book);
        month=month_year[0];
        year=month_year[1];
        Toast.makeText(getApplicationContext(),"Book No:"+book+"\nMonth:"+month+"\n"+"Year:"+year,Toast.LENGTH_LONG).show();
        //*/////////////////////////////////////////////////////////////////////////////////////////////////////////////





        final Handler handler = new Handler(Looper.getMainLooper());

        final Runnable r = new Runnable() {
            public void run() {


                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int database_size = c1.getCount(), db_increment = 0;
                            String output = null;
                            counter = 0;
                            DbLogin dbLogin = new DbLogin(context, DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
                            SQLiteDatabase sqLiteDatabase1 = dbLogin.getReadableDatabase();
                            Cursor c_dbLogin = dbLogin.getData(sqLiteDatabase1);
                            c_dbLogin.moveToFirst();
                            String imei = c_dbLogin.getString(c_dbLogin.getColumnIndex(DbLogin.IMEI));
                            //  System.out.println(":::::::::::::::Imei for user" + imei);
                            sqLiteDatabase1.close();
                            JSONArray jsona_UserImei = new JSONArray();
                            JSONObject jsonObjectImei = new JSONObject();
                            try {
                                jsonObjectImei.put("USER_IMEI", imei);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsona_UserImei.put(jsonObjectImei);

                            //10.0.2.2:8080
                            /// 192.168.0.102
                           String url1 = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/validateUser";
                            /// String url1 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/validateUser";
                            //  String url1 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/validateUser";
                          //String url1 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://192.168.1.2:8080/WSDemo1/resttest/invoke/validateUser";
                           // String url1 = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/validateUser";  // dynamic ip of Rajib sir comp
                            //  String url1 = "http://192.168.0.102:8080/WSDemo1/resttest/invoke/validateUser";
                            // String url1 = "http://192.168.43.80:8080/WSDemo1/resttest/invoke/validateUser";


                            //   String url2 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/newload";
                           String url2 = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/newload";
                            // String url2 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/newload";
                          // String url2 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/newload";
                            // String url2 = "http://192.168.43.80:8080/WSDemo1/resttest/invoke/newload";
                            //192.168.1.2
                            //String url2 = "http://192.168.1.2:8080/WSDemo1/resttest/invoke/newload";
                           // String url2 = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/newload";
                            //   String url2 = "http://192.168.0.102:8080/WSDemo1/resttest/invoke/newload";



/*//////////////// Commented to check HttpUrlConnection
                            HttpPost httpPost1 = new HttpPost(url1);

                            HttpParams httpParams1 = new BasicHttpParams();


                            DefaultHttpClient defaultHttpClient1 = new DefaultHttpClient(httpParams1);


                            HttpResponse httpResponse1;
*////////////////////////

                            try {
/*?????????????????????  Commented to check HttpUrlConnection
                                StringEntity stringEntity1 = new StringEntity(jsona_UserImei.toString());
                                Log.d("String Entity imei data", "::::::::::" + stringEntity1);
                                Log.d("String Entity imei data", "::::::::::" + stringEntity1.toString());
                                stringEntity1.setContentEncoding("UTF-8");
                                stringEntity1.setContentType("application/json");

                                httpPost1.setEntity(stringEntity1);
                                httpPost1.addHeader("Accept", "application/json");


                                httpResponse1 = defaultHttpClient1.execute(httpPost1);
                                InputStream inputStream = httpResponse1.getEntity().getContent();
                                String serverResponse = null;
                                if (inputStream != null) {
                                    serverResponse = convertInputStreamToString(inputStream);
                                }
                                Log.d("Response from Upload user_validation webservice:", ":::::::" + serverResponse + ":::::");
                                Integer user_validation = Integer.parseInt(serverResponse);
                                *//////////////
                                URL url_http1=new URL(url1);
                                HttpURLConnection httpURLConnection= (HttpURLConnection) url_http1.openConnection();
                                httpURLConnection.setDoOutput(true);
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                                OutputStream os = httpURLConnection.getOutputStream();
                                String a=String.valueOf(jsona_UserImei);

                                os.write(a.getBytes());
                                os.close();
                                os.flush();

                                BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));


                                String sr="";
                                System.out.println("Output from Server .... \n");
                                String line="";

                                while ((line = br.readLine()) != null)
                                    sr+= line;
                                   /* while ((sr = br.readLine()) != null) {
                                        sr=br.readLine();
                                        System.out.println("Response:"+sr);
                                    }*/
                                System.out.println("Validation response from server:"+sr);
                                Integer ip = Integer.parseInt(sr);
                                System.out.println("Output from Server \n"+sr);
                                httpURLConnection.disconnect();



                                Integer validation_denied = 0;
                                Integer validation_success = 1;

                                if (ip.equals(validation_denied)) {
                                    output = "-1";
                                    Log.d("Validation denied for this IMEI", "Contact software provider");
                                    final Handler handler4 = new Handler(Looper.getMainLooper());   // Looper.getMainLooper returns main thread
                                    handler4.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Validation denied for this IMEI No."+ "\nContact software provider",
                                                    Toast.LENGTH_LONG).show();
                                            

                                        }
                                    });
                                }






                                if (ip.equals(validation_success)) {
                                    c1.moveToFirst();
                                    do {
                                        ++db_increment;
                                        JSONArray jsona_Upload_data = new JSONArray();
                                        JSONObject jsonObject2 = new JSONObject();

                                        String meterReading = c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                        String meterStatus = "";
                                        meterStatus = c1.getString(c1.getColumnIndex(DbConnection.STATUS)).toString();
                                        System.out.println("Meter Status:::" + meterStatus);
                                        System.out.println("MeterReading:::" + meterReading);
                                        String mS1 = "LOCK";
                                        String mS2 = "NO METER";
                                        String mS3 = "POWER OFF";
                                        String mS4 = "NO DISPLAY";

                                        boolean meterSt = false;
                                        if (meterStatus.equals(mS1)) {
                                            meterSt = true;
                                            System.out.println("LOCK status found");
                                        }
                                        if (meterStatus.equals(mS2)) {
                                            meterSt = true;
                                            System.out.println("NO METER status found");
                                        }
                                        if (meterStatus.equals(mS3)) {
                                            meterSt = true;
                                            System.out.println("POWER OFF status found");
                                        }
                                        if (meterStatus.equals(mS4)) {
                                            meterSt = true;
                                            System.out.println("NO DISPLAY status found");
                                        }
                                        if (meterReading != null) {
                                            System.out.println("Meter Reading is not null");
                                        }
                                        int connection_code = 0;
                                        String book_no_del = null;

                                        if (meterReading != null || meterSt) {
                                            try {

                                                jsonObject2.put("BOOK_NO", c1.getString(c1.getColumnIndex(DbConnection.BOOK_NO)));
                                                book_no_del = c1.getString(c1.getColumnIndex(DbConnection.BOOK_NO));
                                                jsonObject2.put("ROUTE_NO", c1.getString(c1.getColumnIndex(DbConnection.ROUTE_NO)));
                                                jsonObject2.put("SC_NO", c1.getInt(c1.getColumnIndex(DbConnection.SC_NO)));
                                                connection_code = c1.getInt(c1.getColumnIndex(DbConnection.SC_NO));
                                                jsonObject2.put("DATE", c1.getString(c1.getColumnIndex(DbConnection.DATE)));
                                                String meter_r_val = c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                                if (meter_r_val != null) {
                                                    jsonObject2.put("METER_READING", meter_r_val);
                                                } else {
                                                    jsonObject2.put("METER_READING", "NOT_SUCCESSFUL");
                                                }

                                                jsonObject2.put("REMARK1", c1.getString(c1.getColumnIndex(DbConnection.REMARK1)));
                                                jsonObject2.put("STATUS", c1.getString(c1.getColumnIndex(DbConnection.STATUS)));
                                                jsonObject2.put("METER_TYPE", c1.getString(c1.getColumnIndex(DbConnection.METER_TYPE)));
                                                jsonObject2.put("REMARKS", c1.getString(c1.getColumnIndex(DbConnection.REMARKS)));
                                                String user = c1.getString(c1.getColumnIndex(DbConnection.USER));
                                                if (user != null) {
                                                    jsonObject2.put("USER", user);
                                                } else {
                                                    jsonObject2.put("USER", "unavailable");
                                                }

                                                byte[] bytes = c1.getBlob(c1.getColumnIndex(DbConnection.IMAGE));
                                                String bytetoBase64string;
                                                if (bytes != null) {
                                                    //bytesToString = new String(bytes);
                                                    bytetoBase64string = Base64.encodeToString(bytes, Base64.DEFAULT);
                                                } else {
                                                    //bytesToString = "unavailable";
                                                    bytetoBase64string = "unavailable";
                                                }
                                                jsonObject2.put("IMAGE", bytetoBase64string);

                                                ///*////////////////////// Commented as it is not required ///////////////////
                                                jsonObject2.put("MONTH",month);
                                                jsonObject2.put("YEAR",year);
                                                //*///////////////////////////////////////////////////////////////////////////


                                                String location = c1.getString(c1.getColumnIndex(DbConnection.FEEDER_LOCATION));
                                                if (location != null) {
                                                    jsonObject2.put("LOCATION", location);
                                                } else {
                                                    jsonObject2.put("LOCATION", "unavailable");
                                                }

                                                jsona_Upload_data.put(jsonObject2);








/*///////////////// commented to check code for HttpUrlConnection
                                                StringEntity stringEntity2 = new StringEntity(jsona_Upload_data.toString());

                                                stringEntity2.setContentEncoding("UTF-8");
                                                stringEntity2.setContentType("application/json");



                                                Log.d("String Entity data", "::::::::::" + stringEntity2);
                                                Log.d("String Entity data", "::::::::::" + stringEntity2.toString());


                                                HttpPost httpPostDeb4 = new HttpPost(url2);
                                                HttpResponse httpResponseDeb4;



                                                httpPostDeb4.setEntity(stringEntity2);
                                                httpPostDeb4.addHeader("Accept", "application/json");


                                                DefaultHttpClient defaultHttpClientDeb4 = new DefaultHttpClient();

                                                httpResponseDeb4 = defaultHttpClientDeb4.execute(httpPostDeb4);

                                                InputStream inputStreamDeb4 = httpResponseDeb4.getEntity().getContent();


                                                String serverResponseDeb4 = null;
                                                if (inputStreamDeb4 != null) {
                                                    serverResponseDeb4 = convertInputStreamToString(inputStreamDeb4);
                                                }

                                                Integer response4 = Integer.parseInt(serverResponseDeb4);

*///////////////////


                                                URL url_http2=new URL(url2);
                                                HttpURLConnection httpURLConnection2= (HttpURLConnection) url_http2.openConnection();
                                                httpURLConnection2.setDoOutput(true);
                                                httpURLConnection2.setRequestMethod("POST");
                                                httpURLConnection2.setRequestProperty("Content-Type", "application/json");
                                                OutputStream os2 = httpURLConnection2.getOutputStream();
                                                String a2=String.valueOf(jsona_Upload_data);
                                                os2.write(a2.getBytes());
                                                os2.close();
                                                os2.flush();

                                                BufferedReader br2 = new BufferedReader(new InputStreamReader(
                                                        (httpURLConnection2.getInputStream())));


                                                String serverResponse2="";
                                                String serRes="";
                                                System.out.println("Output from Server .... \n");
                                                while ((serverResponse2 = br2.readLine()) != null) {
                                                    serRes += serverResponse2;
                                                    System.out.println(serverResponse2);
                                                }
                                                httpURLConnection2.disconnect();
                                                //Integer response4 = Integer.parseInt(serverResponse2);
                                                Integer response4 = Integer.parseInt(serRes);




                                                if (response4.equals(0)) {
                                                    //   System.out.println("Upload not successful" + "\t" + counter);
                                                }
                                                if (response4.equals(1)) {
                                                    //  dbConnection.deleteMeterReadingBySC_NO(connection_code,book_no_del);
                                                    ++counter;
                                                    //   c1=dbConnection.retriveDataByRoute(book);
                                                    //   System.out.println("Suuccesful upload" + "\t" + counter);
                                                }


                                                output = serverResponse2;
                                                //output = serverResponseDeb4;
                                                



                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }




                                }while(c1.moveToNext());

                                    /*final int temp_no_consumers = c1.getCount(), no_of_pairs95, no_of_odd;
                                    no_of_pairs95 = temp_no_consumers / 95;
                                    no_of_odd = temp_no_consumers % 100;

                                    for (int i = 0; i < no_of_pairs95; i++) {

                                        int x = (i * 95) + 95;

                                        for (int u = (i * 95); u < x; u++) {
                                            c1.moveToPosition(u);




                                            ++db_increment;
                                            JSONArray jsona_Upload_data = new JSONArray();
                                            JSONObject jsonObject2 = new JSONObject();

                                            String meterReading = c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                            String meterStatus = "";
                                            meterStatus = c1.getString(c1.getColumnIndex(DbConnection.STATUS)).toString();
                                            System.out.println("Meter Status:::" + meterStatus);
                                            System.out.println("MeterReading:::" + meterReading);
                                            String mS1 = "LOCK";
                                            String mS2 = "NO METER";
                                            String mS3 = "POWER OFF";
                                            String mS4 = "NO DISPLAY";

                                            boolean meterSt = false;
                                            if (meterStatus.equals(mS1)) {
                                                meterSt = true;
                                                System.out.println("LOCK status found");
                                            }
                                            if (meterStatus.equals(mS2)) {
                                                meterSt = true;
                                                System.out.println("NO METER status found");
                                            }
                                            if (meterStatus.equals(mS3)) {
                                                meterSt = true;
                                                System.out.println("POWER OFF status found");
                                            }
                                            if (meterStatus.equals(mS4)) {
                                                meterSt = true;
                                                System.out.println("NO DISPLAY status found");
                                            }
                                            if (meterReading != null) {
                                                System.out.println("Meter Reading is not null");
                                            }
                                            int connection_code = 0;
                                            String book_no_del = null;

                                            if (meterReading != null || meterSt) {
                                                try {

                                                    jsonObject2.put("BOOK_NO", c1.getString(c1.getColumnIndex(DbConnection.BOOK_NO)));
                                                    book_no_del = c1.getString(c1.getColumnIndex(DbConnection.BOOK_NO));
                                                    jsonObject2.put("ROUTE_NO", c1.getString(c1.getColumnIndex(DbConnection.ROUTE_NO)));
                                                    jsonObject2.put("SC_NO", c1.getInt(c1.getColumnIndex(DbConnection.SC_NO)));
                                                    connection_code = c1.getInt(c1.getColumnIndex(DbConnection.SC_NO));
                                                    jsonObject2.put("DATE", c1.getString(c1.getColumnIndex(DbConnection.DATE)));
                                                    String meter_r_val = c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                                    if (meter_r_val != null) {
                                                        jsonObject2.put("METER_READING", meter_r_val);
                                                    } else {
                                                        jsonObject2.put("METER_READING", "NOT_SUCCESSFUL");
                                                    }

                                                    jsonObject2.put("REMARK1", c1.getString(c1.getColumnIndex(DbConnection.REMARK1)));
                                                    jsonObject2.put("STATUS", c1.getString(c1.getColumnIndex(DbConnection.STATUS)));
                                                    jsonObject2.put("METER_TYPE", c1.getString(c1.getColumnIndex(DbConnection.METER_TYPE)));
                                                    jsonObject2.put("REMARKS", c1.getString(c1.getColumnIndex(DbConnection.REMARKS)));
                                                    String user = c1.getString(c1.getColumnIndex(DbConnection.USER));
                                                    if (user != null) {
                                                        jsonObject2.put("USER", user);
                                                    } else {
                                                        jsonObject2.put("USER", "unavailable");
                                                    }

                                                    byte[] bytes = c1.getBlob(c1.getColumnIndex(DbConnection.IMAGE));
                                                    String bytetoBase64string;
                                                    if (bytes != null) {
                                                        //bytesToString = new String(bytes);
                                                        bytetoBase64string = Base64.encodeToString(bytes, Base64.DEFAULT);
                                                    } else {
                                                        //bytesToString = "unavailable";
                                                        bytetoBase64string = "unavailable";
                                                    }
                                                    jsonObject2.put("IMAGE", bytetoBase64string);
                                                    String location = c1.getString(c1.getColumnIndex(DbConnection.FEEDER_LOCATION));
                                                    if (location != null) {
                                                        jsonObject2.put("LOCATION", location);
                                                    } else {
                                                        jsonObject2.put("LOCATION", "unavailable");
                                                    }

                                                    jsona_Upload_data.put(jsonObject2);



                                                    StringEntity stringEntity2 = new StringEntity(jsona_Upload_data.toString());

                                                    stringEntity2.setContentEncoding("UTF-8");
                                                    stringEntity2.setContentType("application/json");



                                                    Log.d("String Entity data", "::::::::::" + stringEntity2);
                                                    Log.d("String Entity data", "::::::::::" + stringEntity2.toString());






                                                    HttpPost httpPostDeb4 = new HttpPost(url2);
                                                    HttpResponse httpResponseDeb4;



                                                    httpPostDeb4.setEntity(stringEntity2);
                                                    httpPostDeb4.addHeader("Accept", "application/json");


                                                    DefaultHttpClient defaultHttpClientDeb4 = new DefaultHttpClient();

                                                    httpResponseDeb4 = defaultHttpClientDeb4.execute(httpPostDeb4);




                                                    InputStream inputStreamDeb4 = httpResponseDeb4.getEntity().getContent();

                                                    String serverResponseDeb4 = null;
                                                    if (inputStreamDeb4 != null) {
                                                        serverResponseDeb4 = convertInputStreamToString(inputStreamDeb4);
                                                    }

                                                    Integer response4 = Integer.parseInt(serverResponseDeb4);


                                                    if (response4.equals(0)) {
                                                        //   System.out.println("Upload not successful" + "\t" + counter);
                                                    }
                                                    if (response4.equals(1)) {
                                                        //  dbConnection.deleteMeterReadingBySC_NO(connection_code,book_no_del);
                                                        ++counter;
                                                        //   c1=dbConnection.retriveDataByRoute(book);
                                                        //   System.out.println("Suuccesful upload" + "\t" + counter);
                                                    }


                                                    Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");

                                                    output = serverResponseDeb4;
                                                    Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");

                                                    Log.d("Response for number of successful upload:", ":::::::" + serverResponse + ":::::");



                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        }


                                    }



                                    int te1 = (no_of_pairs95 * 100) + no_of_odd;



                                    for (int u = (no_of_pairs95 * 95); u < (te1+1); u++) {
                                        HttpPost httpPostDeb5 = new HttpPost(url2);
                                        HttpResponse httpResponseDeb5;


                                        c1.moveToPosition(u);
                                        ++db_increment;
                                        JSONArray jsona_Upload_data = new JSONArray();
                                        JSONObject jsonObject2 = new JSONObject();

                                        String meterReading = c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                        String meterStatus = "";
                                        meterStatus = c1.getString(c1.getColumnIndex(DbConnection.STATUS)).toString();
                                        System.out.println("Meter Status:::" + meterStatus);
                                        System.out.println("MeterReading:::" + meterReading);
                                        String mS1 = "LOCK";
                                        String mS2 = "NO METER";
                                        String mS3 = "POWER OFF";
                                        String mS4 = "NO DISPLAY";

                                        boolean meterSt = false;
                                        if (meterStatus.equals(mS1)) {
                                            meterSt = true;
                                            System.out.println("LOCK status found");
                                        }
                                        if (meterStatus.equals(mS2)) {
                                            meterSt = true;
                                            System.out.println("NO METER status found");
                                        }
                                        if (meterStatus.equals(mS3)) {
                                            meterSt = true;
                                            System.out.println("POWER OFF status found");
                                        }
                                        if (meterStatus.equals(mS4)) {
                                            meterSt = true;
                                            System.out.println("NO DISPLAY status found");
                                        }
                                        if (meterReading != null) {
                                            System.out.println("Meter Reading is not null");
                                        }
                                        int connection_code = 0;
                                        String book_no_del = null;

                                        if (meterReading != null || meterSt) {
                                            try {

                                                jsonObject2.put("BOOK_NO", c1.getString(c1.getColumnIndex(DbConnection.BOOK_NO)));
                                                book_no_del = c1.getString(c1.getColumnIndex(DbConnection.BOOK_NO));
                                                jsonObject2.put("ROUTE_NO", c1.getString(c1.getColumnIndex(DbConnection.ROUTE_NO)));
                                                jsonObject2.put("SC_NO", c1.getInt(c1.getColumnIndex(DbConnection.SC_NO)));
                                                connection_code = c1.getInt(c1.getColumnIndex(DbConnection.SC_NO));
                                                jsonObject2.put("DATE", c1.getString(c1.getColumnIndex(DbConnection.DATE)));
                                                String meter_r_val = c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                                if (meter_r_val != null) {
                                                    jsonObject2.put("METER_READING", meter_r_val);
                                                } else {
                                                    jsonObject2.put("METER_READING", "NOT_SUCCESSFUL");
                                                }

                                                jsonObject2.put("REMARK1", c1.getString(c1.getColumnIndex(DbConnection.REMARK1)));
                                                jsonObject2.put("STATUS", c1.getString(c1.getColumnIndex(DbConnection.STATUS)));
                                                jsonObject2.put("METER_TYPE", c1.getString(c1.getColumnIndex(DbConnection.METER_TYPE)));
                                                jsonObject2.put("REMARKS", c1.getString(c1.getColumnIndex(DbConnection.REMARKS)));
                                                String user = c1.getString(c1.getColumnIndex(DbConnection.USER));
                                                if (user != null) {
                                                    jsonObject2.put("USER", user);
                                                } else {
                                                    jsonObject2.put("USER", "unavailable");
                                                }

                                                byte[] bytes = c1.getBlob(c1.getColumnIndex(DbConnection.IMAGE));
                                                String bytetoBase64string;
                                                if (bytes != null) {
                                                    //bytesToString = new String(bytes);
                                                    bytetoBase64string = Base64.encodeToString(bytes, Base64.DEFAULT);
                                                } else {
                                                    //bytesToString = "unavailable";
                                                    bytetoBase64string = "unavailable";
                                                }
                                                jsonObject2.put("IMAGE", bytetoBase64string);
                                                String location = c1.getString(c1.getColumnIndex(DbConnection.FEEDER_LOCATION));
                                                if (location != null) {
                                                    jsonObject2.put("LOCATION", location);
                                                } else {
                                                    jsonObject2.put("LOCATION", "unavailable");
                                                }

                                                jsona_Upload_data.put(jsonObject2);



                                                StringEntity stringEntity2 = new StringEntity(jsona_Upload_data.toString());

                                                stringEntity2.setContentEncoding("UTF-8");
                                                stringEntity2.setContentType("application/json");



                                                httpPostDeb5.setEntity(stringEntity2);
                                                httpPostDeb5.addHeader("Accept", "application/json");


                                                DefaultHttpClient defaultHttpClientDeb5 = new DefaultHttpClient();

                                                httpResponseDeb5 = defaultHttpClientDeb5.execute(httpPostDeb5);



                                                InputStream inputStreamDeb5 = httpResponseDeb5.getEntity().getContent();
                                                String serverResponseDeb = null;
                                                if (inputStreamDeb5 != null) {
                                                    serverResponseDeb = convertInputStreamToString(inputStreamDeb5);
                                                }

                                                Integer response = Integer.parseInt(serverResponseDeb);





                                                if (response.equals(0)) {
                                                    //  System.out.println("Upload not successful" + "\t" + counter);
                                                }
                                                if (response.equals(1)) {
                                                    //  dbConnection.deleteMeterReadingBySC_NO(connection_code,book_no_del);
                                                    ++counter;
                                                    //   c1=dbConnection.retriveDataByRoute(book);
                                                    //  System.out.println("Suuccesful upload" + "\t" + counter);
                                                }



                                                Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");
                                                //  output = serverResponse2;
                                                output = serverResponseDeb;
                                                Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");

                                                Log.d("Response for number of successful upload:", ":::::::" + serverResponse + ":::::");


                                                //jsonArray.put(jsonObject2);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }*/
                                    final Handler handler3 = new Handler(Looper.getMainLooper());   // Looper.getMainLooper returns main thread
                                    handler3.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "MMR Upload Message:" + "\n" + counter + " records get uploaded.",
                                                    Toast.LENGTH_LONG).show();
                                            int id = 1;
                                            Intent intent = new Intent(getApplicationContext(), ElectricityDeptDaman.class);
                                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            builder = new NotificationCompat.Builder(getApplicationContext());
                                            builder.setSmallIcon(R.drawable.notificatioinicon);
                                            builder.setContentTitle("Domestic");
                                            builder.setContentText(counter + " records uploaded");
                                            builder.setContentIntent(pendingIntent);

                                            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                                            inboxStyle.setBigContentTitle("Domestic Upload details:");
                                            String a = "Book No: " + book;
                                            String b = "No. of consumers: " + counter;
                                            ArrayList al = new ArrayList();
                                            al.add(0, a);
                                            al.add(1, b);
                                            for (int i = 0; i < 2; i++) {
                                                inboxStyle.addLine((CharSequence) al.get(i));
                                            }

                                            builder.setStyle(inboxStyle);
                                            notificationManager.notify(id, builder.build());

                                        }
                                    });


                                    output = String.valueOf(counter);
                                    Log.d("Count :::::::::::::::::::::::::::::::::", String.valueOf(counter));


                                    c1.close();


                                    stopSelf();

                                }


                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            //      return output;


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                thread.start();


                int i = 0;

                while (i < 5) {
                    Log.d("************", String.valueOf(i));
                    i++;
                }


            }
        };




        int au = 0;
        Log.d("(##########(", "!!!!!!!!!!!!!above while loop");
        while (au < 1) {
            Log.d("(((((((((((", "!!!!!!!!!!!!!in while loop");
            if (au == 2) {
                Log.d("******", "~~~~~~~~~~~~" + String.valueOf(au));
                handler.removeCallbacks(r);
            } else {
                ++au;
                Log.d("&&&&&&&&&&&", "~~~~~~~~~~~~" + String.valueOf(au));
                handler.post(r);
            }
        }


        return Service.START_NOT_STICKY;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

