package com.example.ziwei.stocksearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ziwei on 11/21/2017.
 */

public class News {
    private String mTitle;
    private String mAuthor;
    private String mDate;
    private String mLink;

    public News(String title, String author, String date, String link) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mLink = link;
    }

    public static ArrayList<News> fromJSON(JSONObject obj) {
        try {
            ArrayList<News> news = new ArrayList<>();
            JSONArray items = obj.getJSONObject("rss").getJSONArray("channel").getJSONObject(0).getJSONArray("item");
            for (int i = 0; i < items.length(); i++) {
                JSONObject each = items.getJSONObject(i);
                String title = each.getJSONArray("title").getString(0);
                String link = each.getJSONArray("link").getString(0);
                String date = each.getJSONArray("pubDate").getString(0);
                date = "Date: " + date.substring(0, date.length()-5) + "PDT";
                String author = each.getJSONArray("sa:author_name").getString(0);

                News n = new News(title, author, date, link);
                news.add(n);
            }

            return news;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}
