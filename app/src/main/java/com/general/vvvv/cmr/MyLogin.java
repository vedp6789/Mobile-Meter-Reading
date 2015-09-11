package com.general.vvvv.cmr;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.general.SQLite.DbLogin;

/**
 * Created by comp on 17/09/2014.
 */
public class MyLogin extends ActionBarActivity {
TextView tv_user_id_login;
    EditText et_user_id_login;
    Button button_validate,button_exit;

    DbLogin dbLogin2;
    SQLiteDatabase db_read;
    static Integer user_id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        tv_user_id_login= (TextView) findViewById(R.id.textview_user_login);
        et_user_id_login= (EditText) findViewById(R.id.edittext_user_id);
        button_validate= (Button) findViewById(R.id.button_validate_login);
        button_exit= (Button) findViewById(R.id.button_exit);

        dbLogin2=new DbLogin(MyLogin.this,DbLogin.DB_NAME,null,DbLogin.DB_VERSION);
        db_read=dbLogin2.getReadableDatabase();
        Cursor cursor=dbLogin2.getUserId(db_read);
        if(cursor.getCount() > 0){
            Log.d("Login database size",String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            do{
                user_id=cursor.getInt(cursor.getColumnIndex(DbLogin.USER_ID));
            }while (cursor.moveToNext());
        }else{
            System.out.println("Login database is empty");
        }

        Log.d("Login database user id",user_id.toString());
        //db_read.close();
        //dbLogin2.close();
        /*b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_user_id.getText().length() > 0){

                }
                Toast.makeText(MyLogin.this,"hhgjkhk",Toast.LENGTH_SHORT).show();
            }
        });*/
        button_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_user_id_login.getText().length() != 0 || et_user_id_login.getText() != null){
                    Integer t=Integer.parseInt(et_user_id_login.getText().toString());
                    if( t.equals(user_id)){
                        Intent intent=new Intent(MyLogin.this,ElectricityDeptDaman.class);
                        ComponentName cn=intent.getComponent();
                        Intent intent1= IntentCompat.makeRestartActivityTask(cn);
                        startActivity(intent1);
                        //MyLogin.this.startActivity(intent);
                        //startActivity(intent);
                    }else{
                        Toast.makeText(MyLogin.this,"Wrong User Id !",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MyLogin.this,"Wrong User Id!",Toast.LENGTH_LONG).show();
                }
            }
        });
button_exit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();

    }
});
}

    @Override
    protected void onStop() {
        super.onStop();
        //db_read.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_read.close();
    }



}
