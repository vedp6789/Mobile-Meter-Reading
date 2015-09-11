package com.general.vvvv.cmr.ltp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fragments.HTC_Fragment.FrDiHtcDownload;
import com.fragments.LTP_Fragment.FrDiLtpDownload;
import com.fragments.LTP_Fragment.FrDiLtpReading;
import com.fragments.LTP_Fragment.FrDiLtpUpload;
import com.general.vvvv.cmr.R;

/**
 * Created by comp on 13/01/2015.
 */
public class LtpActivityObjective extends ActionBarActivity implements Button.OnClickListener {
    Button b_status,b_download,b_reading,b_upload,b_about;
    void create(){
        b_status= (Button) findViewById(R.id.lt_b_status);
        b_download= (Button) findViewById(R.id.lt_b_download);
        b_reading= (Button) findViewById(R.id.lt_b_reading);
        b_upload= (Button) findViewById(R.id.lt_b_ltp_upload);
        b_about= (Button) findViewById(R.id.lt_b_help);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lt_b_status:
                Intent intent=new Intent(LtpActivityObjective.this,LtpActivityStatus.class);
                startActivity(intent);
                break;
            case R.id.lt_b_download:
                FragmentManager fragmentManager=getSupportFragmentManager();
                FrDiLtpDownload frDiLtpDownload=new FrDiLtpDownload();
                frDiLtpDownload.show(fragmentManager,null);
                break;
            case R.id.lt_b_reading:
                Intent intent1=new Intent(LtpActivityObjective.this,LtpActivityReadingType.class);
                startActivity(intent1);
                break;
            case R.id.lt_b_ltp_upload:
                Log.i("upload click","Ltp");
                //Toast.makeText(LtpActivityObjective.this,"upload clik",Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager2=getSupportFragmentManager();
                FrDiLtpUpload frDiLtpUpload=new FrDiLtpUpload();
                frDiLtpUpload.show(fragmentManager2,null);
                break;
            case R.id.lt_b_help:
                showCustomAlert();
                break;
        }
    }


    public void showCustomAlert() {

        Context context = getApplicationContext();
        // Create layout inflator object to inflate toast.xml file
        LayoutInflater inflater = getLayoutInflater();

        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.toast_about, (ViewGroup) findViewById(R.id.root_toast_about));

        Toast toast = new Toast(context);

        // Set layout to toast

        toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastRoot);
        toast.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_activity_objective);
        System.out.println("LTP_Activity_Objective"+"\t: onCreate");
        create();
        b_status.setOnClickListener(this);
        b_download.setOnClickListener(this);
        b_reading.setOnClickListener(this);
        b_upload.setOnClickListener(this);
        b_about.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("LTP_Activity_Objective"+"\t: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("LTP_Activity_Objective"+"\t: onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("LTP_Activity_Objective"+"\t: onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("LTP_Activity_Objective"+"\t: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("LTP_Activity_Objective"+"\t: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("LTP_Activity_Objective"+"\t: onDestroy");
    }


}
