package com.general.vvvv.cmr;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.fragments.FDialogCustomerValidation;
import com.fragments.FDialogReadingBookValidation;
import com.fragments.FDialogReadingCGIValidation;

/**
 * Created by comp on 28/09/2014.
 */
public class MyReadingType extends ActionBarActivity {
    Button b_read_ConnId, b_read_Book, b_read_CGL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_type);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        b_read_ConnId = (Button) findViewById(R.id.b_read_By_CoonId);
        b_read_Book = (Button) findViewById(R.id.b_read_By_Book);
        b_read_CGL = (Button) findViewById(R.id.b_read_By_CGL);

        b_read_ConnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FDialogCustomerValidation fDialogAccValidation = new FDialogCustomerValidation();
                fDialogAccValidation.show(fragmentManager, null);
            }
        });

        b_read_Book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FDialogReadingBookValidation fDialogReadingBookValidation = new FDialogReadingBookValidation();
                fDialogReadingBookValidation.show(fragmentManager, null);
            }
        });
        b_read_CGL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FDialogReadingCGIValidation fDialogReadingCGIValidation = new FDialogReadingCGIValidation();
                fDialogReadingCGIValidation.show(fragmentManager, null);
            }
        });
    }
}
