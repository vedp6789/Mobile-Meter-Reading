package com.general.vvvv.cmr;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Service.AppLocationService;
import com.general.SQLite.DbConnection;
import com.general.SQLite.DbLogin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vvvv on 22-08-2014.
 */
public class MyReading extends ActionBarActivity {
    TextView tv_connection_code_value, tv_meter_no, tv_meter_no_val, tv_meter_reading, tv_route_no, tv_route_no_val, tv_cons_name, tv_cons_name_val, tv_meter_status, tv_meter_type, tv_remark, tv_image;
    TextView tv_cgl_no, tv_cgl_no_val, tv_connection_code, tv_book_code, tv_book_code_val;
    EditText et_Meter_read_val,et_rem_new;
    Spinner sp_meter_status_v, sp_remark_val, sp_meter_type;
    Button b_commit;//b_preview;
    ImageButton b_image_click;
    ImageView iv_preview;
    static int connection_code;
    static String s_meter_status = "NORMAL";
    static String s_remarks = "NORMAL";
    static String s_meter_type = "DIGITAL";
    boolean can_be_saved = true;
    String alertDmsg = null;
    static String currentDateandTime1;
    static String devicecurrentLocation = null;
    static Uri fileUri;
    public static boolean readingUpdate = false;
    AppLocationService appLocationService;
    ContentValues contentValues;
    private static byte[] bytes;
    DbConnection dbConnection;
    SQLiteDatabase sqLiteDatabase;
    DbLogin dbLogin;
    SQLiteDatabase sqLiteDatabaseDbLoginRead;
    static int user;
    static String book_no;
    File f,f2_image_directory;

    Cursor cursor_master_Data;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyDbErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        bytes = null;
        fullNaming();
        readingUpdate=false;
        dbConnection = new DbConnection(MyReading.this, DbConnection.DB_NAME, null, DbConnection.DB_VERSION, new MyDbErrorHandler());
        sqLiteDatabase = dbConnection.getReadableDatabase();
        sqLiteDatabase.close();

        contentValues = new ContentValues();
        appLocationService = new AppLocationService(MyReading.this);


        dbLogin = new DbLogin(MyReading.this, DbLogin.DB_NAME, null, DbLogin.DB_VERSION);
        sqLiteDatabaseDbLoginRead = dbLogin.getReadableDatabase();

        Cursor cuser = dbLogin.getUserId(sqLiteDatabaseDbLoginRead);
        cuser.moveToFirst();
        do {
            user = cuser.getInt(cuser.getColumnIndex(DbLogin.USER_ID));
        } while (cuser.moveToNext());
        sqLiteDatabaseDbLoginRead.close();

        connection_code = getIntent().getExtras().getInt("C_code");

        tv_connection_code_value.setText(String.valueOf(connection_code));

        tv_meter_no_val.setText(getIntent().getExtras().getString("Meter_no"));
        book_no=getIntent().getExtras().getString("Book_no");
        tv_book_code_val.setText(book_no);
        tv_cons_name_val.setText(getIntent().getExtras().getString("C_name"));
        tv_route_no_val.setText(String.valueOf(getIntent().getExtras().getInt("Route_no")));
        tv_cgl_no_val.setText(getIntent().getExtras().getString("Cgl_no"));

