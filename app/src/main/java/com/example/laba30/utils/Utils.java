package com.example.laba30.utils;

import static com.example.laba30.data.Constants.APP_RESULTS_FILE_DIRECTORY;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laba30.data.Date;
import com.example.laba30.data.PersonalDataFormat;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class Utils {
    public static void moveToActivity(AppCompatActivity currActivity, Intent intent) {
        currActivity.startActivity(intent);
        currActivity.finish();
    }

    public static Date getDateThroughOffset(int pos) {
        Calendar mCalendar = Calendar.getInstance();
        if (pos != 0) {
            mCalendar.add(Calendar.DATE, pos);
        }
        return new Date(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH)+1, mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public static void setMinDate(DatePicker datePicker, int minPos) {
        Calendar minCalendar = Calendar.getInstance();
        Date minDate = getDateThroughOffset(minPos);
        minCalendar.set(minDate.year, minDate.month-1, minDate.day);
        datePicker.setMinDate(minCalendar.getTimeInMillis());
    }

    public static void setMaxDate(DatePicker datePicker, int maxPos) {
        Calendar maxCalendar = Calendar.getInstance();
        Date maxDate = getDateThroughOffset(maxPos);
        maxCalendar.set(maxDate.year, maxDate.month-1, maxDate.day);
        datePicker.setMaxDate(maxCalendar.getTimeInMillis());
    }

    public static int getCalendarPos(Date date, int minPos, int maxPos) {
        for (int i = minPos; i <= maxPos; i++) {
            Date comparisonDate = getDateThroughOffset(i);
            if (comparisonDate.year == date.year && comparisonDate.month == date.month && comparisonDate.day == date.day) {
                return i;
            }
        }
        throw (new RuntimeException("Utils: Given date wasn't found in the supplied range."));
    }

    private static String readFile(File mFile) {
        char[] charText = new char[1000000];
        try {
            mFile.createNewFile();
            FileReader reader = new FileReader(mFile);
            reader.read(charText);

            StringBuilder sBuilder = new StringBuilder();
            for (char c : charText) {
                if (((int) c) == 0) {
                    break;
                }
                sBuilder.append(c);
            }
            return String.valueOf(sBuilder);
        } catch (Exception e){
            e.printStackTrace();
            throw(new RuntimeException("An error has occurred during an attempt to read data: " + e.getMessage()));
        }
    }

    public static void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {
        File dir = new File(mcoContext.getFilesDir(), APP_RESULTS_FILE_DIRECTORY);
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File mFile = new File(dir, sFileName);
            mFile.createNewFile();
            String originalText = readFile(mFile);
            Log.d("APP_DEBUGGER", "Original text = " + originalText);

            FileWriter writer = new FileWriter(mFile);
            writer.write(originalText + "\n" + sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
            throw(new RuntimeException("An error has occurred during an attempt to save data: " + e.getMessage()));
        }
    }

    public static boolean checkIfDataIsAlreadySaved(Context mcoContext, String sFileName, String sBody) {
        File dir = new File(mcoContext.getFilesDir(), APP_RESULTS_FILE_DIRECTORY);
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File mFile = new File(dir, sFileName);
            mFile.createNewFile();
            return (readFile(mFile).contains(sBody));
        } catch (Exception e){
            e.printStackTrace();
            throw(new RuntimeException("An error has occurred during an attempt to read data: " + e.getMessage()));
        }
    }

    public static ArrayList<PersonalDataFormat> getResultListFromFile(Context mcoContext, String sFileName) {
        File dir = new File(mcoContext.getFilesDir(), APP_RESULTS_FILE_DIRECTORY);
        if(!dir.exists()){
            dir.mkdir();
        }

        String resultString;
        try {
            File mFile = new File(dir, sFileName);
            mFile.createNewFile();
            resultString = readFile(mFile);
        } catch (Exception e){
            e.printStackTrace();
            throw(new RuntimeException("An error has occurred during an attempt to read data: " + e.getMessage()));
        }

        ArrayList<PersonalDataFormat> resultList = new ArrayList<>();
        String[] stringResultArray = resultString.split("\n");
        for (String i : stringResultArray) {
            PersonalDataFormat newObject = new Gson().fromJson(i, PersonalDataFormat.class);
            if (newObject == null) {
                Log.d("APP_DEBUGGER", "Failed to add object: " + i);
                continue;
            }
            resultList.add(new Gson().fromJson(i, PersonalDataFormat.class));
            Log.d("APP_DEBUGGER", "Added object: " + i);
        }
        return resultList;
    }
}
