package com.fragments.LTP_Fragment;

import android.annotation.TargetApi;
import android.app.Dialog;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.general.SQLite.DbLTPReading;
import com.general.vvvv.cmr.R;
import com.general.vvvv.cmr.ltp.LtpActivityAreaConsumerList;

/**
 * Created by vvvv on 04-02-2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FrDiLtpReadingByArea extends DialogFragment implements View.OnClickListener  {
    EditText et_area;
    Button b_search,b_cancel;
    DbLTPReading dbLTPReading;
    SQLiteDatabase sqLiteDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbLTPReading=new DbLTPReading(getActivity(),DbLTPReading.DB_NAME,null,DbLTPReading.DB_VERSION);
        sqLiteDatabase=null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.f_di_ltp_read_area,container,false);
        et_area= (EditText) view.findViewById(R.id.et_area);
        b_search= (Button) view.findViewById(R.id.b_s_area);
        b_cancel= (Button) view.findViewById(R.id.b_c_area);
        b_search.setOnClickListener(this);
        b_cancel.setOnClickListener(this);
        Dialog dialog=getDialog();
        dialog.setTitle(getString(R.string.acc_valid_dialog_title));
        dialog.setCanceledOnTouchOutside(false);
        return view;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.b_s_area:{
                if(et_area.getText().length()==0){
                    Toast.makeText(getActivity(), "Insert the area code.", Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    sqLiteDatabase=dbLTPReading.getReadableDatabase();
                    String area=et_area.getText().toString().toUpperCase();
                    boolean b=dbLTPReading.areaValidation(area, sqLiteDatabase);
                    if(b){
                        boolean b1=dbLTPReading.noOfConForReading(area);
                        if(b1){
                            dismiss();
                            Intent intent=new Intent(getActivity(),LtpActivityAreaConsumerList.class);
                            intent.putExtra("area",area);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getActivity(),"Reading for all the consumers of this record completed."+"\n Either partially or fully completed.",Toast.LENGTH_LONG).show();
                        }


                    }else{
                        Toast.makeText(getActivity(),"Area code not available.",Toast.LENGTH_SHORT).show();

                    }
                    sqLiteDatabase.close();
                }
                break;
            }
            case R.id.b_c_area:{
                dismiss();
                break;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