        String dpath="/MMR";
        f = new File(Environment.getExternalStorageDirectory() + dpath);
        if(!f.isDirectory()) {
             f.mkdir();
        }
        String dpath2="/MMR/"+book_no;
        f2_image_directory = new File(Environment.getExternalStorageDirectory() + dpath2);
        if( !f2_image_directory.isDirectory()){
             f2_image_directory.mkdir();
        }
        /*tv_address_val.setText(getIntent().getExtras().getString("Address"));
        tv_mob_no_val.setText(getIntent().getExtras().getString("Mobile_no"));
        tv_feeder_name_val.setText(getIntent().getExtras().getString("Feeder_name"));*/

///////////////Image file Capture Intent
        /*File f=new File(Environment.getExternalStorageDirectory(),getIntent().getExtras().getString("C_code").toString()+".PNG");
        fileUri= Uri.fromFile(f);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);*/
//////////////////////////////////////////////////////////////////////////Spinner creation//////////////////////////////////////////
///////////////////////////
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.meter_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_meter_status_v.setAdapter(adapter);
        sp_meter_status_v.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_meter_status = sp_meter_status_v.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Remark, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_remark_val.setAdapter(adapter1);
        sp_remark_val.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_remarks = sp_remark_val.getSelectedItem().toString();

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
//////////////////////////////////////////////////////////////////////////////////////////Spinner.................................................................................

////////////////////////////////Image Capture Button///////////////////////

        b_image_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File f = new File(f2_image_directory, tv_connection_code_value.getText().toString() + ".png");
                fileUri = Uri.fromFile(f);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, 1);
            }
        });
