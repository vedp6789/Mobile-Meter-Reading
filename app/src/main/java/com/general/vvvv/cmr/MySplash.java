package com.general.vvvv.cmr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.IntentCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.general.SQLite.DbLogin;
import com.remote_connection.InternetConnectivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by comp on 17/09/2014.
 */
public class MySplash extends Activity {
    DbLogin dbLogin;
    SQLiteDatabase db_read, db_write;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        dbLogin = new DbLogin(MySplash.this, DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
        db_read = dbLogin.getReadableDatabase();
        db_write = dbLogin.getWritableDatabase();

        String identifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        if (tm != null) {
            identifier = tm.getDeviceId();
        }
        if (identifier == null || identifier.length() == 0) {
            identifier = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        Toast.makeText(MySplash.this, "Your IMEI no:" + identifier, Toast.LENGTH_LONG).show();

        System.out.println("IMEI no:" + identifier);
        Log.d("IMEI or Android id:", identifier);


        Cursor cursor = dbLogin.getData(db_read);
        if (cursor.getCount() > 0) {
            Intent intent = new Intent(MySplash.this, MyLogin.class);
            startActivity(intent);
        } else {
            Toast.makeText(MySplash.this, "Login database is empty", Toast.LENGTH_SHORT).show();
            if (identifier != null) {
                new AsynchLogin().execute(identifier);
            } else {
                Toast.makeText(MySplash.this, "Unable to find your IMEI no.", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_write.close();
        db_read.close();
    }

    class AsynchLogin extends AsyncTask<String, Void, Boolean> {

        String response1 = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MySplash.this);
            progressDialog.setMessage("On Resume...");
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(false);
                }
            });
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = false;

            //String url="http://117.239.214.2:8087/WSDemo1/resttest/invoke/getlogindetail/"+params[0];
            // String url="http://10.0.2.2:8080/WSDemo1/resttest/invoke/getlogindetail/"+params[0];
            //String url="http://117.239.214.4:8087/WSDemo1/resttest/invoke/getlogindetail/"+params[0];
            InternetConnectivity internetConnectivity = new InternetConnectivity();
            boolean b = internetConnectivity.checkInternetConnection(MySplash.this);
            if (b) {
               String url="http://10.0.2.2:8080/WSDemo1/resttest/invoke/getlogindetail/"+params[0];
                /// String url = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
                // String url = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
                //String url = "http://192.168.100.57:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
                //String url = "http://192.168.1.14:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
                ///String url = "http://192.168.0.102:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
                // String url = "http://192.168.1.2:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
               //String url = "http://192.168.0.100:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
               // String url = "http://192.168.0.100:8081/WSDemo1/resttest/invoke/getlogindetail/" + params[0];
                // String url = "http://192.168.43.80:8080/WSDemo1/resttest/invoke/getlogindetail/" + params[0];

                HttpGet httpGet = new HttpGet(url);
                HttpParams httpParams = new BasicHttpParams();
                DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);
                HttpResponse httpResponse = null;
                HttpEntity httpEntity = null;
                try {
                    httpResponse = defaultHttpClient.execute(httpGet);
                    httpEntity = httpResponse.getEntity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //HttpEntity httpEntity=httpResponse.getEntity();
                //String jsonString;
                try {
                    response1 = EntityUtils.toString(httpEntity);
                    //String jsonString=response1;
                    Log.d("Response:", "....:::::" + response1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response1 != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response1);
                        JSONArray jsonArray = new JSONArray();
                        jsonArray = jsonObject.getJSONArray("Login_data");
                        if (jsonArray.length() > 0) {
                            System.out.println("jsonarray for Login data is available.");
                            int user_id = 0;
                            String license_no = null;
                            String imei = null;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                user_id = jsonObject1.getInt("USER_ID");
                                license_no = jsonObject1.getString("LICENSE_NO");
                                imei = jsonObject1.getString("IMEI_NO");
                            }
                            if (user_id != 0 & license_no != null) {
                                System.out.println("Going to save Login data in database.");
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DbLogin.USER_ID, user_id);
                                contentValues.put(DbLogin.LICENSE_NO, license_no);
                                contentValues.put(DbLogin.IMEI, imei);
                                result = dbLogin.insertData(contentValues, db_write);
                            }

                        } else {
                            System.out.println("IMEI is not authentic.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    result = false;
                }
            }


            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                progressDialog.dismiss();
                Intent intent = new Intent(MySplash.this, MyLogin.class);
                ComponentName cn = intent.getComponent();
                Intent intent1 = IntentCompat.makeRestartActivityTask(cn);
                startActivity(intent1);
                //startActivity(intent);
            } else {
                progressDialog.dismiss();
                Toast.makeText(MySplash.this, "Sorry..." + "\n" + "Internet Connection or IMEI verificaion might be the problem !", Toast.LENGTH_LONG).show();
                onStop();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (progressDialog != null) {
                progressDialog.dismiss();

            }
        }
    }

}
