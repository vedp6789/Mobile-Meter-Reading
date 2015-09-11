package com.general.vvvv.cmr.htc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.fragments.HTC_Fragment.FrDiHtcDownload;
import com.fragments.HTC_Fragment.FrDiHtcReading;
import com.fragments.HTC_Fragment.FrDiHtcUpload;
import com.general.vvvv.cmr.R;

/**
 * Created by comp on 13/01/2015.
 */
public class HtcActivityObjective extends ActionBarActivity implements Button.OnClickListener {
    Button b_status,b_download,b_reading,b_upload,b_about;
    void create(){
        b_status= (Button) findViewById(R.id.ht_b_status);
        b_download= (Button) findViewById(R.id.ht_b_download);
        b_reading= (Button) findViewById(R.id.ht_b_reading);
        b_upload= (Button) findViewById(R.id.ht_b_upload);
        b_about= (Button) findViewById(R.id.ht_b_help);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ht_b_status:
                Intent intent1=new Intent(HtcActivityObjective.this,HtcActivityStatus.class);
                startActivity(intent1);
                break;
            case R.id.ht_b_download:
                FragmentManager fragmentManager=getSupportFragmentManager();
                FrDiHtcDownload frDiHtcDownload=new FrDiHtcDownload();
                frDiHtcDownload.show(fragmentManager,null);
                break;
            case R.id.ht_b_reading:
                Intent intent=new Intent(HtcActivityObjective.this,HtcActivityReadingType.class);
                startActivity(intent);
                break;
            case R.id.ht_b_upload:
                FragmentManager fragmentManager2=getSupportFragmentManager();
                FrDiHtcUpload frDiHtcUpload=new FrDiHtcUpload();
                frDiHtcUpload.show(fragmentManager2,null);
                break;
            case R.id.ht_b_help:
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
        setContentView(R.layout.ht_activity_objective);
        System.out.println("HTC_Activity_Objective"+"\t: onCreate");
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
        System.out.println("HTC_Activity_Objective"+"\t: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("HTC_Activity_Objective"+"\t: onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("HTC_Activity_Objective"+"\t: onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HTC_Activity_Objective"+"\t: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("HTC_Activity_Objective"+"\t: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("HTC_Activity_Objective"+"\t: onDestroy");
    }


}
