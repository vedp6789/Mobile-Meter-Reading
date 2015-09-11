package com.fragments;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
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
import com.general.vvvv.cmr.MyReadingByBookNo;
import com.general.vvvv.cmr.R;

/**
 * Created by comp on 29/09/2014.
 */
public class FDialogReadingBookValidation extends DialogFragment implements View.OnClickListener{
    SQLiteDatabase sqLiteDatabase;
    DbConnection dbConnection;
    EditText et_Book;
    Button b_search,b_cancel;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dialog_reading_book_validation,container,false);
        et_Book= (EditText) view.findViewById(R.id.et_read_d_b_no);
        b_search= (Button) view.findViewById(R.id.b_search_book);
        b_cancel= (Button) view.findViewById(R.id.b_read_by_b_cancel);
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
            case R.id.b_search_book:
                if(et_Book.getText().length()==0){
                    Toast.makeText(getActivity(), "Insert Book No.", Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    sqLiteDatabase=dbConnection.getReadableDatabase();
                    boolean b=dbConnection.bookCodeValidation(et_Book.getText().toString(),sqLiteDatabase);
                    if(b){

                        //Toast.makeText(getActivity(),"Search Successful",Toast.LENGTH_SHORT).show();
                        dismiss();
                        Intent intent=new Intent(getActivity(), MyReadingByBookNo.class);
                        intent.putExtra("book_no",et_Book.getText().toString());
                        //dismiss();
                        startActivity(intent);

                    }else{
                        Toast.makeText(getActivity(),"Book No. is not available !",Toast.LENGTH_SHORT).show();

                    }
                sqLiteDatabase.close();
                    break;
                }

            case R.id.b_read_by_b_cancel:
                dismiss();
                break;
        }
    }


}
