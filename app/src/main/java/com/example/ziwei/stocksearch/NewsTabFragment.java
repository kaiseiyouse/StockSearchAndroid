package com.example.ziwei.stocksearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziwei on 11/20/2017.
 */

public class NewsTabFragment extends Fragment {

    private static NewsTabFragment mInstance;
    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private View mRootView;
    private ProgressBar mProgressBar;
    private TextView mAlertText;

    private static final String URL = "http://stock-183802.appspot.com/api/news?symbol=";

    public NewsTabFragment() {}

    public static NewsTabFragment newInstance() {
        if(mInstance == null) {
            mInstance = new NewsTabFragment();
        }
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_news, container, false);
            mRecyclerView = mRootView.findViewById(R.id.news_recycler_view);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(llm);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(getContext(), llm.getOrientation());
            mRecyclerView.addItemDecoration(mDividerItemDecoration);
            mProgressBar = mRootView.findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
            mAlertText = mRootView.findViewById(R.id.alert_text);
            mAlertText.setVisibility(View.GONE);

            String symbol = SectionPagerAdapter.sSymbol;
            String url = URL + symbol;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            ArrayList<News> news = News.fromJSON(response);
                            mAdapter = new NewsAdapter(news);
                            mRecyclerView.setAdapter(mAdapter);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("news", "onErrorResponse: " + error);
                            mProgressBar.setVisibility(View.GONE);
                            mAlertText.setVisibility(View.VISIBLE);
                        }
                    });
            MySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);

        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if(parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    private class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mDateTextView;

        private News mNews;

        public NewsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.news_listitem, parent, false));
            mTitleTextView = itemView.findViewById(R.id.news_title);
            mAuthorTextView = itemView.findViewById(R.id.news_author);
            mDateTextView = itemView.findViewById(R.id.news_date);

            mTitleTextView.setOnClickListener(this);
            mAuthorTextView.setOnClickListener(this);
            mDateTextView.setOnClickListener(this);
        }

        public void bind(News news) {
            mNews = news;
            mTitleTextView.setText(news.getTitle());
            mAuthorTextView.setText(news.getAuthor());
            mDateTextView.setText(news.getDate());
        }


        @Override
        public void onClick(View view) {
            Log.d("link", "onClick: "+mNews.getLink());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNews.getLink()));
            startActivity(browserIntent);
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<News> mNewsList;

        public NewsAdapter(List<News> news) {
            mNewsList = news;
        }

        @Override
        public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new NewsHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(NewsHolder holder, int position) {
            News news = mNewsList.get(position);
            holder.bind(news);
        }

        @Override
        public int getItemCount() {
            if(mNewsList == null) return 0;
            return mNewsList.size();
        }
    }
}
