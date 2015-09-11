package com.fragments;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Service.UploadMeterReadingService;
import com.general.SQLite.DbConnection;
import com.general.SQLite.DbLogin;
import com.general.vvvv.cmr.R;
import com.remote_connection.InternetConnectivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by comp on 06/09/2014.
 */
public class FDialogUpdate extends DialogFragment implements View.OnClickListener {
    EditText et_book_upload;
    Button b_upload, b_cancel_dialog_upload;
    ProgressDialog progressDialog;
    static String book_no = null;
    DbConnection dbConnection;
    SQLiteDatabase liteDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog_upload, container, false);
        et_book_upload = (EditText) v.findViewById(R.id.et_route_no_upload);
        b_upload = (Button) v.findViewById(R.id.b_upload);
        b_cancel_dialog_upload = (Button) v.findViewById(R.id.b_uploadDialogCancel);

        b_upload.setOnClickListener(this);
        b_cancel_dialog_upload.setOnClickListener(this);

        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.upload_dialog_title));
        dialog.setCanceledOnTouchOutside(false);
        return v;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_upload:
                InternetConnectivity internetConnectivity = new InternetConnectivity();
                Boolean aBoolean = internetConnectivity.checkInternetConnection(getActivity());
                if (aBoolean) {
                    if (et_book_upload.getText().length() != 0) {
                        RetrieveUploadableDataFromLocalDb retrieveUploadableDataFromLocalDb = new RetrieveUploadableDataFromLocalDb();
                        retrieveUploadableDataFromLocalDb.getdatafromDb(String.valueOf(et_book_upload.getText()));
                    } else {
                        Toast.makeText(getActivity(), "Book id please !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No internet connectivity found !", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.b_uploadDialogCancel:
                dismiss();
                break;
        }
    }


    class RetrieveUploadableDataFromLocalDb {
        Context context = getActivity();
        Handler handler=null;
        Runnable runnable=null;
        boolean stopRunnale=false;
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public class MyDbErrorHandler implements DatabaseErrorHandler {
            @Override
            public void onCorruption(SQLiteDatabase sqLiteDatabase) {

            }
        }


        public void getdatafromDb(String book) {
            dbConnection = new DbConnection(getActivity(), DbConnection.DB_NAME, null, DbConnection.DB_VERSION, new MyDbErrorHandler());

            book_no = book;
            liteDatabase = dbConnection.getWritableDatabase();

            Cursor c = dbConnection.retriveDataByBook(book);
            Log.d("Cursor length:", String.valueOf(c.getCount()));
            if (c.getCount() > 0) {
                final Cursor c1 = dbConnection.retriveDataByRoute(book);
                boolean b = checkWhetherAnyConsumerMeterReadingUpdated(c1);
                if (b) {
                    //Toast.makeText(getActivity(),"Waiting to create json data",Toast.LENGTH_LONG).show();


     //// commented as new approach for Handler is to do test               new NewAsyncUpdate().execute(c1);


                    Intent intentUploadMRS =new Intent(getActivity(), UploadMeterReadingService.class);
                    intentUploadMRS.putExtra("DemoT","check");
                    intentUploadMRS.putExtra("BOOK",book);



                    getActivity().startService(intentUploadMRS);
                    dismiss();



     /*               Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                int database_size=c1.getCount(),db_increment=0;
                                String output = null;
                                Integer counter=0;
                                DbLogin dbLogin = new DbLogin(getActivity(), DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
                                SQLiteDatabase sqLiteDatabase = dbLogin.getReadableDatabase();
                                Cursor c_dbLogin = dbLogin.getData(sqLiteDatabase);
                                c_dbLogin.moveToFirst();
                                String imei = c_dbLogin.getString(c_dbLogin.getColumnIndex(DbLogin.IMEI));
                                System.out.println(":::::::::::::::Imei for user"+imei);
                                sqLiteDatabase.close();
                                JSONArray  jsona_UserImei=new JSONArray();
                                JSONObject jsonObjectImei = new JSONObject();
                                try {
                                    jsonObjectImei.put("USER_IMEI", imei);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsona_UserImei.put(jsonObjectImei);

                                //10.0.2.2:8080

                               // String url1 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/validateUser";
                               String url1 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/validateUser";
                               // String url1 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/validateUser";
                              //   String url2 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/newload";
                                 String url2 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/newload";
                                //String url2 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/newload";
                                HttpPost httpPost1 = new HttpPost(url1);
                                HttpPost httpPost2 = new HttpPost(url2);
                                HttpParams httpParams1 = new BasicHttpParams();
                                HttpParams httpParams2 = new BasicHttpParams();
                                DefaultHttpClient defaultHttpClient1 = new DefaultHttpClient(httpParams1);
                                DefaultHttpClient defaultHttpClient2 = new DefaultHttpClient(httpParams2);
                                HttpResponse httpResponse1;
                                HttpResponse httpResponse2;

                                try {

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
                                    Integer validation_denied = 0;
                                    Integer validation_success = 1;
                                    if (user_validation.equals(validation_denied)) {
                                        output = "-1";
                                        Log.d("Validation denied for this IMEI", "Contact software provider");
                                    }
                                    if (user_validation.equals(validation_success)) {
                                        c1.moveToFirst();
                                        do {
                                            ++db_increment;
                                            JSONArray jsona_Upload_data=new JSONArray();
                                            JSONObject jsonObject2 = new JSONObject();

                                            String meterReading = c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                            String meterStatus = "";
                                            meterStatus = c1.getString(c1.getColumnIndex(DbConnection.STATUS)).toString();
                                            System.out.println("Meter Status:::" + meterStatus);
                                            System.out.println("MeterReading:::" + meterReading);
                                            String mS = "LOCK";
                                            boolean meterSt = false;
                                            if (meterStatus.equals(mS)) {
                                                meterSt = true;
                                                System.out.println("LOCK status found");
                                            }
                                            if (meterReading != null) {
                                                System.out.println("Meter Reading is not null");
                                            }

                                            //Log.d("Meter_reading value for SC_NO:"+String.valueOf(c.getInt(c.getColumnIndex(DbConnection.SC_NO))),c.getString(c.getColumnIndex(DbConnection.METER_READING)));
                                            if (meterReading != null || meterSt) {
                                                try {
                                                    System.out.println("Condition::::::::" + meterStatus);
                                                    //jsonObject2.put("S_NO",c.getInt(c.getColumnIndex(DbConnection.S_NO)));
                                                    // Log.d("Meter_reading value for SC_NO:"+String.valueOf(c.getInt(c.getColumnIndex(DbConnection.SC_NO))),c.getString(c.getColumnIndex(DbConnection.METER_READING)));
                                                    jsonObject2.put("BOOK_NO",c1.getString(c1.getColumnIndex(DbConnection.BOOK_NO)));
                                                    jsonObject2.put("ROUTE_NO", c1.getString(c1.getColumnIndex(DbConnection.ROUTE_NO)));
                                                    jsonObject2.put("SC_NO", c1.getInt(c1.getColumnIndex(DbConnection.SC_NO)));

                                                    jsonObject2.put("DATE", c1.getString(c1.getColumnIndex(DbConnection.DATE)));
                                                    String meter_r_val =c1.getString(c1.getColumnIndex(DbConnection.METER_READING));
                                                    if (meter_r_val != null) {
                                                        jsonObject2.put("METER_READING", meter_r_val);
                                                    } else {
                                                        jsonObject2.put("METER_READING", "NOT_SUCCESSFUL");
                                                    }

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


                                                    // Log.d("Json Object in do IN Background", "::::::::" + .toString() + "::::::::");
                                                    StringEntity stringEntity2 = new StringEntity(jsona_Upload_data.toString());
                                                    Log.d("String Entity data", "::::::::::" + stringEntity2);
                                                    Log.d("String Entity data", "::::::::::" + stringEntity2.toString());
                                                    stringEntity2.setContentEncoding("UTF-8");
                                                    stringEntity2.setContentType("application/json");

                                                    httpPost2.setEntity(stringEntity2);
                                                    httpPost2.addHeader("Accept", "application/json");

                                                    Log.d("String Entity data", "::::::::::" + stringEntity2);
                                                    Log.d("String Entity data", "::::::::::" + stringEntity2.toString());

                *//*httpPost.setHeader("Accept","application/json");
                httpPost.setHeader("Content-type","application/json");*//*
                                                    httpResponse2 = defaultHttpClient2.execute(httpPost2);
                                                    // liteDatabase.close();
                                                    InputStream inputStream2 = httpResponse2.getEntity().getContent();
                                                    String serverResponse2 = null;
                                                    if (inputStream2 != null) {
                                                        serverResponse2 = convertInputStreamToString(inputStream2);
                                                    }
                                                    Integer response=Integer.parseInt(serverResponse2);
                                                    if(response.equals(0)){
                                                        System.out.println("Upload not successful"+"\t"+counter);
                                                    }
                                                    if(response.equals(1)){

                                                        ++counter;
                                                        System.out.println("Suuccesful upload"+"\t"+counter);
                                                    }

                                                    // HttpEntity httpEntity=httpResponse.getEntity();
                                                    //response1= EntityUtils.toString(httpEntity);

                                                    Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");
                                                    output = serverResponse2;
                                                    Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");

                                                    Log.d("Response for number of successful upload:", ":::::::" + serverResponse + ":::::");


                                                    //jsonArray.put(jsonObject2);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        } while (c1.moveToNext());

                                        output=String.valueOf(counter);


                                    }


                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (ClientProtocolException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                //      return output;



                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });


                    thread.start();*/


               handler=new Handler();
                  runnable=new Runnable() {
                        @Override
                        public void run() {

                        }
                    };
                handler.postDelayed(runnable,10000);


                } else {
                    Toast.makeText(getActivity(), "Meter reading is not available for any Consumer related to this book!" + "\n" + "Upload get canceled.", Toast.LENGTH_LONG).show();
                    liteDatabase.close();
                }

            } else {
                Toast.makeText(getActivity(), "Book number: " + book_no + " is not available !" + "\n" + "Kindly check your Book number.", Toast.LENGTH_LONG).show();
                liteDatabase.close();
            }


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
        public boolean checkWhetherAnyConsumerMeterReadingUpdated(Cursor cursor) {
            boolean b = false;

            System.out.println("cursor size;;;;" + cursor.getCount());
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                do {
                    String meter_reading_available = cursor.getString(cursor.getColumnIndex(DbConnection.METER_READING));
                    String meter_Status = "";
                    meter_Status = cursor.getString(cursor.getColumnIndex(DbConnection.STATUS));
                    boolean b1 = false;
                    String m_St = "LOCK";
                    String m_St1 = "NO METER";
                    String m_St2 = "POWER OFF";
                    String m_St3 = "NO DISPLAY";
                    if ((meter_Status.equals(m_St)) || (meter_Status.equals(m_St1)) || (meter_Status.equals(m_St2)) || (meter_Status.equals(m_St3))) {
                        b1 = true;
                    }
                    if (meter_reading_available != null || b1) {
                        b = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }else{

            }



            return b;
        }
    }

/*

    class AsyncUpdate extends AsyncTask<JSONArray, Void, String> {
        // String response1;
        @Override
        protected String doInBackground(JSONArray... params) {
            //String url="http://117.239.214.2:8087/WSDemo1/resttest/invoke/newload";
            //String url="http://117.239.214.4:8087/WSDemo1/resttest/invoke/newload";
            String url = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/newload";
            //  String url="http://192.168.100.57:8080/WSDemo1/resttest/invoke/newload";
            //String url="http://192.168.0.102:8080/WSDemo1/resttest/invoke/newload";
            //  String url="http://10.0.2.2:8080/WSDemo1/resttest/invoke/newload";
            //Log.d("Json Object in do IN Background","::::::::"+params[0].toString()+"::::::::");
            String result = "kljhkljlhkjlkhjlkjh";
            // HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpParams httpParams = new BasicHttpParams();
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);
            HttpResponse httpResponse;

            //JSONObject object=params[0];
            try {
                Log.d("Json Object in do IN Background", "::::::::" + params[0].toString() + "::::::::");
                StringEntity stringEntity = new StringEntity(params[0].toString());
                Log.d("String Entity data", "::::::::::" + stringEntity);
                Log.d("String Entity data", "::::::::::" + stringEntity.toString());
                stringEntity.setContentEncoding("UTF-8");
                stringEntity.setContentType("application/json");

                httpPost.setEntity(stringEntity);
                httpPost.addHeader("Accept", "application/json");

                Log.d("String Entity data", "::::::::::" + stringEntity);
                Log.d("String Entity data", "::::::::::" + stringEntity.toString());

                */
/*httpPost.setHeader("Accept","application/json");
                httpPost.setHeader("Content-type","application/json");*//*

                httpResponse = defaultHttpClient.execute(httpPost);
                liteDatabase.close();
                InputStream inputStream = httpResponse.getEntity().getContent();
                String serverResponse = null;
                if (inputStream != null) {
                    serverResponse = convertInputStreamToString(inputStream);
                }

                // HttpEntity httpEntity=httpResponse.getEntity();
                //response1= EntityUtils.toString(httpEntity);

                Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");
                result = serverResponse;
                Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");

                //result=convertInputStreamToString(inputStream);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            */
/*try {
                HttpResponse httpResponse=httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
*//*

            return result;
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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Trying to upload...");
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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //progressDialog.setMessage(s+"records uploaded.");
            progressDialog.dismiss();
            Log.d("onPOst Execution:", s);
            Integer res = Integer.parseInt(s);
            if (res == 0) {
                Toast.makeText(getActivity(), "No record uploaded to server.", Toast.LENGTH_SHORT).show();
            }
            if (res == 100000) {
                Toast.makeText(getActivity(), "Authentication denied ! \n" + "Please contact to software provider.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), s + " record uploaded", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(getActivity(), "No record uploaded to server.", Toast.LENGTH_LONG).show();
            dismiss();

        }
    }

*/

    class NewAsyncUpdate extends AsyncTask<Cursor, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Trying to upload...");
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
        protected String doInBackground(Cursor... params) {
            String output = null;
            Integer counter=0;
            DbLogin dbLogin = new DbLogin(getActivity(), DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
            SQLiteDatabase sqLiteDatabase = dbLogin.getReadableDatabase();
            Cursor c_dbLogin = dbLogin.getData(sqLiteDatabase);
            c_dbLogin.moveToFirst();
            String imei = c_dbLogin.getString(c_dbLogin.getColumnIndex(DbLogin.IMEI));
            sqLiteDatabase.close();
            JSONArray  jsona_UserImei=new JSONArray();
            JSONObject jsonObjectImei = new JSONObject();
            try {
                jsonObjectImei.put("USER_IMEI", imei);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsona_UserImei.put(jsonObjectImei);

            //10.0.2.2:8080

          // String url1 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/validateUser";
           String url1 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/validateUser";
          // String url1 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/validateUser";
          // String url2 = "http://1.22.91.107:8080/WSDemo1/resttest/invoke/newload";
           String url2 = "http://192.168.0.111:8080/WSDemo1/resttest/invoke/newload";
         //  String url2 = "http://10.0.2.2:8080/WSDemo1/resttest/invoke/newload";
            HttpPost httpPost1 = new HttpPost(url1);
            HttpPost httpPost2 = new HttpPost(url2);
            HttpParams httpParams1 = new BasicHttpParams();
            HttpParams httpParams2 = new BasicHttpParams();
            DefaultHttpClient defaultHttpClient1 = new DefaultHttpClient(httpParams1);
            DefaultHttpClient defaultHttpClient2 = new DefaultHttpClient(httpParams2);
            HttpResponse httpResponse1;
            HttpResponse httpResponse2;

            try {

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
                Integer validation_denied = 0;
                Integer validation_success = 1;
                if (user_validation.equals(validation_denied)) {
                    output = "-1";
                    Log.d("Validation denied for this IMEI", "Contact software provider");
                }
                if (user_validation.equals(validation_success)) {
                    params[0].moveToFirst();
                    do {
                        JSONArray jsona_Upload_data=new JSONArray();
                        JSONObject jsonObject2 = new JSONObject();

                        String meterReading = params[0].getString(params[0].getColumnIndex(DbConnection.METER_READING));
                        String meterStatus = "";
                        meterStatus = params[0].getString(params[0].getColumnIndex(DbConnection.STATUS)).toString();
                        System.out.println("Meter Status:::" + meterStatus);
                        System.out.println("MeterReading:::" + meterReading);
                        String mS = "LOCK";
                        boolean meterSt = false;
                        if (meterStatus.equals(mS)) {
                            meterSt = true;
                            System.out.println("LOCK status found");
                        }
                        if (meterReading != null) {
                            System.out.println("Meter Reading is not null");
                        }

                        //Log.d("Meter_reading value for SC_NO:"+String.valueOf(c.getInt(c.getColumnIndex(DbConnection.SC_NO))),c.getString(c.getColumnIndex(DbConnection.METER_READING)));
                        if (meterReading != null || meterSt) {
                            try {
                                System.out.println("Condition::::::::" + meterStatus);
                                //jsonObject2.put("S_NO",c.getInt(c.getColumnIndex(DbConnection.S_NO)));
                                // Log.d("Meter_reading value for SC_NO:"+String.valueOf(c.getInt(c.getColumnIndex(DbConnection.SC_NO))),c.getString(c.getColumnIndex(DbConnection.METER_READING)));
                                jsonObject2.put("BOOK_NO", params[0].getString(params[0].getColumnIndex(DbConnection.BOOK_NO)));
                                jsonObject2.put("ROUTE_NO", params[0].getString(params[0].getColumnIndex(DbConnection.ROUTE_NO)));
                                jsonObject2.put("SC_NO", params[0].getInt(params[0].getColumnIndex(DbConnection.SC_NO)));
                                //jsonObject2.put("NAME_CONSUMER",c.getString(c.getColumnIndex(DbConnection.NAME_CONSUMER)));
                                //jsonObject2.put("ADDRESS",c.getString(c.getColumnIndex(DbConnection.ADDRESS)));
                                //jsonObject2.put("CONTACT_NO",c.getString(c.getColumnIndex(DbConnection.CONTACT_NO)));
                                //jsonObject2.put("METER_NO",c.getString(c.getColumnIndex(DbConnection.METER_NO)));
                                //jsonObject2.put("SUBSTATIONNAME",c.getString(c.getColumnIndex(DbConnection.SUBSTATIONNAME)));
                                //jsonObject2.put("FEEDERNAME",c.getString(c.getColumnIndex(DbConnection.FEEDERNAME)));
                                jsonObject2.put("DATE", params[0].getString(params[0].getColumnIndex(DbConnection.DATE)));
                                String meter_r_val = params[0].getString(params[0].getColumnIndex(DbConnection.METER_READING));
                                if (meter_r_val != null) {
                                    jsonObject2.put("METER_READING", meter_r_val);
                                } else {
                                    jsonObject2.put("METER_READING", "NOT_SUCCESSFUL");
                                }

                                jsonObject2.put("STATUS", params[0].getString(params[0].getColumnIndex(DbConnection.STATUS)));
                                jsonObject2.put("METER_TYPE", params[0].getString(params[0].getColumnIndex(DbConnection.METER_TYPE)));
                                jsonObject2.put("REMARKS", params[0].getString(params[0].getColumnIndex(DbConnection.REMARKS)));
                                String user = params[0].getString(params[0].getColumnIndex(DbConnection.USER));
                                if (user != null) {
                                    jsonObject2.put("USER", user);
                                } else {
                                    jsonObject2.put("USER", "unavailable");
                                }

                                byte[] bytes = params[0].getBlob(params[0].getColumnIndex(DbConnection.IMAGE));
                                String bytetoBase64string;
                                if (bytes != null) {
                                    //bytesToString = new String(bytes);
                                    bytetoBase64string = Base64.encodeToString(bytes, Base64.DEFAULT);
                                } else {
                                    //bytesToString = "unavailable";
                                    bytetoBase64string = "unavailable";
                                }
                                jsonObject2.put("IMAGE", bytetoBase64string);
                                String location = params[0].getString(params[0].getColumnIndex(DbConnection.FEEDER_LOCATION));
                                if (location != null) {
                                    jsonObject2.put("LOCATION", location);
                                } else {
                                    jsonObject2.put("LOCATION", "unavailable");
                                }

                                jsona_Upload_data.put(jsonObject2);


                               // Log.d("Json Object in do IN Background", "::::::::" + .toString() + "::::::::");
                                StringEntity stringEntity2 = new StringEntity(jsona_Upload_data.toString());
                                Log.d("String Entity data", "::::::::::" + stringEntity2);
                                Log.d("String Entity data", "::::::::::" + stringEntity2.toString());
                                stringEntity2.setContentEncoding("UTF-8");
                                stringEntity2.setContentType("application/json");

                                httpPost2.setEntity(stringEntity2);
                                httpPost2.addHeader("Accept", "application/json");

                                Log.d("String Entity data", "::::::::::" + stringEntity2);
                                Log.d("String Entity data", "::::::::::" + stringEntity2.toString());

                /*httpPost.setHeader("Accept","application/json");
                httpPost.setHeader("Content-type","application/json");*/
                                httpResponse2 = defaultHttpClient2.execute(httpPost2);
                               // liteDatabase.close();
                                InputStream inputStream2 = httpResponse2.getEntity().getContent();
                                String serverResponse2 = null;
                                if (inputStream2 != null) {
                                    serverResponse2 = convertInputStreamToString(inputStream2);
                                }
                                Integer response=Integer.parseInt(serverResponse2);
                                if(response.equals(0)){
                                    System.out.println("Upload not successful"+"\t"+counter);
                                }
                                if(response.equals(1)){

                                    ++counter;
                                    System.out.println("Suuccesful upload"+"\t"+counter);
                                }

                                // HttpEntity httpEntity=httpResponse.getEntity();
                                //response1= EntityUtils.toString(httpEntity);

                                Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");
                                output = serverResponse2;
                                Log.d("Response from Upload server:", ":::::::" + serverResponse + ":::::");

                                Log.d("Response for number of successful upload:", ":::::::" + serverResponse + ":::::");


                                //jsonArray.put(jsonObject2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } while (params[0].moveToNext());

                    output=String.valueOf(counter);

                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return output;
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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Log.d("onPOst Execution:", s);
            Integer res = Integer.parseInt(s);
            if (res == 0) {
                Toast.makeText(getActivity(), "No record uploaded to server \n", Toast.LENGTH_LONG).show();
            }
            if (res == -1) {
                Toast.makeText(getActivity(), "Authentication denied ! \n" + "Please contact to software provider.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), s + " record uploaded", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getActivity(), "No record uploaded to server.", Toast.LENGTH_LONG).show();
            dismiss();


        }
    }
}