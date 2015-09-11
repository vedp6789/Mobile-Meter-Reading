package com.general.vvvv.cmr.ltp;

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
import com.general.SQLite.DbLTPReading;
import com.general.SQLite.DbLogin;
import com.general.vvvv.cmr.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vvvv on 25-01-2015.
 */
public class LtpActivityReadingFromLtpStatus extends ActionBarActivity {
    TextView tv_consumer_id, tv_ltp_name, tv_area_code, tv_cgl;
    EditText et_kw, et_kva, et_kwh, et_kvah, et_kvarh, et_zone1, et_zone2, et_zone3, et_rem_1;
    Spinner sp_meter_status, sp_remark, sp_meter_type;
    Button b_commit;
    static int consumer_id;
    static String s_meter_status = "NORMAL";
    static String s_remarks = "NORMAL";
    static String s_meter_type = "DIGITAL";


    boolean can_be_saved = true;
    String alertDmsg = null;
    static String currentDateandTime1;
    static String devicecurrentLocation = null;
    //static Uri fileUri;
    public static boolean readingUpdate = false;
    AppLocationService appLocationService;
    ContentValues contentValues;
    //private static byte[] bytes;
    DbLTPReading dbLTPReading;

    SQLiteDatabase sqLiteDatabase;
    DbLogin dbLogin;
    SQLiteDatabase sqLiteDatabaseDbLoginRead;
    static int user;
    static String area_code;
    File f, f2_image_directory;
    int consumer_code;
    String area,cgl,name;
    void init() {
        tv_consumer_id = (TextView) findViewById(R.id.tv_Consumer_id_value);
        tv_ltp_name = (TextView) findViewById(R.id.tv_ltp_name_val);
        tv_area_code = (TextView) findViewById(R.id.tv_area_valid_code);
        tv_cgl = (TextView) findViewById(R.id.tv_cgl_val);

        et_kw = (EditText) findViewById(R.id.et_kw_val);
        et_kva = (EditText) findViewById(R.id.et_kva_val);
        et_kwh = (EditText) findViewById(R.id.et_kwh_val);
        et_kvah = (EditText) findViewById(R.id.et_kvah_val);
        et_kvarh = (EditText) findViewById(R.id.et_kvarh_val);
        et_zone1 = (EditText) findViewById(R.id.et_zone1_val);
        et_zone2 = (EditText) findViewById(R.id.et_zone2_val);
        et_zone3 = (EditText) findViewById(R.id.et_zone3_val);
        et_rem_1 = (EditText) findViewById(R.id.et_rem_val);


        sp_meter_status = (Spinner) findViewById(R.id.sp_meter_stat_val);
        sp_remark = (Spinner) findViewById(R.id.sp_remark_val);
        sp_meter_type = (Spinner) findViewById(R.id.sp_meter_type);


        b_commit = (Button) findViewById(R.id.b_commit_reading);
    }

    public void alertMessage(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LtpActivityReadingFromLtpStatus.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        LtpActivityReadingFromLtpStatus.this.startActivity(intent);
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltp_reading_from_ltp_stautus);
        init();

        readingUpdate = false;
        dbLTPReading = new DbLTPReading(LtpActivityReadingFromLtpStatus.this, DbLTPReading.DB_NAME, null, DbLTPReading.DB_VERSION);
        sqLiteDatabase = dbLTPReading.getReadableDatabase();
        sqLiteDatabase.close();

        contentValues = new ContentValues();
        appLocationService = new AppLocationService(LtpActivityReadingFromLtpStatus.this);


