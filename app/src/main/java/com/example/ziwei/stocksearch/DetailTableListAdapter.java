package com.example.ziwei.stocksearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ziwei on 11/21/2017.
 */

public class DetailTableListAdapter extends ArrayAdapter<DetailRow> {

    private ArrayList<DetailRow> mItems;
    private boolean mIncrease;

    public DetailTableListAdapter(Context ctx, int resId, ArrayList<DetailRow> items, boolean increase) {
        super(ctx, resId, items);
        mItems = items;
        mIncrease = increase;
    }

    public ArrayList<DetailRow> getListItems() {
        return mItems;
    }

    public void setListItems(ArrayList<DetailRow> items) {
        mItems = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DetailRow row = getItem(position);
        if(position == 2) {
//            Log.d("dfdf", "position: " + position);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_table_list_item_arrow_row, parent, false);
            ImageView imageView = convertView.findViewById(R.id.image_view_arrow);
            if(mIncrease) {
                imageView.setImageResource(R.drawable.up);
            } else {
                imageView.setImageResource(R.drawable.down);
            }
        } else convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_table_listitem, parent, false);

        TextView th = convertView.findViewById(R.id.table_head);
        TextView td = convertView.findViewById(R.id.table_data);

        th.setText(row.th);
        td.setText(row.td);
        return convertView;
    }

    @Override
    public int getCount() {
        if(mItems == null) return 0;
        return mItems.size();
    }
}
