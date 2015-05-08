package com.trainer.g14.g_trainer;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
* written by: Jan Anthony Miranda
* tested by: Jan Anthony Miranda
* debugged by: Jan Anthony Miranda
*/
public class dates {
    private static final String TAG = dates.class.getSimpleName(); //for Log() when debugging

    public dates(){
    //empty constructor
    }

    //return int representation of month 1-12
    private int getMonth(String month){
        switch (month.toUpperCase()){
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
        }
        return -1;
    }

    //returns how many days date2 is after date1
    public long compareDates(String date1, String date2){
        boolean single=false;
        boolean single2=false;

        //month
        String month1 = date1.substring(5,8);
        int month1i=getMonth(month1);
        String month2 = date2.substring(5,8);
        int month2i=getMonth(month2);

        //day
        String day1 = date1.substring(9,11);
        if(day1.substring(1).equals(",")){
            single = true;
            day1=date1.substring(9,10);
        }
        int day1i=Integer.parseInt(day1);

        String day2 = date2.substring(9,11);
        if(day2.substring(1).equals(",")){
            single2 = true;
            day2=date2.substring(9,10);
        }
        int day2i=Integer.parseInt(day2);

        //year
        String year1;
        if(single)
            year1 = date1.substring(12,16);
        else
            year1 = date1.substring(13,17);
        int year1i=Integer.parseInt(year1);
        String year2;
        if(single2)
            year2 = date2.substring(12,16);
        else
            year2 = date2.substring(13,17);
        int year2i=Integer.parseInt(year2);


        //Log.i(TAG, "Date1 :" + month1i + "/" + day1i + "/" + year1i);
        //Log.i(TAG, "Date2 :"+month2i+"/"+day2i+"/"+year2i);

        return diff(year2i, month2i, day2i, year1i, month1i, day1i);
    }

    private static int julianDay(int year, int month, int day) {
        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;
        int jdn = day + (153 * m + 2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
        return jdn;
    }

    private static int diff(int y1, int m1, int d1, int y2, int m2, int d2) {
        return julianDay(y1, m1, d1) - julianDay(y2, m2, d2);
    }

    //returns todays date
    public String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, MMM d, yyyy h:mm a", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //returns date of a given date
    private String getDateTime(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, MMM d, yyyy h:mm a", Locale.getDefault());
        Date date = new Date(year-1900,month,day,0,0);
        return dateFormat.format(date);
    }

    //returns int representation of a day within a week 0-6
    public int getWorkoutsWeek(String date){
        String day = date.substring(0,3);
        switch (day){
            case "Mon":
                return 0;
            case "Tues":
                return 1;
            case "Wed":
                return 2;
            case "Thu":
                return 3;
            case "Fri":
                return 4;
            case "Sat":
                return 5;
            case "Sun":
                return 6;
        }
        return -1;
    }

}