        dbLogin = new DbLogin(LtpActivityReadingFromLtpStatus.this, DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
        sqLiteDatabaseDbLoginRead = dbLogin.getReadableDatabase();

        Cursor cuser = dbLogin.getUserId(sqLiteDatabaseDbLoginRead);
        cuser.moveToFirst();
        do {
            user = cuser.getInt(cuser.getColumnIndex(DbLogin.USER_ID));
        } while (cuser.moveToNext());
        sqLiteDatabaseDbLoginRead.close();

        consumer_code=getIntent().getExtras().getInt("C_code");
        consumer_id=consumer_code;
        name=getIntent().getExtras().getString("C_name");
        area=getIntent().getExtras().getString("Area_code");
        area_code=area;
        cgl=getIntent().getExtras().getString("Cgl_no");
        tv_consumer_id.setText(String.valueOf(consumer_code));
        tv_area_code.setText(area);
        tv_cgl.setText(cgl);
        tv_ltp_name.setText(name);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.meter_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_meter_status.setAdapter(adapter);
        sp_meter_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_meter_status = sp_meter_status.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Remark, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_remark.setAdapter(adapter1);
        sp_remark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_remarks = sp_remark.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.meter_type, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_meter_type.setAdapter(adapter2);
        sp_meter_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_meter_type = sp_meter_type.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//////////////////////////////////////////////////////////////////////////////////////////On Commit to local database//////////////////////////////////////////////////
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
                boolean allow_reading;
                boolean allow_Lt_reading;

                if ((s_meter_status.equals("LOCK")) || (s_meter_status.equals("NO METER")) || (s_meter_status.equals("POWER OFF")) || (s_meter_status.equals("NO DISPLAY"))) {
                    allow_reading = false;
                } else {
                    allow_reading = true;
                }

                if (allow_reading) {
                    if ((et_kw.getText().length() != 0) && (et_kva.getText().length() != 0) && (et_kwh.getText().length() != 0) && (et_kvah.getText().length() != 0) && (et_kvarh.getText().length() != 0)) {
                        localDatabaseUpdate();
                    } else {
                        if ((et_kw.getText().length() != 0)) {
                            if ((et_kva.getText().length() != 0)) {
                                if ((et_kwh.getText().length() != 0)) {
                                    if ((et_kvah.getText().length() != 0)) {
                                        if ((et_kvarh.getText().length() != 0)) {
                                        } else {
                                            Toast.makeText(LtpActivityReadingFromLtpStatus.this, "Kindly insert the meter reading for KVARH.", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Toast.makeText(LtpActivityReadingFromLtpStatus.this, "Kindly insert the meter reading for KVAH.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(LtpActivityReadingFromLtpStatus.this, "Kindly insert the meter reading for KWH.", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(LtpActivityReadingFromLtpStatus.this, "Kindly insert the meter reading for KVA.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LtpActivityReadingFromLtpStatus.this, "Kindly insert the meter reading for KW.", Toast.LENGTH_LONG).show();
                        }
                        can_be_saved = false;
                    }
                } else {

                    localDatabaseUpdate();
                }


                if (readingUpdate) {
                    //deleteLastFromDCIM();
                    //deleteLastPhotoTaken();
                    Toast.makeText(LtpActivityReadingFromLtpStatus.this, "Local database get successfully updated !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LtpActivityReadingFromLtpStatus.this, "Problem in updating local database !\n" + "Crosscheck your meter reading.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void localDatabaseUpdate() {
        String remark1 = null;

        remark1 = et_rem_1.getText().toString();

        if ((s_meter_status.equals("LOCK")) || (s_meter_status.equals("NO METER")) || (s_meter_status.equals("POWER OFF")) || (s_meter_status.equals("NO DISPLAY"))) {
            Toast.makeText(LtpActivityReadingFromLtpStatus.this, s_meter_status + " status found.", Toast.LENGTH_SHORT).show();
            //contentValues.put(DbConnection.METER_READING,"");
            contentValues.put(DbLTPReading.REMARKS, s_remarks);
            contentValues.put(DbLTPReading.STATUS, s_meter_status);
            contentValues.put(DbLTPReading.METER_TYPE, s_meter_type);
            contentValues.put(DbLTPReading.DATE, currentDateandTime1);
            contentValues.put(DbLTPReading.FEEDER_LOCATION, devicecurrentLocation);


        } else {
            contentValues.put(DbLTPReading.KW, et_kw.getText().toString());
            contentValues.put(DbLTPReading.KVA, et_kva.getText().toString());
            contentValues.put(DbLTPReading.KWH, et_kwh.getText().toString());
            contentValues.put(DbLTPReading.KVAH, et_kvah.getText().toString());
            contentValues.put(DbLTPReading.KVARH, et_kvarh.getText().toString());
            contentValues.put(DbLTPReading.ZONE_1, et_kw.getText().toString());
            contentValues.put(DbLTPReading.ZONE_2, et_kw.getText().toString());
            contentValues.put(DbLTPReading.ZONE_3, et_kw.getText().toString());

            contentValues.put(DbLTPReading.REMARKS, s_remarks);
            contentValues.put(DbLTPReading.STATUS, s_meter_status);
            contentValues.put(DbLTPReading.METER_TYPE, s_meter_type);
            contentValues.put(DbLTPReading.DATE, currentDateandTime1);
            contentValues.put(DbLTPReading.FEEDER_LOCATION, devicecurrentLocation);

        }


        if (remark1.length() > 0) {
            contentValues.put(DbLTPReading.REMARK1, remark1);
            //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
        } else {
            contentValues.put(DbLTPReading.REMARK1, "NA");
            // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
        }


        contentValues.put(DbLTPReading.USER, user);
        dbLTPReading.updateReadingDetails2(contentValues, consumer_id);

    }

}
