package com.general.vvvv.cmr.htc;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Service.AppLocationService;
import com.general.SQLite.DbConnection;
import com.general.SQLite.DbHTCReading;
import com.general.SQLite.DbLogin;
import com.general.vvvv.cmr.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by vvvv on 07-02-2015.
 */
public class HTCActivityMMReadingFromHTCStatus extends ActionBarActivity {
    TextView tv_consumer_id, tv_htc_name, tv_area_code;
    EditText et_kw_ht, et_kva_ht, et_kwh_ht, et_kvah_ht, et_kvarh_ht, et_zone1_ht, et_zone2_ht, et_zone3_ht, et_rem_1_ht, et_kw_lt, et_kva_lt, et_kwh_lt, et_kvah_lt, et_kvarh_lt, et_rem_1_lt;
    Spinner sp_meter_status_ht, sp_remark_ht, sp_meter_type_ht, sp_meter_status_lt, sp_remark_lt, sp_meter_type_lt;
    Button b_commit;//b_preview;
    //ImageButton b_image_click;
    //ImageView iv_preview;
    static int consumer_id;
    static String s_meter_status_ht = "NORMAL";
    static String s_remarks_ht = "NORMAL";
    static String s_meter_type_ht = "DIGITAL";

    static String s_meter_status_lt = "NORMAL";
    static String s_remarks_lt = "NORMAL";
    static String s_meter_type_lt = "DIGITAL";

    boolean can_be_saved = true;
    String alertDmsg = null;
    static String currentDateandTime1;
    static String devicecurrentLocation = null;
    //static Uri fileUri;
    public static boolean readingUpdate = false;
    AppLocationService appLocationService;
    ContentValues contentValues;
    //private static byte[] bytes;
    DbHTCReading dbHTCReading;
    //DbConnection dbConnection;
    SQLiteDatabase sqLiteDatabase;
    DbLogin dbLogin;
    SQLiteDatabase sqLiteDatabaseDbLoginRead;
    static int user;
    static String area_code;
    File f, f2_image_directory;

