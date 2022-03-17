package com.zestic.common.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  /**
   * Convert string date time to timestamp.
   * @param value input value is date time in string
   * @return
   */
  public static Timestamp timestampFromString(String value) {
    Timestamp timestamp = null;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
      Date parsedDate = dateFormat.parse(value);
      timestamp = new Timestamp(parsedDate.getTime());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return timestamp;
  }

  /**
   * Convert local date time to string timestamp.
   * @return
   */
  public static String dateStringFromLocalDate() {
    String timestamp = "";
    try {
      timestamp = new SimpleDateFormat(DATE_TIME_PATTERN).format(Calendar.getInstance().getTime());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return timestamp;
  }

  /**
   * Convert date to string.
   * @param date input
   * @return
   */
  public static String dateStringFromLocalDate(Date date) {
    String formatted;
    SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
    formatted = formatter.format(date);
    return formatted;
  }
}
