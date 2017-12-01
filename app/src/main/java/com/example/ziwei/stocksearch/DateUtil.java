package com.example.ziwei.stocksearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ziwei on 11/21/2017.
 */

public class DateUtil {
    private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

    public static String getYesterday(String today) {
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
            cal.setTime(sdf.parse(today));
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
