package com.billing.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.billing.exceptions.ExceptionMsg;

public class DateUtil {
	
	public final static String DATE_FORMAT_DE = "dd.MM.yyyy";
	
	public final static String DATE_FORMAT_US = "yyyy-MM-dd";
	
	public final static String DATE_FORMAT_US_WITH_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
		
	public final static String DATE_FORMAT = "dd-MM-yyyy";
	

	public static String getDateToStringFormat(Date date, String pattern) {
		String stringFormat = "";
		try {
			if(date != null) {
				if(pattern == null || (pattern != null && pattern.trim().isEmpty())) {
					pattern = DATE_FORMAT_DE;
				}
				SimpleDateFormat df = new SimpleDateFormat(pattern);
				stringFormat = df.format(date);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return stringFormat;
	}
	
	public static Date getStringToDateFormat(String dateStr, String pattern) {
		Date date = null;
		try {
			if(dateStr != null && !dateStr.isEmpty()) {
				if(pattern == null || (pattern != null && pattern.isEmpty())) {
					pattern = DATE_FORMAT_DE;
				}
				SimpleDateFormat df = new SimpleDateFormat(pattern);
				date = df.parse(dateStr);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date convertFormattedFullDate(String date) {
		DateFormat dfIn = new SimpleDateFormat(DATE_FORMAT_US+" HH:mm:ss"); 
	    Date startDate = null;
	    try {
	        startDate = dfIn.parse(date);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return startDate;
	}
	
	public static String getCurrentYear(){
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		return String.valueOf(year);
	}
	
	public static String getCurrentMonth(){
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH);
		return String.valueOf(month);
	}
	
	public static int getCurrentYearInt(){
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		return year;
	}
	
	public static int getCurrentMonthInt(){
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH);
		return month;
	}
	
	public static String getStartDateWithTimeofParticularYear(int year) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_WITH_TIMESTAMP);
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, year);
    	cal.set(Calendar.DAY_OF_YEAR, 1);    
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return dateFormat.format(cal.getTime());
    }
    
    public static String getEndDateWithTimeofParticularYear(int year) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_WITH_TIMESTAMP);
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.MONTH, 11); // 11 = december
    	cal.set(Calendar.DAY_OF_MONTH, 31);
    	cal.set(Calendar.HOUR_OF_DAY, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
    	cal.set(Calendar.MILLISECOND, 999);
    	return dateFormat.format(cal.getTime());
    }
    
    public static String getStartDateofMonthWithStartTime() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_WITH_TIMESTAMP);
    	Calendar c = Calendar.getInstance();
    	int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = 1;
		c.set(year, month, day);
		c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
		return dateFormat.format(c.getTime());
    }
    
    public static String getEndDateofMonthWithEndTime() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_WITH_TIMESTAMP);
    	Calendar c = Calendar.getInstance();
    	int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = 1;
		c.set(year, month, day);
		int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		c.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth-1);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return dateFormat.format(c.getTime());
    }
    
    public static Date Yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    
    public static int getDay() {
		return new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
	}
    
    public static int getDay(Date datum) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(datum);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
    
    /**
     * This method adds workdays (MONDAY - FRIDAY) to a given calendar object.
     * If the number of days is negative than this method subtracts the working
     * days from the calendar object.
     * 
     * 
     * @param cal
     * @param days
     * @return new calendar instance
     */
    public static Calendar getWeekDaysDate(final Calendar baseDate, final int days) {
        Calendar resultDate = null;
        Calendar workCal = Calendar.getInstance();
        workCal.setTime(baseDate.getTime());

        int currentWorkDay = workCal.get(Calendar.DAY_OF_WEEK);

        // test if SATURDAY ?
        if (currentWorkDay == Calendar.SATURDAY) {
            // move to next FRIDAY
            workCal.add(Calendar.DAY_OF_MONTH, (days < 0 ? -1 : +2));
            currentWorkDay = workCal.get(Calendar.DAY_OF_WEEK);
        }
        // test if SUNDAY ?
        if (currentWorkDay == Calendar.SUNDAY) {
            // move to next FRIDAY
            workCal.add(Calendar.DAY_OF_MONTH, (days < 0 ? -2 : +1));
            currentWorkDay = workCal.get(Calendar.DAY_OF_WEEK);
        }

        // test if we are in a working week (should be so!)
        if (currentWorkDay >= Calendar.MONDAY && currentWorkDay <= Calendar.FRIDAY) {
            boolean inCurrentWeek = false;
            if (days > 0)
                inCurrentWeek = (currentWorkDay + days < 7);
            else
                inCurrentWeek = (currentWorkDay + days > 1);

            if (inCurrentWeek) {
                workCal.add(Calendar.DAY_OF_MONTH, days);
                resultDate = workCal;
            } else {
                int totalDays = 0;
                int daysInCurrentWeek = 0;

                // fill up current week.
                if (days > 0) {
                    daysInCurrentWeek = Calendar.SATURDAY - currentWorkDay;
                    totalDays = daysInCurrentWeek + 2;
                } else {
                    daysInCurrentWeek = -(currentWorkDay - Calendar.SUNDAY);
                    totalDays = daysInCurrentWeek - 2;
                }

                int restTotalDays = days - daysInCurrentWeek;
                // next working week... add 2 days for each week.
                int x = restTotalDays / 5;
                totalDays += restTotalDays + (x * 2);

                workCal.add(Calendar.DAY_OF_MONTH, totalDays);
                resultDate = workCal;
            }
        }   
        return resultDate;
    }
	
    public static List<Integer> getAllMonthInInteger() {
    	List<Integer> monthList = new ArrayList<Integer>();
    	Integer month[] = {0,1,2,3,4,5,6,7,8,9,10,11};
    	for(Integer mon : month) {
    		monthList.add(mon);
    	}
    	return monthList;
    }
    
    public static List<String> getAllMonthShortName(String localeString) {
    	List<String> monthsList = new ArrayList<String>();
    	Locale locale = new Locale(localeString);
	    String[] months = new DateFormatSymbols(locale).getShortMonths();
    	for (int i = 0; i < months.length; i++) {
    		String month = months[i];
    	    if(month!=null && !month.isEmpty())
    	    	monthsList .add(months[i]);
	    }
    	return monthsList;
    }
    
    public static String getStartDateOfMonth(int month , int year) {
    	String startDate = null;
    	try {
			Calendar c = Calendar.getInstance();
		    int day = 1;
		    c.set(year, month, day);
		    DateFormat dfOut = new SimpleDateFormat(DATE_FORMAT_US);
		    startDate = dfOut.format(c.getTime());
	    } catch (Exception e) {
	    	ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
	    }
		return startDate;
    }
    
    public static String getEndDateOfMonth(int month , int year) {
    	String endtDate = null;
    	try {
			Calendar c = Calendar.getInstance();
		    int day = 1;
		    c.set(year, month, day);
		    int numOfDaysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		    DateFormat dfOut = new SimpleDateFormat(DATE_FORMAT_US);
		    c.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth-1);
		    endtDate = dfOut.format(c.getTime());
	    } catch (Exception e) {
	    	ExceptionMsg.ErrorMsg(e, Thread.currentThread().getStackTrace()[1]);
	    }
		return endtDate;
    }
}
