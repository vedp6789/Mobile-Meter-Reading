package com.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.general.vvvv.cmr.R;

/**
 * Created by comp on 04/10/2014.
 */
public class FDialogView extends DialogFragment implements View.OnClickListener {
    EditText et_book;
    Button b_p_from,b_p_to,b_view_rec,b_can;
    TextView tv_p_frm,tv_p_to;
    static final int DATE_PICKER_ID = 1111;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dialog_rec_viewer,container,false);
        et_book= (EditText) view.findViewById(R.id.et_view_by_book);
        b_p_from= (Button) view.findViewById(R.id.b_p_from);
        b_p_to= (Button) view.findViewById(R.id.b_p_to);
        b_view_rec= (Button) view.findViewById(R.id.b_view_record);
        b_can= (Button) view.findViewById(R.id.b_v_can);
        tv_p_frm= (TextView) view.findViewById(R.id.tv_c_from);
        tv_p_to= (TextView) view.findViewById(R.id.tv_c_to);
        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.msg));
        dialog.setCanceledOnTouchOutside(false);
        return view;

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_p_from:

                break;
            case R.id.b_p_to:
                break;
            case R.id.b_view_record:
                break;
            case R.id.b_v_can:
                break;

        }
    }
}
