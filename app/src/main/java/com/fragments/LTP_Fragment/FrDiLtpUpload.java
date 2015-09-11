package com.fragments.LTP_Fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Service.UploadLTPReadingService;
import com.Service.UploadMeterReadingService;
import com.fragments.Nullable;
import com.general.SQLite.DbConnection;
import com.general.SQLite.DbLTPReading;
import com.general.vvvv.cmr.R;
import com.remote_connection.InternetConnectivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by comp on 16/01/2015.
 */
public class FrDiLtpUpload extends DialogFragment implements View.OnClickListener {
    EditText et_area_upload;
    Button b_area_upload, b_cancel_dialog_upload;
    ProgressDialog progressDialog;
    static String area_code = null;
    DbLTPReading dbLTPReading;
    SQLiteDatabase liteDatabase;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_area_upload:
                InternetConnectivity internetConnectivity = new InternetConnectivity();
                Boolean aBoolean = internetConnectivity.checkInternetConnection(getActivity());
                if (aBoolean) {
                    if (et_area_upload.getText().length() != 0) {
                        RetrieveUploadableDataFromLocalDb retrieveUploadableDataFromLocalDb = new RetrieveUploadableDataFromLocalDb();
                        retrieveUploadableDataFromLocalDb.getdatafromDb(String.valueOf(et_area_upload.getText()).toUpperCase());
                    } else {
                        Toast.makeText(getActivity(), "Area code please !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No internet connectivity found !", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.b_area_uploadDialogCancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.f_di_ltp_upload, container, false);
        et_area_upload = (EditText) v.findViewById(R.id.et_area_code_upload);
        b_area_upload = (Button) v.findViewById(R.id.b_area_upload);
        b_cancel_dialog_upload = (Button) v.findViewById(R.id.b_area_uploadDialogCancel);

        b_area_upload.setOnClickListener(this);
        b_cancel_dialog_upload.setOnClickListener(this);

        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.upload_dialog_title));
        dialog.setCanceledOnTouchOutside(false);
        return v;

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


        public void getdatafromDb(String area) {
            dbLTPReading = new DbLTPReading(getActivity(), DbLTPReading.DB_NAME, null, DbLTPReading.DB_VERSION);

            area_code = area;
            liteDatabase = dbLTPReading.getWritableDatabase();

            Cursor c = dbLTPReading.retriveDataByArea(area);
            Log.d("Cursor length:", String.valueOf(c.getCount()));
            if (c.getCount() > 0) {
                final Cursor c1 = dbLTPReading.retriveDataByAreaCode(area);
                boolean b = checkWhetherAnyConsumerMeterReadingUpdated(c1);
                if (b) {

                    Intent intentUploadMRS =new Intent(getActivity(), UploadLTPReadingService.class);
                    intentUploadMRS.putExtra("DemoT","check");
                    intentUploadMRS.putExtra("AREA",area_code);



                    getActivity().startService(intentUploadMRS);
                    dismiss();





                   /* handler=new Handler();
                    runnable=new Runnable() {
                        @Override
                        public void run() {

                        }
                    };
                    handler.postDelayed(runnable,10000);
*/

                } else {
                    Toast.makeText(getActivity(), "Meter reading is not available for any Consumer related to this area!" + "\n" + "Upload get canceled.", Toast.LENGTH_LONG).show();
                    liteDatabase.close();
                }

            } else {
                Toast.makeText(getActivity(), "Area code: " + area_code + " is not available !" + "\n" + "Kindly check your Area code.", Toast.LENGTH_LONG).show();
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

                    String KW=cursor.getString(cursor.getColumnIndex(DbLTPReading.KW));
                    String KVA=cursor.getString(cursor.getColumnIndex(DbLTPReading.KVA));
                    String KWH=cursor.getString(cursor.getColumnIndex(DbLTPReading.KWH));
                    String KVAH=cursor.getString(cursor.getColumnIndex(DbLTPReading.KVAH));
                    String KVARH=cursor.getString(cursor.getColumnIndex(DbLTPReading.KVARH));

                    String meter_Status = "";
                    meter_Status = cursor.getString(cursor.getColumnIndex(DbLTPReading.STATUS));
                    boolean b1 = false;
                    String m_St = "LOCK";
                    String m_St1 = "NO METER";
                    String m_St2 = "POWER OFF";
                    String m_St3 = "NO DISPLAY";
                    if ((meter_Status.equals(m_St)) || (meter_Status.equals(m_St1)) || (meter_Status.equals(m_St2)) || (meter_Status.equals(m_St3))) {
                        b1 = true;
                    }
                    if (((KW != null) && (KVA != null) && (KWH != null) && (KVAH != null) && (KVARH != null))  || b1) {
                        b = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }else{

            }



            return b;
        }
    }



}
