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
import com.general.vvvv.cmr.MyReading;
import com.general.vvvv.cmr.R;

/**
 * Created by vvvv on 22-08-2014.
 */

public class FDialogCustomerValidation extends DialogFragment implements View.OnClickListener {

  //  MyActivityHelper myActivityHelper;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class MyDbErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    }
    /*public interface MyActivityHelper{

        public Boolean helper();
    }*/
    SQLiteDatabase sqLiteDatabase;
    DbConnection dbConnection;
    EditText et_Connection;
    Button b_search,b_cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_TITLE,0);
        dbConnection=new DbConnection(getActivity(),DbConnection.DB_NAME,null,DbConnection.DB_VERSION,new MyDbErrorHandler());
        sqLiteDatabase=dbConnection.getReadableDatabase();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dialog_account_validation,container,false);
        et_Connection= (EditText) view.findViewById(R.id.etAccount_no);
        b_search= (Button) view.findViewById(R.id.b_search);
        b_cancel= (Button) view.findViewById(R.id.b_accValidateCancel);
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
    public void onClick(View view) {
     switch (view.getId()){
         case R.id.b_search:
             if(et_Connection.getText().length()==0){
                 Toast.makeText(getActivity(),"Insert Connection id",Toast.LENGTH_SHORT).show();
                 break;
             }else {
                 /*b_search.requestFocus();
                 Boolean aBoolean=dbConnection.bookCodeValidation(et_Connection.getText().toString());
                 if(aBoolean){
                     Intent intent=new Intent(getActivity(),MyReading.class);
                     intent.putExtra("Book_no",et_Book_no.getText().toString());
                     dismiss();
                     startActivity(intent);
                     break;
                 }else {
                     Toast.makeText(getActivity(),"Sorry!"+"\n"+"Book number is not identified.",Toast.LENGTH_SHORT).show();
                     break;
                 }*/
                 Cursor c=dbConnection.getMasterDetailForConnectionID(Integer.parseInt(et_Connection.getText().toString()),sqLiteDatabase);
                 if(c.getCount()>0){
                     //Toast.makeText(getActivity(),"Search successful.",Toast.LENGTH_SHORT).show();

                     c.moveToFirst();
                     Bundle bundle=new Bundle();

                     bundle.putInt("C_code",Integer.parseInt(c.getString(c.getColumnIndex(DbConnection.SC_NO))));
                     bundle.putString("Meter_no",c.getString(c.getColumnIndex(DbConnection.METER_NO)));
                     bundle.putString("Book_no",c.getString(c.getColumnIndex(DbConnection.BOOK_NO)));
                     bundle.putInt("Route_no",Integer.parseInt(c.getString(c.getColumnIndex(DbConnection.ROUTE_NO))));
                     bundle.putString("Cgl_no",c.getString(c.getColumnIndex(DbConnection.CGL_NO)));
                     bundle.putString("C_name",c.getString(c.getColumnIndex(DbConnection.NAME_CONSUMER)));
                    // bundle.putString("Address",c.getString(c.getColumnIndex(DbConnection.ADDRESS)));
                    // bundle.putString("Mobile_no",c.getString(c.getColumnIndex(DbConnection.CONTACT_NO)));
                     //bundle.putString("Feeder_name",c.getString(c.getColumnIndex(DbConnection.FEEDERNAME)));


                     Intent intent=new Intent(getActivity(),MyReading.class);
                     intent.putExtras(bundle);
                     dismiss();
                     startActivity(intent);
                     sqLiteDatabase.close();
                     //MyActivity activity=new MyActivity();
                     //activity.cosumerSearchValidate();


                 }else{
                     Toast.makeText(getActivity(), "Consumer code either not valid or not get downloaded.", Toast.LENGTH_SHORT).show();
                     et_Connection.setText("");
                     //sqLiteDatabase.close();
                     break;
                 }
                 /*ConnectToAuthenticateAcc connectToAuthenticateAcc = new ConnectToAuthenticateAcc();
                 connectToAuthenticateAcc.execute(etAccount_no.getText().toString());*/

             }

         case R.id.b_accValidateCancel:
             dismiss();
             break;
     }
    }

}
