package com.general.vvvv.cmr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fragments.FDialogDownload;
import com.fragments.FDialogUpdate;


public class MyActivity extends ActionBarActivity implements Button.OnClickListener/*,FDialogAccValidation.MyActivityHelper*/ {
    Button b_status, b_reading, b_download, b_view, b_upload, b_help;//, b_exit;
    static Boolean aBoolean = false;
    //boolean consumerSearchValidated1=false;

    @Override
    protected void onStart() {
        super.onStart();
    }


    /* public void cosumerSearchValidate(){

            consumerSearchValidated1=true;
        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        init();
        b_status.setOnClickListener(this);
        b_reading.setOnClickListener(this);
        b_download.setOnClickListener(this);
        b_upload.setOnClickListener(this);
        b_help.setOnClickListener(this);
        //  b_exit.setOnClickListener(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void init() {
        b_status = (Button) findViewById(R.id.b_status);
        b_reading = (Button) findViewById(R.id.b_reading);
        b_download = (Button) findViewById(R.id.b_download);
        b_upload = (Button) findViewById(R.id.b_upload);
        b_help = (Button) findViewById(R.id.b_help);
        //  b_exit = (Button) findViewById(R.id.b_exit);
        //b_view= (Button) findViewById(R.id.b_view_s_reading);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_status:
                //Toast.makeText(MyActivity.this, "status", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MyActivity.this, MyStatus.class);
                startActivity(i);
                break;
            case R.id.b_reading:
                //Toast.makeText(MyActivity.this, "reading", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MyActivity.this, MyReadingType.class);
                startActivity(intent);

                /*if(consumerSearchValidated1){
                    Intent intent=new Intent(MyActivity.this,MyReading.class);
                    startActivity(intent);
                }*/
                break;
            case R.id.b_download:
                // Toast.makeText(MyActivity.this, "download", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager2 = getSupportFragmentManager();
                FDialogDownload fDialogDownload = new FDialogDownload();
                fDialogDownload.show(fragmentManager2, null);
                /*Intent intent=new Intent(MyActivity.this,MyDownload.class);
                startActivity(intent);*/
                break;
            case R.id.b_upload:
                //Toast.makeText(MyActivity.this,"Server is under development !",Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FDialogUpdate fDialogUpdate = new FDialogUpdate();
                fDialogUpdate.show(fragmentManager1, null);

                break;
            case R.id.b_help:

                // Toast.makeText(MyActivity.this, "Contact the Application provider !", Toast.LENGTH_SHORT).show();
                showCustomAlert();
                /*Intent intent1=new Intent(MyActivity.this,ServerSavedImageView.class);
                startActivity(intent1);*/
                break;
            /*case R.id.b_exit:
                //Toast.makeText(MyActivity.this, "exit", Toast.LENGTH_SHORT).show();
                finish();
                break;*/
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

    /*@Override
    public Boolean helper() {
        consumerSearchValidated=true;
        return true;
    }*/
}
