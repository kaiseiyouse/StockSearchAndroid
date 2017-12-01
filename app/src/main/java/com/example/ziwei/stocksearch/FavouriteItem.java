package com.example.ziwei.stocksearch;

import android.content.SharedPreferences;

import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ziwei on 11/22/2017.
 */

public class FavouriteItem {
    private String mSymbol;
    private String mPrice;
    private String mChange;
    private String mPercent;
    private String mChangeAndPercent;


    public FavouriteItem(String symbol, String price, String change, String percent) {
        mSymbol = symbol;
        mPrice = price;
        mChange = change;
        mPercent = percent;
        if(change != null) {
            mChangeAndPercent = change + "(" + percent + "%)";
        } else {
            mChangeAndPercent = null;
        }
    }

    public static FavouriteItem fromJSON(String response, String symbol) {
        String[] arr = response.split("@");
        try {
            JSONObject today = new JSONObject(arr[0]);
            JSONObject yesterday = new JSONObject(arr[1]);
            DecimalFormat df = new DecimalFormat("0.00");
            float price = Float.valueOf(today.getString("4. close"));
            float prev = Float.valueOf(yesterday.getString("4. close"));
            float change = price - prev;
            float percent = change / prev * 100;

            return new FavouriteItem(symbol, df.format(price), df.format(change), df.format(percent));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getChangeAndPercent() {
        return mChangeAndPercent;
    }

    public void setChangeAndPercent(String changeAndPercent) {
        mChangeAndPercent = changeAndPercent;
    }

    public String getChange() {
        return mChange;
    }

    public void setChange(String change) {
        mChange = change;
    }

    public String getPercent() {
        return mPercent;
    }

    public void setPercent(String percent) {
        mPercent = percent;
    }
}
