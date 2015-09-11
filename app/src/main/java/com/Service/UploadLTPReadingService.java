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
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.general.SQLite.DbConnection;
import com.general.SQLite.DbLTPReading;
import com.general.SQLite.DbLogin;
import com.general.SQLite.DbLtpDownloadStatus;
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
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by comp on 16/01/2015.
 */
public class UploadLTPReadingService extends Service {
    DbLTPReading dbLTPReading;
    DbLtpDownloadStatus dbLtpDownloadStatus;

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
        dbLTPReading = new DbLTPReading(context, DbLTPReading.DB_NAME, null, DbLTPReading.DB_VERSION);
        dbLtpDownloadStatus = new DbLtpDownloadStatus(getApplicationContext(),DbLtpDownloadStatus.DB_NAME,null,DbLtpDownloadStatus.DB_VERSION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);
        Toast.makeText(getApplicationContext(), "Trying to upload...", Toast.LENGTH_LONG).show();
        final String area = intent.getStringExtra("AREA");
        c1 = dbLTPReading.retriveDataByAreaCode(area);



        ///*/////////////////////////////////   Commented as month and year is not required to send ////////////////////

        String month_year []=dbLtpDownloadStatus.getMonthAndYear(area);
        month=month_year[0];
        year=month_year[1];
        Toast.makeText(getApplicationContext(),"Area Code:"+area+"\nMonth:"+month+"\n"+"Year:"+year,Toast.LENGTH_LONG).show();
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
                            //192.168.1.14
                            String url1 = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://192.168.1.14:8080/WSDemo1/resttest/invoke/validateUser";
                            /// String url1 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/validateUser";
                            //  String url1 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://192.168.1.2:8080/WSDemo1/resttest/invoke/validateUser";
                            //String url1 = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/validateUser";  // dynamic ip of Rajib sir comp
                            //  String url1 = "http://192.168.0.102:8080/WSDemo1/resttest/invoke/validateUser";
                            // String url1 = "http://192.168.43.80:8080/WSDemo1/resttest/invoke/validateUser";


                            //   String url2 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/newload";
                            String url2 = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/ltpupld";
                            //String url2 = "http://192.168.1.14:8080/WSDemo1/resttest/invoke/ltpupld";
                            // String url2 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/newload";

                           //String url2 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/ltpupld";
                            // String url2 = "http://192.168.43.80:8080/WSDemo1/resttest/invoke/newload";
                            //192.168.1.2
                            //String url2 = "http://192.168.1.2:8080/WSDemo1/resttest/invoke/newload";
                           //String url2 = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/ltpupld";
                            //   String url2 = "http://192.168.0.102:8080/WSDemo1/resttest/invoke/newload";





                            try {

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

                                        String KW = c1.getString(c1.getColumnIndex(DbLTPReading.KW));
                                        String KVA = c1.getString(c1.getColumnIndex(DbLTPReading.KVA));
                                        String KWH = c1.getString(c1.getColumnIndex(DbLTPReading.KWH));
                                        String KVAH = c1.getString(c1.getColumnIndex(DbLTPReading.KVAH));
                                        String KVARH = c1.getString(c1.getColumnIndex(DbLTPReading.KVARH));

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

                                        }
                                        int consumer_id = 0;
                                        String area_no_del = null;