    void init() {
        tv_consumer_id = (TextView) findViewById(R.id.tv_Consumer_id_value);
        tv_htc_name = (TextView) findViewById(R.id.tv_name_val);
        tv_area_code = (TextView) findViewById(R.id.tv_area_valid_code);

        et_kw_ht = (EditText) findViewById(R.id.et_kw_ht_val);
        et_kva_ht = (EditText) findViewById(R.id.et_kva_ht_val);
        et_kwh_ht = (EditText) findViewById(R.id.et_kwh_ht_val);
        et_kvah_ht = (EditText) findViewById(R.id.et_kvah_ht_val);
        et_kvarh_ht = (EditText) findViewById(R.id.et_kvarh_ht_val);
        et_zone1_ht = (EditText) findViewById(R.id.et_zone1_ht_val);
        et_zone2_ht = (EditText) findViewById(R.id.et_zone2_ht_val);
        et_zone3_ht = (EditText) findViewById(R.id.et_zone3_ht_val);
        et_rem_1_ht = (EditText) findViewById(R.id.et_ht_rem_val);
        et_kw_lt = (EditText) findViewById(R.id.et_kw_lt_val);
        et_kva_lt = (EditText) findViewById(R.id.et_kva_lt_val);
        et_kwh_lt = (EditText) findViewById(R.id.et_kwh_lt_val);
        et_kvah_lt = (EditText) findViewById(R.id.et_kvah_lt_val);
        et_kvarh_lt = (EditText) findViewById(R.id.et_kvarh_lt_val);
        et_rem_1_lt = (EditText) findViewById(R.id.et_lt_rem_val);

        sp_meter_status_ht = (Spinner) findViewById(R.id.sp_ht_meter_stat_val);
        sp_remark_ht = (Spinner) findViewById(R.id.sp_ht_remark_val);
        sp_meter_type_ht = (Spinner) findViewById(R.id.sp_ht_meter_type);
        sp_meter_status_lt = (Spinner) findViewById(R.id.sp_lt_meter_stat_val);
        sp_remark_lt = (Spinner) findViewById(R.id.sp_lt_remark_val);
        sp_meter_type_lt = (Spinner) findViewById(R.id.sp_lt_meter_type);

        b_commit = (Button) findViewById(R.id.b_commit_reading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htc_reading);

        init();

        readingUpdate = false;
        dbHTCReading = new DbHTCReading(HTCActivityMMReadingFromHTCStatus.this, DbHTCReading.DB_NAME, null, DbHTCReading.DB_VERSION);
        sqLiteDatabase = dbHTCReading.getReadableDatabase();
        sqLiteDatabase.close();

        contentValues = new ContentValues();
        appLocationService = new AppLocationService(HTCActivityMMReadingFromHTCStatus.this);


        dbLogin = new DbLogin(HTCActivityMMReadingFromHTCStatus.this, DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
        sqLiteDatabaseDbLoginRead = dbLogin.getReadableDatabase();

        Cursor cuser = dbLogin.getUserId(sqLiteDatabaseDbLoginRead);
        cuser.moveToFirst();
        do {
            user = cuser.getInt(cuser.getColumnIndex(DbLogin.USER_ID));
        } while (cuser.moveToNext());
        sqLiteDatabaseDbLoginRead.close();

        Bundle bundle = getIntent().getExtras();
        consumer_id = bundle.getInt("C_code");

        String[] consumer_data = dbHTCReading.getConsumerMasterData(consumer_id);

        tv_consumer_id.setText(String.valueOf(consumer_id));

        tv_area_code.setText(consumer_data[0]);
        tv_htc_name.setText(consumer_data[1]);
        area_code = consumer_data[0];


        ArrayAdapter<CharSequence> adapter_ht = ArrayAdapter.createFromResource(this, R.array.meter_status, android.R.layout.simple_spinner_item);
        adapter_ht.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_meter_status_ht.setAdapter(adapter_ht);

        sp_meter_status_ht.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_meter_status_ht = sp_meter_status_ht.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<CharSequence> adapter1_ht = ArrayAdapter.createFromResource(this, R.array.Remark, android.R.layout.simple_spinner_item);
        adapter1_ht.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_remark_ht.setAdapter(adapter1_ht);
        sp_remark_ht.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_remarks_ht = sp_remark_ht.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter2_ht = ArrayAdapter.createFromResource(this, R.array.meter_type, android.R.layout.simple_spinner_item);
        adapter2_ht.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_meter_type_ht.setAdapter(adapter2_ht);
        sp_meter_type_ht.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_meter_type_ht = sp_meter_type_ht.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter_lt = ArrayAdapter.createFromResource(this, R.array.meter_status, android.R.layout.simple_spinner_item);
        adapter_lt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_meter_status_lt.setAdapter(adapter_lt);
        sp_meter_status_lt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_meter_status_lt = sp_meter_status_lt.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<CharSequence> adapter1_lt = ArrayAdapter.createFromResource(this, R.array.Remark, android.R.layout.simple_spinner_item);
        adapter1_lt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_remark_lt.setAdapter(adapter1_lt);
        sp_remark_lt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_remarks_lt = sp_remark_lt.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter2_lt = ArrayAdapter.createFromResource(this, R.array.meter_type, android.R.layout.simple_spinner_item);
        adapter2_lt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_meter_type_lt.setAdapter(adapter2_lt);
        sp_meter_type_lt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_meter_type_lt = sp_meter_type_lt.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String check_meter_status = bundle.getString("STATUS_CM");
        String a = "NORMAL", b = "DEFECTIVE", c = "DIAL COMPLETE";
        String[] type = getResources().getStringArray(R.array.meter_type);
        String[] status = getResources().getStringArray(R.array.meter_status);
        String[] remarks = getResources().getStringArray(R.array.Remark);
        if (check_meter_status.equals(a)) {

            sp_meter_status_lt.setSelection(0);
            s_meter_status_lt = "NORMAL";
            String m_type = bundle.getString("TYPE"), rem = bundle.getString("REMARKS");
            s_meter_type_lt = m_type;
            s_remarks_lt = rem;

            int type_index = Arrays.asList(type).indexOf(m_type);
            int rem_index = Arrays.asList(remarks).indexOf(rem);
            sp_meter_type_lt.setSelection(type_index);
            sp_remark_lt.setSelection(rem_index);

            if (bundle.getString("KW_CM") != null) {
                et_kw_lt.setText(bundle.getString("KW_CM"));
            }
            if (bundle.getString("KVA_CM") != null) {
                et_kva_lt.setText(bundle.getString("KVA_CM"));
            }
            if (bundle.getString("KWH_CM") != null) {
                et_kwh_lt.setText(bundle.getString("KWH_CM"));
            }
            if (bundle.getString("KVAH_CM") != null) {
                et_kvah_lt.setText(bundle.getString("KVAH_CM"));
            }
            if (bundle.getString("KVARH_CM") != null) {
                et_kvarh_lt.setText(bundle.getString("KVARH_CM"));
            }
            if (bundle.getString("REMARK1") != null) {
                String remNA="NA";
                if(bundle.getString("REMARK1").equals(remNA)){

                }else{
                    et_rem_1_lt.setText(bundle.getString("REMARK1"));
                }

            }

        } else {
            if (check_meter_status.equals(b)) {
                sp_meter_status_lt.setSelection(1);
                s_meter_status_lt = bundle.getString("STATUS_CM");
                String m_type = bundle.getString("TYPE"), rem = bundle.getString("REMARKS");
                s_meter_type_lt = m_type;
                s_remarks_lt = rem;

                int type_index = Arrays.asList(type).indexOf(m_type);
                int rem_index = Arrays.asList(remarks).indexOf(rem);
                sp_meter_type_lt.setSelection(type_index);
                sp_remark_lt.setSelection(rem_index);

                if (bundle.getString("KW_CM") != null) {
                    et_kw_lt.setText(bundle.getString("KW_CM"));
                }
                if (bundle.getString("KVA_CM") != null) {
                    et_kva_lt.setText(bundle.getString("KVA_CM"));
                }
                if (bundle.getString("KWH_CM") != null) {
                    et_kwh_lt.setText(bundle.getString("KWH_CM"));
                }
                if (bundle.getString("KVAH_CM") != null) {
                    et_kvah_lt.setText(bundle.getString("KVAH_CM"));
                }
                if (bundle.getString("KVARH_CM") != null) {
                    et_kvarh_lt.setText(bundle.getString("KVARH_CM"));
                }
                if (bundle.getString("REMARK1") != null) {
                    String remNA="NA";
                    if(bundle.getString("REMARK1").equals(remNA)){

                    }else{
                        et_rem_1_lt.setText(bundle.getString("REMARK1"));
                    }

                }


            } else {
                if (check_meter_status.equals(c)) {
                    sp_meter_status_lt.setSelection(3);
                    s_meter_status_lt = bundle.getString("STATUS_CM");
                    String m_type = bundle.getString("TYPE"), rem = bundle.getString("REMARKS");
                    s_meter_type_lt = m_type;
                    s_remarks_lt = rem;

                    int type_index = Arrays.asList(type).indexOf(m_type);
                    int rem_index = Arrays.asList(remarks).indexOf(rem);
                    sp_meter_type_lt.setSelection(type_index);
                    sp_remark_lt.setSelection(rem_index);

                    if (bundle.getString("KW_CM") != null) {
                        et_kw_lt.setText(bundle.getString("KW_CM"));
                    }
                    if (bundle.getString("KVA_CM") != null) {
                        et_kva_lt.setText(bundle.getString("KVA_CM"));
                    }
                    if (bundle.getString("KWH_CM") != null) {
                        et_kwh_lt.setText(bundle.getString("KWH_CM"));
                    }
                    if (bundle.getString("KVAH_CM") != null) {
                        et_kvah_lt.setText(bundle.getString("KVAH_CM"));
                    }
                    if (bundle.getString("KVARH_CM") != null) {
                        et_kvarh_lt.setText(bundle.getString("KVARH_CM"));
                    }
                    if (bundle.getString("REMARK1") != null) {
                        String remNA="NA";
                        if(bundle.getString("REMARK1").equals(remNA)){

                        }else{
                            et_rem_1_lt.setText(bundle.getString("REMARK1"));
                        }

                    }

                } else {
                    String stat=bundle.getString("STATUS_CM");
                    int stat_index=Arrays.asList(status).indexOf(stat);
                    sp_meter_status_lt.setSelection(stat_index);
                    s_meter_status_lt = bundle.getString("STATUS_CM");
                    String m_type = bundle.getString("TYPE"), rem = bundle.getString("REMARKS");
                    s_meter_type_lt = m_type;
                    s_remarks_lt = rem;

                    int type_index = Arrays.asList(type).indexOf(m_type);
                    int rem_index = Arrays.asList(remarks).indexOf(rem);
                    sp_meter_type_lt.setSelection(type_index);
                    sp_remark_lt.setSelection(rem_index);

                    if (bundle.getString("REMARK1") != null) {
                        String remNA="NA";
                        if(bundle.getString("REMARK1").equals(remNA)){

                        }else{
                            et_rem_1_lt.setText(bundle.getString("REMARK1"));
                        }

                    }

                }
            }
        }

        b_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy_HHmmss");/////////////////////Current Time ///////////////////////////
                currentDateandTime1 = sdf1.format(new Date());

                Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
                Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                if (gpsLocation != null) {
                    double latitude = gpsLocation.getLatitude();
                    double longitude = gpsLocation.getLongitude();
                    devicecurrentLocation = "Latitude: " + Double.toString(latitude) + " Longitude: " + Double.toString(longitude);
                    /*Toast.makeText(
                            getApplicationContext(),
                            "Mobile Location (GPS): \nLatitude: " + latitude
                                    + "\nLongitude: " + longitude,
                            Toast.LENGTH_LONG).show();*/
                } else {

                    if (nwLocation != null) {
                        double latitude = nwLocation.getLatitude();
                        double longitude = nwLocation.getLongitude();
                        devicecurrentLocation = "Latitude: " + Double.toString(latitude) + " Longitude: " + Double.toString(longitude);
                        /*Toast.makeText(
                                getApplicationContext(),
                                "Mobile Location (NW): \nLatitude: " + latitude
                                        + "\nLongitude: " + longitude,
                                Toast.LENGTH_LONG).show();*/
                    } else {
                        alertDmsg = "GPS Provider & Network Provider";
                        alertMessage(alertDmsg);
                    }
                }
                boolean allow_Ht_reading;
                boolean allow_Lt_reading;

                if ((s_meter_status_ht.equals("LOCK")) || (s_meter_status_ht.equals("NO METER")) || (s_meter_status_ht.equals("POWER OFF")) || (s_meter_status_ht.equals("NO DISPLAY"))) {
                    allow_Ht_reading = false;
                } else {
                    allow_Ht_reading = true;
                }
                if ((s_meter_status_lt.equals("LOCK")) || (s_meter_status_lt.equals("NO METER")) || (s_meter_status_lt.equals("POWER OFF")) || (s_meter_status_lt.equals("NO DISPLAY"))) {
                    allow_Lt_reading = false;

                } else {
                    allow_Lt_reading = true;
                }
                if (allow_Ht_reading && allow_Lt_reading) {
                    if ((et_kw_ht.getText().length() != 0) && (et_kva_ht.getText().length() != 0) && (et_kwh_ht.getText().length() != 0) && (et_kvah_ht.getText().length() != 0) && (et_kvarh_ht.getText().length() != 0) && (et_kwh_lt.getText().length() != 0)) {
                        localDatabaseUpdate();
                    } else {
                        if ((et_kw_ht.getText().length() != 0)) {
                            if ((et_kva_ht.getText().length() != 0)) {
                                if ((et_kwh_ht.getText().length() != 0)) {
                                    if ((et_kvah_ht.getText().length() != 0)) {
                                        if ((et_kvarh_ht.getText().length() != 0)) {
                                            if ((et_kwh_lt.getText().length() != 0)) {

                                            } else {
                                                Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KWH Check meter.", Toast.LENGTH_LONG).show();
                                            }

                                        } else {
                                            Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KVARH Main meter.", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KVAH Main meter.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KWH Main meter.", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KVA Main meter.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KW Main meter.", Toast.LENGTH_LONG).show();
                        }
                        can_be_saved = false;
                    }
                }else{
                    boolean allow_reading,allow_ht_save=false,allow_lt_save=false;
                    if(allow_Ht_reading){
                        if ((et_kw_ht.getText().length() != 0) && (et_kva_ht.getText().length() != 0) && (et_kwh_ht.getText().length() != 0) && (et_kvah_ht.getText().length() != 0) && (et_kvarh_ht.getText().length() != 0)){
                            //allow_ht_save=true;
                            if(allow_Lt_reading){
                                if ((et_kwh_lt.getText().length() != 0)) {
                                    //allow_lt_save=true;
                                    localDatabaseUpdate();

                                } else {
                                    Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KWH Check meter.", Toast.LENGTH_LONG).show();
                                }

                            }else{
                                localDatabaseUpdate();
                            }
                        }else{
                            if ((et_kw_ht.getText().length() != 0)) {
                                if ((et_kva_ht.getText().length() != 0)) {
                                    if ((et_kwh_ht.getText().length() != 0)) {
                                        if ((et_kvah_ht.getText().length() != 0)) {
                                            if ((et_kvarh_ht.getText().length() != 0)) {


                                            } else {
                                                Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KVARH Main meter.", Toast.LENGTH_LONG).show();
                                            }

                                        } else {
                                            Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KVAH Main meter.", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KWH Main meter.", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KVA Main meter.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KW Main meter.", Toast.LENGTH_LONG).show();
                            }
                            can_be_saved=false;
                        }
                    }else{
                        if(allow_Lt_reading){
                            if ((et_kwh_lt.getText().length() != 0)) {
                                //allow_lt_save=true;
                                localDatabaseUpdate();

                            } else {
                                Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Kindly insert the meter reading for KWH Check meter.", Toast.LENGTH_LONG).show();
                            }

                        }else{
                            localDatabaseUpdate();
                        }
                    }

                }


                if (readingUpdate) {
                    //deleteLastFromDCIM();
                    //deleteLastPhotoTaken();
                    Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Local database get successfully updated !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, "Problem in updating local database !\n"+"Crosscheck your meter reading.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    void localDatabaseUpdate() {
        String remark1_ht = null;
        String remark1_lt = null;
        remark1_ht = et_rem_1_ht.getText().toString();
        remark1_lt = et_rem_1_lt.getText().toString();
        if ((s_meter_status_ht.equals("LOCK")) || (s_meter_status_ht.equals("NO METER")) || (s_meter_status_ht.equals("POWER OFF")) || (s_meter_status_ht.equals("NO DISPLAY"))) {
            Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, s_meter_status_ht + " status found.", Toast.LENGTH_SHORT).show();
            //contentValues.put(DbConnection.METER_READING,"");
            contentValues.put(DbHTCReading.REMARKS_MM, s_remarks_ht);
            contentValues.put(DbHTCReading.STATUS_MM, s_meter_status_ht);
            contentValues.put(DbHTCReading.METER_TYPE_MM, s_meter_type_ht);
            contentValues.put(DbHTCReading.DATE, currentDateandTime1);
            contentValues.put(DbHTCReading.FEEDER_LOCATION, devicecurrentLocation);


        } else {
            contentValues.put(DbHTCReading.KW_MM, et_kw_ht.getText().toString());
            contentValues.put(DbHTCReading.KVA_MM, et_kva_ht.getText().toString());
            contentValues.put(DbHTCReading.KWH_MM, et_kwh_ht.getText().toString());
            contentValues.put(DbHTCReading.KVAH_MM, et_kvah_ht.getText().toString());
            contentValues.put(DbHTCReading.KVARH_MM, et_kvarh_ht.getText().toString());
            contentValues.put(DbHTCReading.ZONE_1_MM, et_kw_ht.getText().toString());
            contentValues.put(DbHTCReading.ZONE_2_MM, et_kw_ht.getText().toString());
            contentValues.put(DbHTCReading.ZONE_3_MM, et_kw_ht.getText().toString());

            contentValues.put(DbHTCReading.REMARKS_MM, s_remarks_ht);
            contentValues.put(DbHTCReading.STATUS_MM, s_meter_status_ht);
            contentValues.put(DbHTCReading.METER_TYPE_MM, s_meter_type_ht);
            contentValues.put(DbConnection.DATE, currentDateandTime1);
            contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);

        }

        if ((s_meter_status_lt.equals("LOCK")) || (s_meter_status_lt.equals("NO METER")) || (s_meter_status_lt.equals("POWER OFF")) || (s_meter_status_lt.equals("NO DISPLAY"))) {
            Toast.makeText(HTCActivityMMReadingFromHTCStatus.this, s_meter_status_lt + " status found.", Toast.LENGTH_SHORT).show();
            //contentValues.put(DbConnection.METER_READING,"");
            contentValues.put(DbHTCReading.REMARKS_CM, s_remarks_lt);
            contentValues.put(DbHTCReading.STATUS_CM, s_meter_status_lt);
            contentValues.put(DbHTCReading.METER_TYPE_CM, s_meter_type_lt);

        } else {
            contentValues.put(DbHTCReading.KW_CM, et_kw_lt.getText().toString());
            contentValues.put(DbHTCReading.KVA_CM, et_kva_lt.getText().toString());
            contentValues.put(DbHTCReading.KWH_CM, et_kwh_lt.getText().toString());
            contentValues.put(DbHTCReading.KVAH_CM, et_kvah_lt.getText().toString());
            contentValues.put(DbHTCReading.KVARH_CM, et_kvarh_lt.getText().toString());
            contentValues.put(DbHTCReading.REMARKS_CM, s_remarks_lt);
            contentValues.put(DbHTCReading.STATUS_CM, s_meter_status_lt);
            contentValues.put(DbHTCReading.METER_TYPE_CM, s_meter_type_lt);

        }


        if (remark1_ht.length() > 0){
            contentValues.put(DbHTCReading.REMARK1_MM,remark1_ht);
            //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
        }else{
            contentValues.put(DbHTCReading.REMARK1_MM,"NA");
            // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
        }

        if (remark1_lt.length() > 0){
            contentValues.put(DbHTCReading.REMARK1_CM,remark1_lt);
            //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
        }else{
            contentValues.put(DbHTCReading.REMARK1_CM,"NA");
            // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
        }

        contentValues.put(DbHTCReading.USER,user);
        dbHTCReading.updateReadingDetails2(contentValues, consumer_id);

    }

    public void alertMessage(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                HTCActivityMMReadingFromHTCStatus.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        HTCActivityMMReadingFromHTCStatus.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


}
