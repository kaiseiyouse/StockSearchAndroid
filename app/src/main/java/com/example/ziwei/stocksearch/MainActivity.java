package com.example.ziwei.stocksearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private String mSymbol;
    private AutoCompleteTextView mAutoTextView;
    private TextView mGetQuote;
    private TextView mClear;
    private Toast mToast;
    private ProgressBar mProgressBar;
    private ListView mFavlistView;
    private FavListAdapter mFavListAdapter;
    private ImageButton mRefresh;
    private Spinner mSortBy;
    private Spinner mOrder;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private Switch mAutoRefreshSwitch;

    private final String URL = "http://stock-183802.appspot.com/api/";
    private final long AUTO_REFRESH_PERIOD = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progressBar);
        mAutoTextView = (AutoCompleteTextView) findViewById(R.id.auto_textView);
        final AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, null);
        mAutoTextView.setThreshold(1);
        mAutoTextView.setAdapter(autoCompleteAdapter);
        mAutoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString().trim();
                if(str.equals("") || str.contains("-")) return;
                mSymbol = str.toUpperCase();
                String url = URL + "autocomplete?input=" + str;
                StringRequest stringRequest = new StringRequest
                        (Request.Method.GET, url, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
//                                Log.d("autocomplete", "onResponse: "+response);
                                try {
                                    JSONArray array = new JSONArray(response);
                                    ArrayList<String> items = new ArrayList<>();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject row = array.getJSONObject(i);
                                        String symbol = row.getString("Symbol");
                                        String name = row.getString("Name");
                                        String exchange = row.getString("Exchange");
                                        items.add(symbol+"-"+name+"("+exchange+")");
                                    }
                                    autoCompleteAdapter.setItems(items);
//                                    Log.d("auto", items.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("autocomplete", "onErrorResponse: "+error);
                            }
                        });

                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAutoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String symbol = autoCompleteAdapter.getItem(i);
                symbol = symbol.split("-")[0];
                Log.d("onclick", symbol);
                mAutoTextView.setText(symbol);
                mSymbol = symbol;
            }
        });

        mFavlistView = findViewById(R.id.fav_listView);

        mGetQuote = (TextView) findViewById(R.id.getQuote);
        mGetQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSymbol == null || mSymbol.trim().equals("")) {
                    mToast = Toast.makeText(MainActivity.this, "Please enter a stock name or symbol", Toast.LENGTH_SHORT);
                    mToast.show();
                    return;
                }
                submit(mSymbol);
            }
        });

        mClear = (TextView) findViewById(R.id.clear);
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAutoTextView.setText("");
                mSymbol = null;
                if(mToast != null) {
                    mToast.cancel();
                }
            }
        });

        loadFavList();
        refresh();

        mRefresh = findViewById(R.id.btn_refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        mFavlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FavouriteItem item = (FavouriteItem) adapterView.getItemAtPosition(i);
//                Log.d("favlist item clicked", "onItemClick: "+item.getSymbol());
                submit(item.getSymbol());
            }
        });

        mFavlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                FavouriteItem item = (FavouriteItem) adapterView.getItemAtPosition(i);
                showMenu(view, item.getSymbol(), i);
                return true;
            }
        });

        mSortBy = findViewById(R.id.spinner_sort_by);
        mOrder = findViewById(R.id.spinner_order);

        mSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sortMethod = adapterView.getItemAtPosition(i).toString();
                String sortOrder = mOrder.getSelectedItem().toString();
                sortFavList(sortMethod, sortOrder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sortMethod = mSortBy.getSelectedItem().toString();
                String sortOrder = adapterView.getItemAtPosition(i).toString();
                sortFavList(sortMethod, sortOrder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAutoRefreshSwitch = findViewById(R.id.switch_auto_refresh);
        mAutoRefreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    mTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                }
                            });
                        }
                    };
                    mTimer = new Timer();
                    mTimer.schedule(mTimerTask, 2000, AUTO_REFRESH_PERIOD);
                } else {
                    mTimerTask.cancel();
                    mTimerTask = null;
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        });

    }

    private void showMenu (View view, final String symbol, final int position) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
        {
            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.menu_item_favlist_no:
                        Log.d ("contextMenu", "settings");
                        break;
                    case R.id.menu_item_favlist_yes:
                        Log.d ("contextMenu", "about");
                        MyApp.removeFromFavourite(symbol);
                        mFavListAdapter.getItemList().remove(position);
                        mFavListAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
        menu.inflate (R.menu.menu_favlist_item);
        menu.show();
    }

    private void submit(String symbol) {
        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        i.putExtra("symbol", symbol);
        startActivity(i);
    }

    private void refresh() {
        Log.d("refresh: ", "refreshing...");
        ArrayList<FavouriteItem> symbols = mFavListAdapter.getItemList();

        for(int i=0; i < symbols.size(); i++) {
            final int index = i;
            final String symbol = symbols.get(index).getSymbol();
            String url = URL + "refresh?symbol=" + symbol;
            mProgressBar.setVisibility(View.VISIBLE);

            StringRequest stringRequest = new StringRequest
                    (Request.Method.GET, url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            FavouriteItem item = FavouriteItem.fromJSON(response, symbol);
                            if(item == null) return;
//                            Log.d("fav", "onResponse: "+item.getSymbol());
//                            Log.d("fav", "onResponse: "+item.getPrice());
//                            Log.d("fav", "onResponse: "+item.getChangeAndPercent());


                            mFavListAdapter.getItemList().set(index, item);
                            int visiblePosition = mFavlistView.getFirstVisiblePosition();
                            View view = mFavlistView.getChildAt(index - visiblePosition);
                            mFavListAdapter.getView(index, view, mFavlistView);

                            mProgressBar.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d("details", "onErrorResponse: " + error);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void loadFavList() {
        ArrayList<FavouriteItem> items = new ArrayList<>(10);
        ArrayList<String> symbols = MyApp.getFavList();
        for(String s:symbols) {
            items.add(new FavouriteItem(s, null, null, null));
        }

        mFavListAdapter = new FavListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        mFavlistView.setAdapter(mFavListAdapter);
    }

    private void sortFavList(final String sortMethod, final String sortOrder) {
        ArrayList<FavouriteItem> list = mFavListAdapter.getItemList();
        Comparator<FavouriteItem> comparator = new Comparator<FavouriteItem>() {
            @Override
            public int compare(FavouriteItem item1, FavouriteItem item2) {
                boolean asc = (sortOrder.equals("Ascending"));
                switch (sortMethod) {
                    case "Default":
                        return asc? MyApp.getFavList().indexOf(item1.getSymbol()) - MyApp.getFavList().indexOf(item2.getSymbol())
                                : MyApp.getFavList().indexOf(item2.getSymbol()) - MyApp.getFavList().indexOf(item1.getSymbol());

                    case "Symbol":
                        return asc? item1.getSymbol().compareTo(item2.getSymbol())
                                : item2.getSymbol().compareTo(item1.getSymbol());

                    case "Price":
                        return asc? Float.valueOf(item1.getPrice()).compareTo(Float.valueOf(item2.getPrice()))
                                : Float.valueOf(item2.getPrice()).compareTo(Float.valueOf(item1.getPrice()));

                    case "Change":
                        return asc? Float.valueOf(item1.getChange()).compareTo(Float.valueOf(item2.getChange()))
                                : Float.valueOf(item2.getChange()).compareTo(Float.valueOf(item1.getChange()));

                    case "Change Percent":
                        return asc? Float.valueOf(item1.getPercent()).compareTo(Float.valueOf(item2.getPercent()))
                                : Float.valueOf(item2.getPercent()).compareTo(Float.valueOf(item1.getPercent()));

                    default:
                        break;
                }
                return 0;
            }
        };

        Collections.sort(list, comparator);
        mFavListAdapter.notifyDataSetChanged();
    }
}
