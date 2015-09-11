package com.general.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.general.vvvv.cmr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 23-01-2015.
 */
public class HtcExpandableAdapter implements ExpandableListAdapter {
    Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<PojoDownload>> _listDataChild;

    public HtcExpandableAdapter(Context context, List<String> listDataHeader,
                                HashMap<String, ArrayList<PojoDownload>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_group_view, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tv_expandable_group);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = null;


        if (groupPosition == 0) {
            String area_code = "";
            Integer no_of_c = 0;
            String date = "";
            PojoDownload pojoDownload = (PojoDownload) getChild(groupPosition, childPosition);
            area_code = pojoDownload.getArea_code();
            Log.d("downloaded area:", area_code);
            no_of_c = pojoDownload.getConumers();
            Log.d("download no of consumers:", String.valueOf(no_of_c));
            date = pojoDownload.getDate();
            Log.d("download date:", date);


            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_view_ltp_download, null);


                TextView tv_area = (TextView) convertView
                        .findViewById(R.id.tv_area_code1);
                TextView tv_con = (TextView) convertView.findViewById(R.id.tv_child_consumers);
                TextView tv_dat = (TextView) convertView.findViewById(R.id.tv_child_date);

                tv_area.setText(area_code);
                tv_con.setText(no_of_c.toString());
                tv_dat.setText(date);
            }
        } else {
            if (groupPosition == 1) {
                String area_code = "";
                Integer no_of_c = 0;
                Integer no_of_nc = 0;
                Integer no_of_mm_lock_st = 0;   // mm stands for Main meter
                Integer no_of_mm_Power_Off = 0;
                Integer no_of_mm_NO_Meter = 0;
                Integer no_of_mm_NO_Display = 0;

                Integer no_of_cm_lock_st = 0;   // cm stands for Check meter
                Integer no_of_cm_Power_Off = 0;
                Integer no_of_cm_NO_Meter = 0;
                Integer no_of_cm_NO_Display = 0;


                //String date=null;
                PojoDownload pojoDownload = (PojoDownload) getChild(groupPosition, childPosition);
                area_code = pojoDownload.getArea_code();
                Log.d("reading area_code:", area_code);
                no_of_c = pojoDownload.getConumers();
                Log.d("no of consumers reading success:", String.valueOf(no_of_c));
                no_of_nc = pojoDownload.getNo_of_met_read_nc();
                no_of_mm_lock_st = pojoDownload.getNo_of_Main_Lock_status();
                no_of_mm_NO_Meter = pojoDownload.getNo_of_Main_NO_Meter();
                no_of_mm_Power_Off = pojoDownload.getNo_of_Main_Power_Off();
                no_of_mm_NO_Display = pojoDownload.getNo_of_Main_NO_DISPLAY();
                no_of_cm_lock_st = pojoDownload.getNo_of_Check_Lock_status();
                no_of_cm_NO_Meter = pojoDownload.getNo_of_Check_NO_Meter();
                no_of_cm_Power_Off = pojoDownload.getNo_of_Check_Power_Off();
                no_of_cm_NO_Display = pojoDownload.getNo_of_Check_NO_DISPLAY();

                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.child_view_ltp_reading, null);


                    TextView tv_area2 = (TextView) convertView.findViewById(R.id.tv_area_code2);
                    TextView tv_con2 = (TextView) convertView.findViewById(R.id.tv_child_success_reading);
                    TextView tv_reading_to_be_done = (TextView) convertView.findViewById(R.id.tv_child_reading_remaining);
                    TextView tv_Lock_status = (TextView) convertView.findViewById(R.id.tv_no_of_LOCK);
                    TextView tv_NO_Meter = (TextView) convertView.findViewById(R.id.tv_no_of_NO_METER);
                    TextView tv_Power_Off = (TextView) convertView.findViewById(R.id.tv_no_of_POWER_OFF);
                    TextView tv_NO_Display = (TextView) convertView.findViewById(R.id.tv_no_of_NO_DISPLAY);

                    tv_area2.setText(area_code);
                    tv_con2.setText(no_of_c.toString());
                    tv_reading_to_be_done.setText(no_of_nc.toString());
                    tv_Lock_status.setText(no_of_mm_lock_st.toString() + "/" + no_of_cm_lock_st.toString());
                    tv_NO_Meter.setText(no_of_mm_NO_Meter.toString() + "/" + no_of_cm_NO_Meter.toString());
                    tv_Power_Off.setText(no_of_mm_Power_Off.toString() + "/" + no_of_cm_Power_Off.toString());
                    tv_NO_Display.setText(no_of_mm_NO_Display.toString() + "/" + no_of_cm_NO_Display.toString());
                }
                //tv_dat.setText(date);
            } else {
                if (groupPosition == 2) {
                    String area_code = "";
                    Integer htc_no = 0;


                    PojoDownload pojoDownload = (PojoDownload) getChild(groupPosition, childPosition);
                    area_code = pojoDownload.getArea_code();
                    htc_no = pojoDownload.getHtc_no();


                    if (convertView == null) {
                        LayoutInflater infalInflater = (LayoutInflater) this._context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = infalInflater.inflate(R.layout.child_view_ltp_unsuccess_reading, null);


                        TextView tv_area3 = (TextView) convertView.findViewById(R.id.tv_area_code3);
                        TextView tv_htc = (TextView) convertView.findViewById(R.id.tv_ltp_no);


                        tv_area3.setText(area_code);
                        tv_htc.setText(htc_no.toString());

                        //tv_Lock_status.setText(no_of_lock_st.toString());
                    }

                } else {
                    if (groupPosition == 3) {
                        String area_code = "";
                        Integer htc_no = 0;


                        PojoDownload pojoDownload = (PojoDownload) getChild(groupPosition, childPosition);
                        area_code = pojoDownload.getArea_code();
                        htc_no = pojoDownload.getHtc_no();


                        if (convertView == null) {
                            LayoutInflater infalInflater = (LayoutInflater) this._context
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            convertView = infalInflater.inflate(R.layout.child_view_ltp_unsuccess_reading, null);


                            TextView tv_area3 = (TextView) convertView.findViewById(R.id.tv_area_code3);
                            TextView tv_htc = (TextView) convertView.findViewById(R.id.tv_ltp_no);


                            tv_area3.setText(area_code);
                            tv_htc.setText(htc_no.toString());

                            //tv_Lock_status.setText(no_of_lock_st.toString());
                        }

                    }
                }
            }


        }


        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }
}
