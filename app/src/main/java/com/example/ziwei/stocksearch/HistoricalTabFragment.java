package com.example.ziwei.stocksearch;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by ziwei on 11/20/2017.
 */

public class HistoricalTabFragment extends Fragment {

    private static HistoricalTabFragment mInstance;
    private String mSymbol;
    private View mRootView;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mAlertText;
    private int mNumOfPics = 0;

    public HistoricalTabFragment() {}

    public static HistoricalTabFragment newInstance() {
        if(mInstance == null) {
            mInstance = new HistoricalTabFragment();
        }
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_historical, container, false);
            mProgressBar = mRootView.findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
            mAlertText = mRootView.findViewById(R.id.alert_text);
            mAlertText.setVisibility(View.GONE);
            mSymbol = SectionPagerAdapter.sSymbol;
            mWebView = mRootView.findViewById(R.id.history_chart);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mAlertText.setVisibility(View.GONE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
//                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    mAlertText.setVisibility(View.VISIBLE);
                }
            });

            mWebView.addJavascriptInterface(new JavaScriptInterface(getContext(), mSymbol), "Android");
            mWebView.setPictureListener(new WebView.PictureListener() {
                @Override
                public void onNewPicture(WebView webView, Picture picture) {
                    Log.d("history", "onNewPicture: "+"history");
                    mNumOfPics++;
                    if(mNumOfPics == 5) {
                        mNumOfPics = 0;
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
            mWebView.loadUrl("file:///android_asset/html/webview.html");
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if(parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
}
