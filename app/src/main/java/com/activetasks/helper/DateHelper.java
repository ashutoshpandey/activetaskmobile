package com.activetasks.helper;

import java.text.SimpleDateFormat;

/**
 * Created by ashutosh on 13/05/2015.
 */
public class DateHelper {

    public static String formatStringDate(String date){

        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat toFormat = new SimpleDateFormat("dd/MM/yyyy");

        try{
            String resultDateStr = toFormat.format(fromFormat.parse(date));
            return resultDateStr;
        }
        catch(Exception ex){
            return date;
        }
    }
}
