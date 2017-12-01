package com.example.ziwei.stocksearch;

import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by ziwei on 11/22/2017.
 */

public class MyApp extends Application {

    private static Context sContext;
    private static HashSet<String> favSet;
    private static ArrayList<String> favList;
    private static final String FILENAME = "favourite_list_file.txt";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        try {
            favList = new ArrayList<>(10);
            favSet = new HashSet<>(10);
            File file = new File(sContext.getFilesDir(), FILENAME);
            if(!file.exists()) {
                file.createNewFile();
                return;
            }
            FileInputStream fis = sContext.openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                favList.add(line);
                favSet.add(line);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static ArrayList<String> getFavList() {
        return favList;
    }

    public static boolean isAdded(String symbol) {
        return favSet.contains(symbol);
    }

    public static void addToFavourite(String symbol) {
        favList.add(symbol);
        favSet.add(symbol);
        try {
            FileOutputStream fos = sContext.openFileOutput(FILENAME, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.append(symbol);
            bw.newLine();
            bw.close();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFromFavourite(String symbol) {
        favList.remove(symbol);
        favSet.remove(symbol);
        try {
            FileOutputStream fos = sContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            for(String s: favList) {
                bw.append(s);
                bw.newLine();
            }
            bw.close();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static int getDefaultOrder(String symbol) {
//
//    }

}
