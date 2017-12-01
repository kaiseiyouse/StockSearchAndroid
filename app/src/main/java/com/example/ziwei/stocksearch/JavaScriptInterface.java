package com.example.ziwei.stocksearch;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by ziwei on 11/22/2017.
 */

public class JavaScriptInterface {

    private Context mContext;
    private String mSymbol;
    private String mIndicator;
    private String mExportUrl;

    public JavaScriptInterface(Context context, String symbol) {
        mContext = context;
        mSymbol = symbol;
    }

    public JavaScriptInterface(Context context, String symbol, String indicator) {
        mContext = context;
        mSymbol = symbol;
        mIndicator = indicator;

        //mProgressBar = rootView.findViewById(R.id.progressBar_indicator_webView);
        //mAlertText = rootView.findViewById(R.id.alert_text_indicator_webView);

    }

    @JavascriptInterface
    public String getSymbol() {
        return mSymbol;
    }

    @JavascriptInterface
    public String getIndicator() {
        return mIndicator;
    }

    @JavascriptInterface
    public String getExportUrl() {
        return mExportUrl;
    }

    @JavascriptInterface
    public void setExportUrl(String url) {
        mExportUrl = url;
    }

}