                                        if (complete_reading || meterSt) {
                                            try {

                                                jsonObject2.put("AREA", c1.getString(c1.getColumnIndex(DbLTPReading.AREA)));

                                                area_no_del = c1.getString(c1.getColumnIndex(DbLTPReading.AREA));

                                                jsonObject2.put("LTP_NO", c1.getInt(c1.getColumnIndex(DbLTPReading.LTP_NO)));
                                                consumer_id = c1.getInt(c1.getColumnIndex(DbLTPReading.LTP_NO));
                                                jsonObject2.put("DATE", c1.getString(c1.getColumnIndex(DbLTPReading.DATE)));

                                                String kw = c1.getString(c1.getColumnIndex(DbLTPReading.KW));
                                                if (kw != null) {
                                                    jsonObject2.put("KW",kw );
                                                } else {
                                                    jsonObject2.put("KW", "NOT_SUCCESSFUL");
                                                }

                                                String kva = c1.getString(c1.getColumnIndex(DbLTPReading.KVA));
                                                if (kva != null) {
                                                    jsonObject2.put("KVA",kva );
                                                } else {
                                                    jsonObject2.put("KVA", "NOT_SUCCESSFUL");
                                                }
                                                String kwh = c1.getString(c1.getColumnIndex(DbLTPReading.KWH));
                                                if (kwh != null) {
                                                    jsonObject2.put("KWH",kwh );
                                                } else {
                                                    jsonObject2.put("KWH", "NOT_SUCCESSFUL");
                                                }
                                                String kvah = c1.getString(c1.getColumnIndex(DbLTPReading.KVAH));
                                                if (kvah != null) {
                                                    jsonObject2.put("KVAH",kvah );
                                                } else {
                                                    jsonObject2.put("KVAH", "NOT_SUCCESSFUL");
                                                }
                                                String kvarh = c1.getString(c1.getColumnIndex(DbLTPReading.KVARH));
                                                if (kvarh != null) {
                                                    jsonObject2.put("KVARH",kvarh );
                                                } else {
                                                    jsonObject2.put("KVARH", "NOT_SUCCESSFUL");
                                                }

                                                String zone1 = c1.getString(c1.getColumnIndex(DbLTPReading.ZONE_1));
                                                if (zone1 != null) {
                                                    jsonObject2.put("ZONE1",zone1 );
                                                } else {
                                                    jsonObject2.put("ZONE1", "NOT_SUCCESSFUL");
                                                }

                                                String zone2 = c1.getString(c1.getColumnIndex(DbLTPReading.ZONE_2));
                                                if (zone2 != null) {
                                                    jsonObject2.put("ZONE2",zone2 );
                                                } else {
                                                    jsonObject2.put("ZONE2", "NOT_SUCCESSFUL");
                                                }

                                                String zone3 = c1.getString(c1.getColumnIndex(DbLTPReading.ZONE_3));
                                                if (zone3 != null) {
                                                    jsonObject2.put("ZONE3",zone3 );
                                                } else {
                                                    jsonObject2.put("ZONE3", "NOT_SUCCESSFUL");
                                                }


                                                jsonObject2.put("REMARK1", c1.getString(c1.getColumnIndex(DbLTPReading.REMARK1)));
                                                jsonObject2.put("STATUS", c1.getString(c1.getColumnIndex(DbLTPReading.STATUS)));
                                                jsonObject2.put("METER_TYPE", c1.getString(c1.getColumnIndex(DbLTPReading.METER_TYPE)));
                                                jsonObject2.put("REMARKS", c1.getString(c1.getColumnIndex(DbLTPReading.REMARKS)));
                                                String user = c1.getString(c1.getColumnIndex(DbLTPReading.USER));
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
                                                jsonObject2.put("MONTH",month);
                                                jsonObject2.put("YEAR",year);
                                                //*///////////////////////////////////////////////////////////////////////////


                                                String location = c1.getString(c1.getColumnIndex(DbLTPReading.FEEDER_LOCATION));
                                                if (location != null) {
                                                    jsonObject2.put("LOCATION", location);
                                                } else {
                                                    jsonObject2.put("LOCATION", "unavailable");
                                                }

                                                jsona_Upload_data.put(jsonObject2);









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


                                    final Handler handler3 = new Handler(Looper.getMainLooper());   // Looper.getMainLooper returns main thread
                                    handler3.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "LTP Upload Message:" + "\n" + counter + " records get uploaded.",
                                                    Toast.LENGTH_LONG).show();
                                            int id = 1;
                                            Intent intent = new Intent(getApplicationContext(), ElectricityDeptDaman.class);
                                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            builder = new NotificationCompat.Builder(getApplicationContext());
                                            builder.setSmallIcon(R.drawable.notificatioinicon);
                                            builder.setContentTitle("LTP");
                                            builder.setContentText(counter + " records uploaded");
                                            builder.setContentIntent(pendingIntent);

                                            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                                            inboxStyle.setBigContentTitle("LTP Upload details:");
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

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
