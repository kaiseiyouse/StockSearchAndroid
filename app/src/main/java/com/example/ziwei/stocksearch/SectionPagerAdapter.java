package com.example.ziwei.stocksearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter {

    private DetailTabFragment mTab1;
    private HistoricalTabFragment mTab2;
    private NewsTabFragment mTab3;

    public static String sSymbol;

    public SectionPagerAdapter(FragmentManager fm, String symbol) {
        super(fm);
        sSymbol = symbol;
        mTab1 = new DetailTabFragment();
        mTab2 = new HistoricalTabFragment();
        mTab3 = new NewsTabFragment();

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mTab1;
            case 1:
                return mTab2;
            case 2:
                return mTab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}
