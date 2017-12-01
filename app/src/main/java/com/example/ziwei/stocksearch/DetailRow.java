package com.example.ziwei.stocksearch;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ziwei on 11/21/2017.
 */

public class DetailRow {
    public String th;
    public String td;
    private static boolean mIncrease=false;

    public DetailRow(String th, String td) {
        this.th = th;
        this.td = td;
    }

    public static boolean isIncrease() {
        return mIncrease;
    }

    public static ArrayList<DetailRow> fromJSON(JSONObject obj) {
        ArrayList<DetailRow> rows = new ArrayList<>();
        try {
            String last_refresh = obj.getJSONObject("Meta Data").getString("3. Last Refreshed").substring(0, 10);
            String timeStamp = last_refresh + " EDT";
            JSONObject daily = obj.getJSONObject("Time Series (Daily)");
            while (!daily.has(last_refresh)) {
                last_refresh = DateUtil.getYesterday(last_refresh);
            }
            String today = last_refresh;
            last_refresh = DateUtil.getYesterday(last_refresh);
            while (!daily.has(last_refresh)) {
                last_refresh = DateUtil.getYesterday(last_refresh);
            }
            String yesterday = last_refresh;
            JSONObject todayObj = daily.getJSONObject(today);
            JSONObject yesterdayObj = daily.getJSONObject(yesterday);
            String symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
            float close = Float.valueOf(todayObj.getString("4. close"));
            float prev = Float.valueOf(yesterdayObj.getString("4. close"));
            float change = close - prev;
            mIncrease = (change >= 0) ;
            float percent = change / prev;
            String open = todayObj.getString("1. open");
            String volume = todayObj.getString("5. volume");
            String low = todayObj.getString("3. low");
            String high = todayObj.getString("2. high");

            DecimalFormat df = new DecimalFormat("0.00");
            rows.add(new DetailRow("Stock Symbol", symbol));
            rows.add(new DetailRow("Last Price", df.format(prev)));
            rows.add(new DetailRow("Change", df.format(change) + "(" + df.format(percent*100) + "%)"));
            rows.add(new DetailRow("Timestamp", timeStamp));
            rows.add(new DetailRow("Open", df.format(Float.valueOf(open))));
            rows.add(new DetailRow("Close", df.format(close)));
            rows.add(new DetailRow("Day's Range", df.format(Float.valueOf(low)) +" - "+ df.format(Float.valueOf(high))));
            rows.add(new DetailRow("Volume", volume));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
