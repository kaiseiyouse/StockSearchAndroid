package com.example.ziwei.stocksearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ziwei on 11/20/2017.
 */

public class DetailTabFragment extends Fragment {

    private static DetailTabFragment mInstance;
    private String mSymbol;
    private View mRootView;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private ProgressBar mProgressBar2; //indicator chart
    private TextView mAlertText;
    private TextView mAlertText2; //indicator chart
    private ImageButton mFacebook;
    private ImageButton mStar;
    private WebView mWebView;
    private Spinner mSpinner;
    private String mIndicator;
    private TextView mIndicatorChange;
    private DetailTableListAdapter mDetailTableListAdapter;
    private ShareDialog mShareDialog;
    private CallbackManager mCallbackManager;
    private JavaScriptInterface mJavaScriptInterface;

    private int mNumberOfPics = 0;
    private boolean mLoadingFinished = true;
    private boolean mRedirect = false;

    private final String URL = "http://stock-183802.appspot.com/api/";
    private final String EXPORT_URL = "https://export.highcharts.com/";
    private final String INDICATOR_URL = "file:///android_asset/html/indicator.html";
    private final String[] indicators = {"Price", "SMA", "EMA", "STOCH", "RSI", "ADX", "CCI", "BBANDS", "MACD"};

    public DetailTabFragment() {
    }

    public static DetailTabFragment newInstance() {
        if(mInstance == null) {
            mInstance = new DetailTabFragment();
        }
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView == null) {
            mSymbol = SectionPagerAdapter.sSymbol;
            String url = URL + "daily?symbol=" + mSymbol;
            mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
            mListView = mRootView.findViewById(R.id.detail_table);
            mProgressBar = mRootView.findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
            mAlertText = mRootView.findViewById(R.id.alert_text);
            mAlertText.setVisibility(View.GONE);
            mStar = mRootView.findViewById(R.id.btn_star);
            if(MyApp.isAdded(mSymbol)) {
                mStar.setBackgroundResource(R.drawable.filled);
            } else {
                mStar.setBackgroundResource(R.drawable.empty);
            }
            mStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyApp.isAdded(mSymbol)) {
                        mStar.setBackgroundResource(R.drawable.empty);
                        MyApp.removeFromFavourite(mSymbol);
                    } else {
                        mStar.setBackgroundResource(R.drawable.filled);
                        MyApp.addToFavourite(mSymbol);
                    }
                }
            });
            mFacebook = mRootView.findViewById(R.id.btn_facebook);
            mFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    mCallbackManager = CallbackManager.Factory.create();
                    mShareDialog = new ShareDialog(getActivity());
//                    mShareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
//                        @Override
//                        public void onSuccess(Sharer.Result result) {
//                            Toast.makeText(getContext(), "You successfully posted", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCancel() {
//                            Toast.makeText(getContext(), "You canceled the post", Toast.LENGTH_SHORT).show();
//                            Log.d("post on cancel: ", "onCancel: ");
//                        }
//
//                        @Override
//                        public void onError(FacebookException error) {
//                            Toast.makeText(getContext(), "Failed to post", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    if(ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(EXPORT_URL + mJavaScriptInterface.getExportUrl()))
                                .build();
                        mShareDialog.show(linkContent, ShareDialog.Mode.FEED);
                    }
                }
            });
            mFacebook.setClickable(false);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            ArrayList<DetailRow> items = DetailRow.fromJSON(response);
                            boolean increase = DetailRow.isIncrease();
                            if(getContext() == null) return;
                                mDetailTableListAdapter = new DetailTableListAdapter(getContext(), android.R.layout.simple_list_item_1, items, increase);
                                mListView.setAdapter(mDetailTableListAdapter);
                                mProgressBar.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d("details", "onErrorResponse: " + error);
                            mProgressBar.setVisibility(View.GONE);
                            mAlertText.setVisibility(View.VISIBLE);
                        }
                    });
            MySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);

            mProgressBar2 = mRootView.findViewById(R.id.progressBar_indicator_webView);
            mAlertText2 = mRootView.findViewById(R.id.alert_text_indicator_webView);



            mIndicator = "Price";
            mWebView = mRootView.findViewById(R.id.webView_indicators);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setDomStorageEnabled(true);
            mJavaScriptInterface = new JavaScriptInterface(getContext(), mSymbol, "Price");
            mWebView.addJavascriptInterface(mJavaScriptInterface, "Android");
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(!mLoadingFinished) {
                        mRedirect = true;
                        mProgressBar2.setVisibility(View.VISIBLE);
                    }
                    mLoadingFinished = false;
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    mLoadingFinished = false;
                    mProgressBar2.setVisibility(View.VISIBLE);
                    mAlertText2.setVisibility(View.GONE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(!mRedirect) {
                        mLoadingFinished = true;
                    }
                    if(mLoadingFinished && !mRedirect) {
//                        mProgressBar2.setVisibility(View.GONE);
                    } else mRedirect = false;
                    Log.d("finished", "onPageFinished: ");
                    //mProgressBar2.setVisibility(View.GONE);
                }


                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    mAlertText2.setVisibility(View.VISIBLE);
                }


            });

            mWebView.setPictureListener(new WebView.PictureListener() {
                @Override
                public void onNewPicture(WebView webView, Picture picture) {
                    Log.d("picture changed", "onNewPicture: ");
                    mNumberOfPics++;
                    if(mNumberOfPics == 5) {
                        mProgressBar2.setVisibility(View.GONE);
                        mFacebook.setClickable(true);
                        mNumberOfPics = 0;
                    }
                }
            });

            mWebView.loadUrl(INDICATOR_URL);
            mSpinner = mRootView.findViewById(R.id.spinner_indicator);
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selected = mSpinner.getSelectedItem().toString();
                    if(selected.equals(mIndicator)) {
                        mIndicatorChange.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    } else {
                        mIndicatorChange.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mIndicatorChange = mRootView.findViewById(R.id.btn_indicator_change);
            mIndicatorChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selected = mSpinner.getSelectedItem().toString();
                    if(selected.equals(mIndicator)) {
                        return;
                    }
                    mJavaScriptInterface = new JavaScriptInterface(getContext(), mSymbol, selected);
                    mWebView.addJavascriptInterface(mJavaScriptInterface, "Android");
                    mWebView.loadUrl(INDICATOR_URL);
                    mIndicator = selected;
                    mIndicatorChange.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            });

//            Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            mWebView.draw(canvas);
//            mIndicatorChartBitmap = bitmap;
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if(parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }


}
