package com.fragments;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.general.SQLite.DbConnection;
import com.general.vvvv.cmr.MyReadingByCGL;
import com.general.vvvv.cmr.R;

/**
 * Created by Dell on 20-11-2014.
 */
public class FDialogReadingCGIValidation extends DialogFragment implements View.OnClickListener {
    SQLiteDatabase sqLiteDatabase;
    DbConnection dbConnection;
    EditText et_cgl;
    Button b_cgl,b_conn,b_cancel;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyDbErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_TITLE,0);
        dbConnection=new DbConnection(getActivity(),DbConnection.DB_NAME,null,DbConnection.DB_VERSION,new MyDbErrorHandler());

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_d_r_cgi_validation,container,false);
        et_cgl= (EditText) view.findViewById(R.id.et_read_d_cgi_no);
        b_cgl= (Button) view.findViewById(R.id.b_search_cgi);
        b_conn= (Button) view.findViewById(R.id.b_search_conn);
        b_cancel= (Button) view.findViewById(R.id.b_read_by_cgi_cancel);
        b_cancel.setOnClickListener(this);
        b_cgl.setOnClickListener(this);
        b_conn.setOnClickListener(this);

        Dialog dialog=getDialog();
        dialog.setTitle(getString(R.string.acc_valid_dialog_title));
        dialog.setCanceledOnTouchOutside(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_search_cgi:
                if(et_cgl.getText().length()==0){
                    Toast.makeText(getActivity(), "Insert CGL No.", Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    sqLiteDatabase=dbConnection.getReadableDatabase();
                    boolean b=dbConnection.CGL_NoValidation(et_cgl.getText().toString(), sqLiteDatabase);
                    if(b){
                        sqLiteDatabase=dbConnection.getReadableDatabase();
                       Cursor cur1=dbConnection.getDetailsOfCGL(et_cgl.getText().toString(), sqLiteDatabase);
                      cur1.moveToFirst();
                      String meter_reading=cur1.getString(cur1.getColumnIndex(DbConnection.METER_READING));
                      String meter_status=cur1.getString(cur1.getColumnIndex(DbConnection.STATUS));
                      String book_no=cur1.getString(cur1.getColumnIndex(DbConnection.BOOK_NO));
                        //Toast.makeText(getActivity(),"Search Successful",Toast.LENGTH_SHORT).show();
                       if(((meter_status != null) && (meter_status.equals("LOCK")) && (meter_status.equals("NO METER"))&& (meter_status.equals("POWER OFF"))&& (meter_status.equals("NO DISPLAY")) ) || (meter_reading != null)) {
                           Toast.makeText(getActivity(),"Meter reading is already done.",Toast.LENGTH_SHORT).show();
                       }else{
                           Intent intent=new Intent(getActivity(), MyReadingByCGL.class);
                           intent.putExtra("r_type","CGL");
                           intent.putExtra("cgl_no",et_cgl.getText().toString());
                           intent.putExtra("book_no",book_no);
                           dismiss();
                           startActivity(intent);
                       }

                       // sqLiteDatabase.close();
                    }else{
                        Toast.makeText(getActivity(),"CGL No. is not available !",Toast.LENGTH_SHORT).show();

                    }
                    sqLiteDatabase.close();
                    break;
                }
            case R.id.b_search_conn:
                if(et_cgl.getText().length()==0){
                    Toast.makeText(getActivity(), "Insert Connection Code", Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    sqLiteDatabase=dbConnection.getReadableDatabase();
                    boolean b=dbConnection.Conn_NoValidation(et_cgl.getText().toString(), sqLiteDatabase);
                    if(b) {
                        sqLiteDatabase = dbConnection.getReadableDatabase();

                        Cursor cur1=dbConnection.getDetailsOfConn(et_cgl.getText().toString(), sqLiteDatabase);
                        cur1.moveToFirst();
                        String meter_reading=cur1.getString(cur1.getColumnIndex(DbConnection.METER_READING));
                        String meter_status=cur1.getString(cur1.getColumnIndex(DbConnection.STATUS));
                        String book_no=cur1.getString(cur1.getColumnIndex(DbConnection.BOOK_NO));
                        //Toast.makeText(getActivity(),"Search Successful",Toast.LENGTH_SHORT).show();
                        if(((meter_status != null) && (meter_status.equals("LOCK")) && (meter_status.equals("NO METER"))&& (meter_status.equals("POWER OFF"))&& (meter_status.equals("NO DISPLAY")) ) || (meter_reading != null)) {
                            Toast.makeText(getActivity(),"Meter reading is already done.",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent=new Intent(getActivity(), MyReadingByCGL.class);
                            intent.putExtra("r_type","Conn");
                            intent.putExtra("Conn",et_cgl.getText().toString());
                            intent.putExtra("book_no",book_no);
                            dismiss();
                            startActivity(intent);
                        }

                        // sqLiteDatabase.close();
                    }else{
                        Toast.makeText(getActivity(),"Connection code is not available !",Toast.LENGTH_SHORT).show();

                    }
                    sqLiteDatabase.close();
                    break;

                }



                    case R.id.b_read_by_cgi_cancel:
                dismiss();
                break;
        }
    }
}
