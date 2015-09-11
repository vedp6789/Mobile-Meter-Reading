package com.general.vvvv.cmr.ltp;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.fragments.LTP_Fragment.FrDiLtpReading;
import com.fragments.LTP_Fragment.FrDiLtpReadingByArea;
import com.general.vvvv.cmr.R;

/**
 * Created by vvvv on 04-02-2015.
 */
public class LtpActivityReadingType extends ActionBarActivity {
    Button b_consumer_id, b_area_code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltp_reading_type);
        b_consumer_id = (Button) findViewById(R.id.b_consumer);
        b_area_code = (Button) findViewById(R.id.b_read_By_Area);
        b_consumer_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FrDiLtpReading frDiLtpReading = new FrDiLtpReading();
                frDiLtpReading.show(fragmentManager1, null);


            }
        });

        b_area_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getSupportFragmentManager();
                FrDiLtpReadingByArea frDiLtpReadingByArea=new FrDiLtpReadingByArea();
                frDiLtpReadingByArea.show(fragmentManager,null);

            }
        });
    }
}
