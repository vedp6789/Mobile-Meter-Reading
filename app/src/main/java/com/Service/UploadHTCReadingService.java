package com.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.general.SQLite.DbHTCReading;
import com.general.SQLite.DbHtcDownloadStatus;
import com.general.SQLite.DbLTPReading;
import com.general.SQLite.DbLogin;
import com.general.SQLite.DbLtpDownloadStatus;
import com.general.vvvv.cmr.ElectricityDeptDaman;
import com.general.vvvv.cmr.R;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vvvv on 28-01-2015.
 */
public class UploadHTCReadingService extends Service {

    DbHTCReading dbHTCReading;
    DbHtcDownloadStatus dbHtcDownloadStatus;

    SQLiteDatabase sqLiteDatabase2;
    Context context = this;
    Cursor c1;
    static int counter;
    NotificationCompat.Builder builder;
    static NotificationManager notificationManager;
    static String month, year;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        dbHTCReading = new DbHTCReading(context, DbHTCReading.DB_NAME, null, DbHTCReading.DB_VERSION);
        dbHtcDownloadStatus = new DbHtcDownloadStatus(getApplicationContext(), DbHtcDownloadStatus.DB_NAME, null, DbHtcDownloadStatus.DB_VERSION);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Toast.makeText(getApplicationContext(), "Trying to upload...", Toast.LENGTH_LONG).show();
        final String area = intent.getStringExtra("AREA");
        c1 = dbHTCReading.retriveDataByAreaCode(area);

        String month_year[] = dbHtcDownloadStatus.getMonthAndYear(area);
        month = month_year[0];
        year = month_year[1];
        Toast.makeText(getApplicationContext(), "Area Code:" + area + "\nMonth:" + month + "\n" + "Year:" + year, Toast.LENGTH_LONG).show();


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
                            //String url1 = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/validateUser";
                            /// String url1 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/validateUser";
                            //  String url1 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://192.168.1.2:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/validateUser";  // dynamic ip of Rajib sir comp
                            String url1 = "http://192.168.0.100:8081/WSDemo1/resttest/invoke/validateUser";  // dynamic ip of Rajib sir comp
                            //  String url1 = "http://192.168.0.102:8080/WSDemo1/resttest/invoke/validateUser";
                            // String url1 = "http://192.168.43.80:8080/WSDemo1/resttest/invoke/validateUser";


                            //   String url2 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/newload";
                            //String url2 = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/ltpupld";
                            // String url2 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/newload";

                            //String url2 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/htcupld";
                            // String url2 = "http://192.168.43.80:8080/WSDemo1/resttest/invoke/newload";
                            //192.168.1.2
                            //String url2 = "http://192.168.1.2:8080/WSDemo1/resttest/invoke/newload";
                            //String url2 = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/htcupld";
                            String url2 = "http://192.168.0.100:8081/WSDemo1/resttest/invoke/htcupld";
                            //   String url2 = "http://192.168.0.102:8080/WSDemo1/resttest/invoke/newload";


                            try {

                                URL url_http1 = new URL(url1);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url_http1.openConnection();
                                httpURLConnection.setDoOutput(true);
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                                OutputStream os = httpURLConnection.getOutputStream();
                                String a = String.valueOf(jsona_UserImei);

                                os.write(a.getBytes());
                                os.close();
                                os.flush();

                                BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));


                                String sr = "";
                                System.out.println("Output from Server .... \n");
                                String line = "";

                                while ((line = br.readLine()) != null)
                                    sr += line;
                                   /* while ((sr = br.readLine()) != null) {
                                        sr=br.readLine();
                                        System.out.println("Response:"+sr);
                                    }*/
                                System.out.println("Validation response from server:" + sr);
                                Integer ip = Integer.parseInt(sr);
                                System.out.println("Output from Server \n" + sr);
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
                                                    "Validation denied for this IMEI No." + "\nContact software provider",
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

