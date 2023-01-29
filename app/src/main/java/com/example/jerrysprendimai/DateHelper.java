package com.example.jerrysprendimai;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
  public static String get_timestamp_display(String timestamp){
      SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
      SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
      //SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");

      String result = "";
      Date dateObj;
      try {
          dateObj = mysqlDateFormat.parse(timestamp);
          result = displayDateFormat.format(dateObj);
      } catch (ParseException e) {
          e.printStackTrace();
      }
      return result;
  }
  public static String get_current_date_mysql(){
      SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      //SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy");

      Date currentTime = Calendar.getInstance().getTime();

      String result = mysqlDateFormat.format(currentTime);
      return result;
  }
  public static String get_current_date_disply(){
      SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      //SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy");

      Date currentTime = Calendar.getInstance().getTime();

      String result = displayDateFormat.format(currentTime);
      return result;
  }
  public static String get_date_mysql(String date){
      SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      //SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy");
      SimpleDateFormat mysqlDateFormat   = new SimpleDateFormat("yyyy-MM-dd");
      Date dateObj;
      String result = "";
      try {
          dateObj = displayDateFormat.parse(date);
          result = mysqlDateFormat.format(dateObj);
      } catch (ParseException e) {
          e.printStackTrace();
      }
      return result;
  }
  public static String get_date_display(String date){
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat mysqlDateFormat   = new SimpleDateFormat("yyyy-MM-dd");
        Date dateObj;
        String result = "";
        try {
            dateObj = mysqlDateFormat.parse(date);
            result = displayDateFormat.format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
  public static String get_YMD_from_date_display(String valueOf, String date){
      //--data fromat dd-MM-yyyy
      String result = "";
      String dateArr[] = date.split("-");
          switch (valueOf){
              case "year":
                  result = dateArr[0];
                  //result = dateArr[2];
                  break;
              case "month":
                  result = dateArr[1];
                  break;
              case "day":
                  result = dateArr[2];
                  //result = dateArr[0];
                  break;
          }


      return result;
  }
}
