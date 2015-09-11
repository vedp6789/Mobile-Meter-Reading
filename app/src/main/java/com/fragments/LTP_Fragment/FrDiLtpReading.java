package com.fragments.LTP_Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.general.SQLite.DbLTPReading;
import com.general.vvvv.cmr.R;
import com.general.vvvv.cmr.ltp.LtpActivityReading;

/**
 * Created by comp on 15/01/2015.
 */
public class FrDiLtpReading extends DialogFragment implements View.OnClickListener{
    SQLiteDatabase sqLiteDatabase;
    DbLTPReading dbLTPReading=null;
    EditText et_Consumer;
    Button b_search,b_cancel;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_s_ltp_no:
                if(et_Consumer.getText().length()==0){
                    Toast.makeText(getActivity(), "Insert Consumer Id.", Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    sqLiteDatabase=dbLTPReading.getReadableDatabase();
                    boolean b=dbLTPReading.consumerIdValidation(Integer.parseInt(et_Consumer.getText().toString()), sqLiteDatabase);
                    if(b){

                        //Toast.makeText(getActivity(),"Search Successful",Toast.LENGTH_SHORT).show();
                        dismiss();
                        Intent intent=new Intent(getActivity(), LtpActivityReading.class);
                        Log.d("consumer_id", et_Consumer.getText().toString());
                        intent.putExtra("CONSUMER_ID",et_Consumer.getText().toString());
                        //dismiss();
                        startActivity(intent);

                    }else{
                        Toast.makeText(getActivity(),"Consumer Id. not available.",Toast.LENGTH_SHORT).show();

                    }
                    sqLiteDatabase.close();
                    break;
                }

            case R.id.b_c_ltp_no:
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbLTPReading=new DbLTPReading(getActivity(),DbLTPReading.DB_NAME,null,DbLTPReading.DB_VERSION);
        sqLiteDatabase=null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.f_d_ltp_reading,container,false);
        et_Consumer= (EditText) view.findViewById(R.id.et_ltp_no);
        b_search= (Button) view.findViewById(R.id.b_s_ltp_no);
        b_cancel= (Button) view.findViewById(R.id.b_c_ltp_no);
        b_cancel.setOnClickListener(this);
        b_search.setOnClickListener(this);

        Dialog dialog=getDialog();
        dialog.setTitle(getString(R.string.acc_valid_dialog_title));
        dialog.setCanceledOnTouchOutside(false);
        return view;
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
