package com.general.vvvv.cmr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.general.vvvv.cmr.htc.HtcActivityObjective;
import com.general.vvvv.cmr.ltp.LtpActivityObjective;

/**
 * Created by comp on 09/10/2014.
 */
public class ElectricityDeptDaman extends ActionBarActivity {
    Button b_dom,b_htc,b_ltc;
    TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elec_dept);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        b_dom= (Button) findViewById(R.id.b_domestic);
        b_htc= (Button) findViewById(R.id.b_htc);
        b_ltc= (Button) findViewById(R.id.b_ltc);

        b_dom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ElectricityDeptDaman.this,MyActivity.class);
                startActivity(intent);
            }
        });
        b_htc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ElectricityDeptDaman.this, HtcActivityObjective.class);
                startActivity(intent);
                //Toast.makeText(ElectricityDeptDaman.this,"Module not available.", Toast.LENGTH_SHORT).show();
            }
        });
        b_ltc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ElectricityDeptDaman.this, LtpActivityObjective.class);
                startActivity(intent);
                //Toast.makeText(ElectricityDeptDaman.this,"Module not available.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
