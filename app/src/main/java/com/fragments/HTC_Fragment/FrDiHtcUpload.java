package com.fragments.HTC_Fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Service.UploadHTCReadingService;
import com.Service.UploadLTPReadingService;
import com.general.SQLite.DbHTCReading;
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
public class FrDiHtcUpload extends DialogFragment implements View.OnClickListener {
    Button b_upload_htc,b_cancel;
    EditText et_area;
    static String area_code = null;
    DbHTCReading dbHTCReading;
    SQLiteDatabase liteDatabase;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_area_upload:
                InternetConnectivity internetConnectivity = new InternetConnectivity();
                Boolean aBoolean = internetConnectivity.checkInternetConnection(getActivity());
                if (aBoolean) {
                    if (et_area.getText().length() != 0) {
                        RetrieveUploadableDataFromLocalDb retrieveUploadableDataFromLocalDb = new RetrieveUploadableDataFromLocalDb();
                        retrieveUploadableDataFromLocalDb.getdatafromDb(String.valueOf(et_area.getText()).toUpperCase());
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

    class RetrieveUploadableDataFromLocalDb {
        Context context = getActivity();
        Handler handler=null;
        Runnable runnable=null;
        boolean stopRunnale=false;



        public void getdatafromDb(String area) {
            dbHTCReading = new DbHTCReading(getActivity(), DbHTCReading.DB_NAME, null, DbHTCReading.DB_VERSION);

            area_code = area;
            liteDatabase = dbHTCReading.getWritableDatabase();

            Cursor c = dbHTCReading.retriveDataByArea(area);
            Log.d("Cursor length:", String.valueOf(c.getCount()));
            if (c.getCount() > 0) {
                final Cursor c1 = dbHTCReading.retriveDataByAreaCode(area);
                boolean b = checkWhetherAnyConsumerMeterReadingUpdated(c1);
                if (b) {

                    Intent intentUploadMRS =new Intent(getActivity(), UploadHTCReadingService.class);

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

                    String KW_MM=cursor.getString(cursor.getColumnIndex(DbHTCReading.KW_MM));
                    String KVA_MM=cursor.getString(cursor.getColumnIndex(DbHTCReading.KVA_MM));
                    String KWH_MM=cursor.getString(cursor.getColumnIndex(DbHTCReading.KWH_MM));
                    String KVAH_MM=cursor.getString(cursor.getColumnIndex(DbHTCReading.KVAH_MM));
                    String KVARH_MM=cursor.getString(cursor.getColumnIndex(DbHTCReading.KVARH_MM));

                    String KWH_CM=cursor.getString(cursor.getColumnIndex(DbHTCReading.KWH_CM));

                    String meter_Status_MM = "";
                    String meter_Status_CM = "";
                    meter_Status_MM = cursor.getString(cursor.getColumnIndex(DbHTCReading.STATUS_MM));
                    meter_Status_CM = cursor.getString(cursor.getColumnIndex(DbHTCReading.STATUS_CM));
                    boolean b1=false,b2=false;
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
                    if ((((KW_MM != null) && (KVA_MM != null) && (KWH_MM != null) && (KVAH_MM != null) && (KVARH_MM != null))  || b1) || ((KWH_CM != null)|| b2) ) {
                        b = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }else{

            }



            return b;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.f_di_ltp_upload,container,false);
        b_upload_htc= (Button) view.findViewById(R.id.b_area_upload);
        b_cancel= (Button) view.findViewById(R.id.b_area_uploadDialogCancel);
        et_area= (EditText) view.findViewById(R.id.et_area_code_upload);

        b_upload_htc.setOnClickListener(this);
        b_cancel.setOnClickListener(this);

        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.upload_dialog_title));
        dialog.setCanceledOnTouchOutside(false);

        return view;
    }
}