//////////////////////////////////////////////////...............................................................................
        /*b_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_id=Integer.valueOf(tv_consumer_code.getText().toString());
                Toast.makeText(MyReading.this,"Consumer Id:"+c_id,Toast.LENGTH_LONG).show();
                Cursor c=dbConnection.returnImage(c_id);
                Toast.makeText(MyReading.this,"Cursor length:"+c.getCount(),Toast.LENGTH_SHORT).show();
                c.moveToFirst();
                byte [] b=c.getBlob(c.getColumnIndex(DbConnection.IMAGE));
                if(b !=null){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap;
                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, options);
                    iv_preview.setImageBitmap(bitmap);
                }
                else {
                    Toast.makeText(MyReading.this,"Image not found in local database !",Toast.LENGTH_SHORT).show();
                }
            sqLiteDatabase.close();
            }
        });*/
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


                if (et_Meter_read_val.getText().length() != 0) {
                    localDatabaseUpdate();
                } else {
                    Toast.makeText(MyReading.this, "Kindly insert the meter reading.", Toast.LENGTH_SHORT).show();
                    can_be_saved = false;
                }

                if (readingUpdate) {
                    //deleteLastFromDCIM();
                    //deleteLastPhotoTaken();
                    Toast.makeText(MyReading.this, "Local database get successfully updated !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MyReading.this, "Problem in updating local database !", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
    private boolean deleteLastFromDCIM() {
        boolean success = false;
        try {
            //Samsungs:
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM/Camera");
            if(!folder.exists()){ //other phones:
                File[] subfolders = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM").listFiles();
                for(File subfolder : subfolders){
                    if(subfolder.getAbsolutePath().contains("100")){
                        folder = subfolder;
                        break;
                    }
                }
                if(!folder.exists())
                    return false;
            }

            File[] images = folder.listFiles();
            File latestSavedImage = images[0];
            for (int i = 1; i < images.length; ++i) {
                if (images[i].lastModified() > latestSavedImage.lastModified()) {
                    latestSavedImage = images[i];
                }
            }
            success = latestSavedImage.delete();
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return success;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        sqLiteDatabase.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sqLiteDatabase.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    void localDatabaseUpdate() {


        /*String remark1=null;
        remark1=et_rem_new.getText().toString();
        //Toast.makeText(MyReading.this, "Connection code is not available in the local database !", Toast.LENGTH_LONG).show();
        contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
        contentValues.put(DbConnection.REMARKS, s_remarks);
        contentValues.put(DbConnection.STATUS, s_meter_status);
        contentValues.put(DbConnection.METER_TYPE, s_meter_type);
        contentValues.put(DbConnection.DATE, currentDateandTime1);
        contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
        if (remark1.length() > 0){
            contentValues.put(DbConnection.REMARK1,remark1);
            //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
        }else{
            contentValues.put(DbConnection.REMARK1,"NA");
            // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
        }
        if (bytes != null) {
            contentValues.put(DbConnection.IMAGE, bytes);
        } else {
            Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

        }
        contentValues.put(DbConnection.USER, user);
        Boolean res=dbConnection.updateReadingDetails(contentValues, connection_code);
        if (res){
            // Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

        }*/




/*       String meter_status_previous=null;
                meter_status_previous = dbConnection.getMeterStatusValuePreviouslyCommited(connection_code);
        if((meter_status_previous != null) && !meter_status_previous.isEmpty()) {
            if (meter_status_previous.equals(s_meter_status)) {
                Toast.makeText(MyReading.this, "Meter Reading aborted !" + "\n" + "Similar meter status found.", Toast.LENGTH_LONG).show();

            } else {
                */
        String remark1=null;
        remark1=et_rem_new.getText().toString();
                if ((s_meter_status.equals("LOCK"))) {
                    //  contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
                    contentValues.put(DbConnection.REMARKS, s_remarks);
                    contentValues.put(DbConnection.STATUS, s_meter_status);
                    contentValues.put(DbConnection.METER_TYPE, s_meter_type);
                    contentValues.put(DbConnection.DATE, currentDateandTime1);
                    contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
                    if (remark1.length() > 0){
                        contentValues.put(DbConnection.REMARK1,remark1);
                        //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
                    }else{
                        contentValues.put(DbConnection.REMARK1,"NA");
                        // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
                    }
                    if (bytes != null) {
                        contentValues.put(DbConnection.IMAGE, bytes);
                    } else {
                        Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

                    }
                    contentValues.put(DbConnection.USER, user);
                    //dbConnection.updateReadingDetails(contentValues, connection_code);
                    Boolean res1=dbConnection.updateReadingDetails(contentValues, connection_code);
                    if (res1){
                        Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

                    }
                }
                if ((s_meter_status.equals("NO METER"))) {
                    // contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
                    contentValues.put(DbConnection.REMARKS, s_remarks);
                    contentValues.put(DbConnection.STATUS, s_meter_status);
                    contentValues.put(DbConnection.METER_TYPE, s_meter_type);
                    contentValues.put(DbConnection.DATE, currentDateandTime1);
                    contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
                    if (remark1.length() > 0){
                        contentValues.put(DbConnection.REMARK1,remark1);
                        //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
                    }else{
                        contentValues.put(DbConnection.REMARK1,"NA");
                        // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
                    }
                    if (bytes != null) {
                        contentValues.put(DbConnection.IMAGE, bytes);
                    } else {
                        Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

                    }
                    contentValues.put(DbConnection.USER, user);
                    Boolean res1=dbConnection.updateReadingDetails(contentValues, connection_code);
                    if (res1){
                        Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

                    }
                }
                if ((s_meter_status.equals("POWER OFF"))) {
                    // contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
                    contentValues.put(DbConnection.REMARKS, s_remarks);
                    contentValues.put(DbConnection.STATUS, s_meter_status);
                    contentValues.put(DbConnection.METER_TYPE, s_meter_type);
                    contentValues.put(DbConnection.DATE, currentDateandTime1);
                    contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
                    if (remark1.length() > 0){
                        contentValues.put(DbConnection.REMARK1,remark1);
                        //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
                    }else{
                        contentValues.put(DbConnection.REMARK1,"NA");
                        // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
                    }
                    if (bytes != null) {
                        contentValues.put(DbConnection.IMAGE, bytes);
                    } else {
                        Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

                    }
                    contentValues.put(DbConnection.USER, user);
                    Boolean res1=dbConnection.updateReadingDetails(contentValues, connection_code);
                    if (res1){
                        Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

                    }
                    //dbConnection.updateReadingDetails(contentValues, connection_code);
                }
                if ((s_meter_status.equals("NO DISPLAY"))) {
                    // contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
                    contentValues.put(DbConnection.REMARKS, s_remarks);
                    contentValues.put(DbConnection.STATUS, s_meter_status);
                    contentValues.put(DbConnection.METER_TYPE, s_meter_type);
                    contentValues.put(DbConnection.DATE, currentDateandTime1);
                    contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
                    if (remark1.length() > 0){
                        contentValues.put(DbConnection.REMARK1,remark1);
                        //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
                    }else{
                        contentValues.put(DbConnection.REMARK1,"NA");
                        // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
                    }
                    if (bytes != null) {
                        contentValues.put(DbConnection.IMAGE, bytes);
                    } else {
                        Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

                    }
                    contentValues.put(DbConnection.USER, user);
                    Boolean res1=dbConnection.updateReadingDetails(contentValues, connection_code);
                    if (res1){
                        Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

                    }
                    //dbConnection.updateReadingDetails(contentValues, connection_code);
                }
                if ((s_meter_status.equals("NORMAL"))) {
                    contentValues.put(DbConnection.METER_READING, et_Meter_read_val.getText().toString());
                    contentValues.put(DbConnection.REMARKS, s_remarks);
                    contentValues.put(DbConnection.STATUS, s_meter_status);
                    contentValues.put(DbConnection.METER_TYPE, s_meter_type);
                    contentValues.put(DbConnection.DATE, currentDateandTime1);
                    contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
                    if (remark1.length() > 0){
                        contentValues.put(DbConnection.REMARK1,remark1);
                        //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
                    }else{
                        contentValues.put(DbConnection.REMARK1,"NA");
                        // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
                    }
                    if (bytes != null) {
                        contentValues.put(DbConnection.IMAGE, bytes);
                    } else {
                        Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

                    }
                    contentValues.put(DbConnection.USER, user);
                    Boolean res1=dbConnection.updateReadingDetails(contentValues, connection_code);
                    if (res1){
                        Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

                    }
                 //   dbConnection.updateReadingDetails(contentValues, connection_code);
                }
                if ((s_meter_status.equals("DEFECTIVE"))) {
                    contentValues.put(DbConnection.METER_READING, et_Meter_read_val.getText().toString());
                    contentValues.put(DbConnection.REMARKS, s_remarks);
                    contentValues.put(DbConnection.STATUS, s_meter_status);
                    contentValues.put(DbConnection.METER_TYPE, s_meter_type);
                    contentValues.put(DbConnection.DATE, currentDateandTime1);
                    contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
                    if (remark1.length() > 0){
                        contentValues.put(DbConnection.REMARK1,remark1);
                        //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
                    }else{
                        contentValues.put(DbConnection.REMARK1,"NA");
                        // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
                    }
                    if (bytes != null) {
                        contentValues.put(DbConnection.IMAGE, bytes);
                    } else {
                        Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

                    }
                    contentValues.put(DbConnection.USER, user);
                    Boolean res1=dbConnection.updateReadingDetails(contentValues, connection_code);
                    if (res1){
                        Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

                    }
                    //dbConnection.updateReadingDetails(contentValues, connection_code);
                }
                if ((s_meter_status.equals("DIAL COMPLETE"))) {
                    contentValues.put(DbConnection.METER_READING, et_Meter_read_val.getText().toString());
                    contentValues.put(DbConnection.REMARKS, s_remarks);
                    contentValues.put(DbConnection.STATUS, s_meter_status);
                    contentValues.put(DbConnection.METER_TYPE, s_meter_type);
                    contentValues.put(DbConnection.DATE, currentDateandTime1);
                    contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
                    if (remark1.length() > 0){
                        contentValues.put(DbConnection.REMARK1,remark1);
                        //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
                    }else{
                        contentValues.put(DbConnection.REMARK1,"NA");
                        // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
                    }
                    if (bytes != null) {
                        contentValues.put(DbConnection.IMAGE, bytes);
                    } else {
                        Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

                    }
                    contentValues.put(DbConnection.USER, user);
                    Boolean res1=dbConnection.updateReadingDetails(contentValues, connection_code);
                    if (res1){
                        Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

                    }
                    //dbConnection.updateReadingDetails(contentValues, connection_code);
                }


        }/*else{
            String remark1=null;
            remark1=et_rem_new.getText().toString();
            Toast.makeText(MyReading.this, "Connection code is not available in the local database !", Toast.LENGTH_LONG).show();
            contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
            contentValues.put(DbConnection.REMARKS, s_remarks);
            contentValues.put(DbConnection.STATUS, s_meter_status);
            contentValues.put(DbConnection.METER_TYPE, s_meter_type);
            contentValues.put(DbConnection.DATE, currentDateandTime1);
            contentValues.put(DbConnection.FEEDER_LOCATION, devicecurrentLocation);
            if (remark1.length() > 0){
                contentValues.put(DbConnection.REMARK1,remark1);
                //   Toast.makeText(MyReadingByBookNo.this,"Remark1 in CV !",Toast.LENGTH_LONG).show();
            }else{
                contentValues.put(DbConnection.REMARK1,"NA");
                // Toast.makeText(MyReadingByBookNo.this,"NA in CV!",Toast.LENGTH_LONG).show();
            }
            if (bytes != null) {
                contentValues.put(DbConnection.IMAGE, bytes);
            } else {
                Toast.makeText(MyReading.this, "Image not get captured successfully !", Toast.LENGTH_LONG).show();

            }
            contentValues.put(DbConnection.USER, user);
            Boolean res=dbConnection.updateReadingDetails(contentValues, connection_code);
            if (res){
               // Toast.makeText(MyReading.this, "Local database updated !", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(MyReading.this, "Sorry, local database not get updated",Toast.LENGTH_LONG).show();

            }
        }
    }*/


            /*contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
            contentValues.put(DbConnection.REMARKS,s_remarks);
            contentValues.put(DbConnection.STATUS,s_meter_status);
            contentValues.put(DbConnection.METER_TYPE,s_meter_type);
            contentValues.put(DbConnection.DATE,currentDateandTime1);
            contentValues.put(DbConnection.FEEDER_LOCATION,devicecurrentLocation);
            if(bytes != null){
                contentValues.put(DbConnection.IMAGE,bytes);
            }else{
                Toast.makeText(MyReading.this,"Image not get captured successfully !",Toast.LENGTH_LONG).show();

            }
            contentValues.put(DbConnection.USER,user);
            dbConnection.updateReadingDetails(contentValues,connection_code);*/

        /*contentValues.put(DbConnection.METER_READING,et_Meter_read_val.getText().toString());
        contentValues.put(DbConnection.REMARKS,s_remarks);
        contentValues.put(DbConnection.STATUS,s_meter_status);
        contentValues.put(DbConnection.METER_TYPE,s_meter_type);
        contentValues.put(DbConnection.DATE,currentDateandTime1);
        contentValues.put(DbConnection.FEEDER_LOCATION,devicecurrentLocation);
        if(bytes != null){
            contentValues.put(DbConnection.IMAGE,bytes);
        }else{
            Toast.makeText(MyReading.this,"Image not get captured successfully !",Toast.LENGTH_LONG).show();

        }
        contentValues.put(DbConnection.USER,user);
        dbConnection.updateReadingDetails(contentValues,connection_code);*/




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
// Check if the result includes a thumbnail Bitmap
            if (resultCode == RESULT_OK) {


                if (data != null) {
                    if (data.hasExtra("data")) {

                        Bitmap bitmap = data.getParcelableExtra("data");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                        //Toast.makeText(MyReading.this,"file compressed ",Toast.LENGTH_LONG).show();
                        bytes = bos.toByteArray();

                        //contentValues.put(DbConnection.IMAGE, bytes);//////////////////////////////////////Image get stored in content values if data returns image value..................

                        iv_preview.setImageBitmap(bitmap);
                    }
                } else {
// If there is no thumbnail image data, the image
// will have been stored in the target output URI.
// Resize the full image to fit in out image view.
                    //int width = iv_preview.getWidth();
                    //int height = iv_preview.getHeight();
                    BitmapFactory.Options factoryOptions = new
                            BitmapFactory.Options();
                    factoryOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fileUri.getPath(),
                            factoryOptions);
                    int imageWidth = factoryOptions.outWidth;
                    int imageHeight = factoryOptions.outHeight;
// Determine how much to scale down the image
                    //int scaleFactor = Math.min(imageWidth / width,
                           // imageHeight / height);
// Decode the image file into a Bitmap sized to fill the View
                    factoryOptions.inJustDecodeBounds = false;
                    factoryOptions.inSampleSize = 14;
                    factoryOptions.inPurgeable = true;
                    Bitmap bitmap =
                            BitmapFactory.decodeFile(fileUri.getPath(),
                                    factoryOptions);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    //Toast.makeText(MyReading.this,"file compressed ",Toast.LENGTH_LONG).show();
                    bytes= bos.toByteArray();
                    //contentValues.put(DbConnection.IMAGE,bArray);//////////////////////////////////////////////////Image get stored in contentValues when using Uri..............................
                    iv_preview.setImageBitmap(bitmap);
                    deleteLastFromDCIM();
                   // deleteLastPhotoTaken();
                }

            }
        }
    }

    void fullNaming(){
        tv_connection_code= (TextView) findViewById(R.id.tv_consumer_valid_code_name);
        tv_connection_code_value= (TextView) findViewById(R.id.tv_Connection_id_value);
        tv_meter_no= (TextView) findViewById(R.id.tv_meter_no);
        tv_meter_no_val= (TextView) findViewById(R.id.tv_meter_no_val);
        tv_meter_reading=(TextView)findViewById(R.id.tv_meter_reading);

        tv_book_code= (TextView) findViewById(R.id.tv_boo_no);
        tv_book_code_val= (TextView) findViewById(R.id.tv_boo_no_val);

        tv_route_no= (TextView) findViewById(R.id.tv_route_no);
        tv_route_no_val= (TextView) findViewById(R.id.tv_route_no_val);

        tv_cons_name= (TextView) findViewById(R.id.tv_consumer_name);
        tv_cons_name_val= (TextView) findViewById(R.id.tv_consumer_name_val);
        /*tv_address= (TextView) findViewById(R.id.tv_address);
        tv_address_val= (TextView) findViewById(R.id.tv_address_val);
        tv_mob_no= (TextView) findViewById(R.id.tv_mob_no);
        tv_mob_no_val= (TextView) findViewById(R.id.tv_mob_no_val);*/
        tv_meter_status= (TextView) findViewById(R.id.tv_meter_stat);
        tv_remark= (TextView) findViewById(R.id.tv_remark);
        tv_meter_type= (TextView) findViewById(R.id.tv_met_type);
        /*tv_feeder_name= (TextView) findViewById(R.id.tv_feeder_name);
        tv_feeder_name_val= (TextView) findViewById(R.id.tv_feeder_name_val);*/
        tv_cgl_no= (TextView) findViewById(R.id.tv_cgl_no);
        tv_cgl_no_val= (TextView) findViewById(R.id.tv_cgl_no_val);
        tv_image= (TextView) findViewById(R.id.tv_image);
        et_Meter_read_val= (EditText) findViewById(R.id.et_meter_reading);
        et_Meter_read_val.requestFocus();
        et_rem_new= (EditText) findViewById(R.id.et_rem);
        sp_meter_status_v= (Spinner) findViewById(R.id.sp_meter_stat_val);
        sp_remark_val= (Spinner) findViewById(R.id.sp_remark_val);
        sp_meter_type= (Spinner) findViewById(R.id.sp_meter_type);
        b_image_click= (ImageButton) findViewById(R.id.b_image_click);
        b_commit= (Button) findViewById(R.id.b_commit_reading);
        iv_preview= (ImageView) findViewById(R.id.iv_image_preview);
        /*b_preview= (Button) findViewById(R.id.b_image_preview);*/
    }

    public void alertMessage(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MyReading.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MyReading.this.startActivity(intent);
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
