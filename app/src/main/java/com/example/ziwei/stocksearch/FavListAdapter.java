package com.example.ziwei.stocksearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ziwei on 11/22/2017.
 */

public class FavListAdapter extends ArrayAdapter<FavouriteItem> {

    private ArrayList<FavouriteItem> mItemList;

    public FavListAdapter(Context ctx, int resId, ArrayList<FavouriteItem> items) {
        super(ctx, resId, items);
        mItemList = items;
    }

    public ArrayList<FavouriteItem> getItemList() {
        return mItemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FavouriteItem row = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fav_listitem, parent, false);
        }
        TextView symbol = convertView.findViewById(R.id.text_symbol);
        TextView price = convertView.findViewById(R.id.text_price);
        TextView changeAndPercent = convertView.findViewById(R.id.text_change_percent);

        symbol.setText(row.getSymbol());
        price.setText(row.getPrice());
        changeAndPercent.setText(row.getChangeAndPercent());
        return convertView;
    }


}