/*                                    String KW_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KW_MM));
                                    String KVA_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KVA_MM));
                                    String KWH_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KWH_MM));
                                    String KVAH_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KVAH_MM));
                                    String KVARH_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KVARH_MM));

                                    String meterStatus = "";
                                    meterStatus = c1.getString(c1.getColumnIndex(DbLTPReading.STATUS)).toString();
                                    System.out.println("Meter Status:::" + meterStatus);
                                    System.out.println("KW:::" + KW);

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
                                    boolean complete_reading=false;
                                    if ((KW != null) && (KVA != null) && (KWH != null) && (KVAH != null) && (KVARH != null)) {
                                        System.out.println("Meter Reading for LTP is not found null for any meter reading");
                                        complete_reading=true;

                                    }*/
                                        boolean allow_data_upload = false;
                                        String KW_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KW_MM));
                                        String KVA_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KVA_MM));
                                        String KWH_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KWH_MM));
                                        String KVAH_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KVAH_MM));
                                        String KVARH_MM = c1.getString(c1.getColumnIndex(DbHTCReading.KVARH_MM));

                                        String KWH_CM = c1.getString(c1.getColumnIndex(DbHTCReading.KWH_CM));

                                        String meter_Status_MM = "";
                                        String meter_Status_CM = "";
                                        meter_Status_MM = c1.getString(c1.getColumnIndex(DbHTCReading.STATUS_MM));
                                        meter_Status_CM = c1.getString(c1.getColumnIndex(DbHTCReading.STATUS_CM));
                                        boolean b1 = false, b2 = false;
                                        String m_St = "LOCK";
                                        String m_St1 = "NO METER";
                                        String m_St2 = "POWER OFF";
                                        String m_St3 = "NO DISPLAY";
                                        if ((meter_Status_MM.equals(m_St)) || (meter_Status_MM.equals(m_St1)) || (meter_Status_MM.equals(m_St2)) || (meter_Status_MM.equals(m_St3))) {
                                            b1 = true;
                                        }
                                        if ((meter_Status_CM.equals(m_St)) || (meter_Status_CM.equals(m_St1)) || (meter_Status_CM.equals(m_St2)) || (meter_Status_CM.equals(m_St3))) {
                                            b2 = true;
                                        }
                                        if ((((KW_MM != null) && (KVA_MM != null) && (KWH_MM != null) && (KVAH_MM != null) && (KVARH_MM != null)) || b1) || ((KWH_CM != null) || b2)) {
                                            allow_data_upload = true;

                                        }

                                        int consumer_id = 0;
                                        String area_no_del = null;

                                        if (allow_data_upload) {
                                            try {

                                                jsonObject2.put("AREA", c1.getString(c1.getColumnIndex(DbHTCReading.AREA)));

                                                area_no_del = c1.getString(c1.getColumnIndex(DbHTCReading.AREA));

                                                jsonObject2.put("HTC_NO", c1.getInt(c1.getColumnIndex(DbHTCReading.HTC_NO)));
                                                consumer_id = c1.getInt(c1.getColumnIndex(DbHTCReading.HTC_NO));
                                                jsonObject2.put("DATE", c1.getString(c1.getColumnIndex(DbHTCReading.DATE)));

                                                String kw_mm = c1.getString(c1.getColumnIndex(DbHTCReading.KW_MM));
                                                if (kw_mm != null) {
                                                    jsonObject2.put("KW_MM", kw_mm);
                                                } else {
                                                    jsonObject2.put("KW_MM", "NA");
                                                }

                                                String kva_mm = c1.getString(c1.getColumnIndex(DbHTCReading.KVA_MM));
                                                if (kva_mm != null) {
                                                    jsonObject2.put("KVA_MM", kva_mm);
                                                } else {
                                                    jsonObject2.put("KVA_MM", "NA");
                                                }
                                                String kwh_mm = c1.getString(c1.getColumnIndex(DbHTCReading.KWH_MM));
                                                if (kwh_mm != null) {
                                                    jsonObject2.put("KWH_MM", kwh_mm);
                                                } else {
                                                    jsonObject2.put("KWH_MM", "NA");
                                                }
                                                String kvah_mm = c1.getString(c1.getColumnIndex(DbHTCReading.KVAH_MM));
                                                if (kvah_mm != null) {
                                                    jsonObject2.put("KVAH_MM", kvah_mm);
                                                } else {
                                                    jsonObject2.put("KVAH_MM", "NA");
                                                }
                                                String kvarh_mm = c1.getString(c1.getColumnIndex(DbHTCReading.KVARH_MM));
                                                if (kvarh_mm != null) {
                                                    jsonObject2.put("KVARH_MM", kvarh_mm);
                                                } else {
                                                    jsonObject2.put("KVARH_MM", "NA");
                                                }


                                                String kw_cm = c1.getString(c1.getColumnIndex(DbHTCReading.KW_CM));
                                                if (kw_cm != null) {
                                                    jsonObject2.put("KW_CM", kw_cm);
                                                } else {
                                                    jsonObject2.put("KW_CM", "NA");
                                                }

                                                String kva_cm = c1.getString(c1.getColumnIndex(DbHTCReading.KVA_CM));
                                                if (kva_cm != null) {
                                                    jsonObject2.put("KVA_CM", kva_cm);
                                                } else {
                                                    jsonObject2.put("KVA_CM", "NA");
                                                }
                                                String kwh_cm = c1.getString(c1.getColumnIndex(DbHTCReading.KWH_CM));
                                                if (kwh_cm != null) {
                                                    jsonObject2.put("KWH_CM", kwh_cm);
                                                } else {
                                                    jsonObject2.put("KWH_CM", "NA");
                                                }
                                                String kvah_cm = c1.getString(c1.getColumnIndex(DbHTCReading.KVAH_CM));
                                                if (kvah_cm != null) {
                                                    jsonObject2.put("KVAH_CM", kvah_cm);
                                                } else {
                                                    jsonObject2.put("KVAH_CM", "NA");
                                                }
                                                String kvarh_cm = c1.getString(c1.getColumnIndex(DbHTCReading.KVARH_CM));
                                                if (kvarh_cm != null) {
                                                    jsonObject2.put("KVARH_CM", kvarh_cm);
                                                } else {
                                                    jsonObject2.put("KVARH_CM", "NA");
                                                }


                                                String zone1_mm = c1.getString(c1.getColumnIndex(DbHTCReading.ZONE_1_MM));
                                                if (zone1_mm != null) {
                                                    jsonObject2.put("ZONE_1_MM", zone1_mm);
                                                } else {
                                                    jsonObject2.put("ZONE_1_MM", "NA");
                                                }

                                                String zone2_mm = c1.getString(c1.getColumnIndex(DbHTCReading.ZONE_2_MM));
                                                if (zone2_mm != null) {
                                                    jsonObject2.put("ZONE_2_MM", zone2_mm);
                                                } else {
                                                    jsonObject2.put("ZONE_2_MM", "NA");
                                                }

                                                String zone3_mm = c1.getString(c1.getColumnIndex(DbHTCReading.ZONE_3_MM));
                                                if (zone3_mm != null) {
                                                    jsonObject2.put("ZONE_3_MM", zone3_mm);
                                                } else {
                                                    jsonObject2.put("ZONE_3_MM", "NA");
                                                }


                                                jsonObject2.put("REMARK1_MM", c1.getString(c1.getColumnIndex(DbHTCReading.REMARK1_MM)));
                                                jsonObject2.put("STATUS_MM", c1.getString(c1.getColumnIndex(DbHTCReading.STATUS_MM)));
                                                jsonObject2.put("METER_TYPE_MM", c1.getString(c1.getColumnIndex(DbHTCReading.METER_TYPE_MM)));
                                                jsonObject2.put("REMARKS_MM", c1.getString(c1.getColumnIndex(DbHTCReading.REMARKS_MM)));
                                                jsonObject2.put("REMARK1_CM", c1.getString(c1.getColumnIndex(DbHTCReading.REMARK1_CM)));
                                                jsonObject2.put("STATUS_CM", c1.getString(c1.getColumnIndex(DbHTCReading.STATUS_CM)));
                                                jsonObject2.put("METER_TYPE_CM", c1.getString(c1.getColumnIndex(DbHTCReading.METER_TYPE_CM)));
                                                jsonObject2.put("REMARKS_CM", c1.getString(c1.getColumnIndex(DbHTCReading.REMARKS_CM)));

                                                String user = c1.getString(c1.getColumnIndex(DbHTCReading.USER));
                                                if (user != null) {
                                                    jsonObject2.put("USER", user);
                                                } else {
                                                    jsonObject2.put("USER", "unavailable");
                                                }

                                                /*byte[] bytes = c1.getBlob(c1.getColumnIndex(DbConnection.IMAGE));
                                                String bytetoBase64string;
                                                if (bytes != null) {
                                                    //bytesToString = new String(bytes);
                                                    bytetoBase64string = Base64.encodeToString(bytes, Base64.DEFAULT);
                                                } else {
                                                    //bytesToString = "unavailable";
                                                    bytetoBase64string = "unavailable";
                                                }
                                                jsonObject2.put("IMAGE", bytetoBase64string);*/

                                                ///*////////////////////// Commented as it is not required ///////////////////
                                                jsonObject2.put("MONTH", month);
                                                jsonObject2.put("YEAR", year);
                                                //*///////////////////////////////////////////////////////////////////////////


                                                String location = c1.getString(c1.getColumnIndex(DbHTCReading.FEEDER_LOCATION));
                                                if (location != null) {
                                                    jsonObject2.put("LOCATION", location);
                                                } else {
                                                    jsonObject2.put("LOCATION", "unavailable");
                                                }

                                                jsona_Upload_data.put(jsonObject2);


                                                URL url_http2 = new URL(url2);
                                                HttpURLConnection httpURLConnection2 = (HttpURLConnection) url_http2.openConnection();
                                                httpURLConnection2.setDoOutput(true);
                                                httpURLConnection2.setRequestMethod("POST");
                                                httpURLConnection2.setRequestProperty("Content-Type", "application/json");
                                                OutputStream os2 = httpURLConnection2.getOutputStream();
                                                String a2 = String.valueOf(jsona_Upload_data);
                                                os2.write(a2.getBytes());
                                                os2.close();
                                                os2.flush();

                                                BufferedReader br2 = new BufferedReader(new InputStreamReader(
                                                        (httpURLConnection2.getInputStream())));


                                                String serverResponse2 = "";
                                                String serRes = "";
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


                                    } while (c1.moveToNext());


                                    final Handler handler3 = new Handler(Looper.getMainLooper());   // Looper.getMainLooper returns main thread
                                    handler3.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "HTC Upload Message:" + "\n" + counter + " records get uploaded.",
                                                    Toast.LENGTH_LONG).show();
                                            int id = 1;
                                            Intent intent = new Intent(getApplicationContext(), ElectricityDeptDaman.class);
                                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            builder = new NotificationCompat.Builder(getApplicationContext());
                                            builder.setSmallIcon(R.drawable.notificatioinicon);
                                            builder.setContentTitle("HTC");
                                            builder.setContentText(counter + " records uploaded");
                                            builder.setContentIntent(pendingIntent);

                                            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                                            inboxStyle.setBigContentTitle("HTC Upload details:");
                                            String a = "Area Code: " + area;
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
}
