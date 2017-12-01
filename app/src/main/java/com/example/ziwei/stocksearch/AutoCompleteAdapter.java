package com.example.ziwei.stocksearch;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by ziwei on 11/20/2017.
 */

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private ArrayList<String> mItems;
    private final String URL = "http://stock-183802.appspot.com/api/autocomplete?input=";
    private final String TAG = "autocomplete";

    public AutoCompleteAdapter(Context context, ArrayList<String> items) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        mItems = items;
        setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    }

    public void setItems(ArrayList<String> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }


//    @NonNull
//    @Override
//    public Filter getFilter() {
//        return super.getFilter();
//    }

}
