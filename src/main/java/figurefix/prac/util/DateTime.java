/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including 
 * without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to 
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package figurefix.prac.util;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;

public class DateTime {
	
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static DateTime now() {
		return new DateTime(System.currentTimeMillis());
	}
	
	/**
	 * calculate different days between the two time object.<br> 
	 * ignore the hours in a day, 
	 * any yyyy-mm-dd hh:mm:ss SSS will be treated as yyyy-mm-dd 00:00:00 000
	 * @param d0 time from
	 * @param d1 time to
	 * @return diff days
	 */
	public static int diffDays(DateTime d0, DateTime d1) {
		DateTime dd0 = DateTime.getInstance(d0.toDate10());
		DateTime dd1 = DateTime.getInstance(d1.toDate10());
		long diff = Math.abs(dd1.getTimeMillis()-dd0.getTimeMillis());
		return (int)(diff/1000/3600/24);
	}
	
	/**
	 * calculate different months between the two time object.<br> 
	 * ignore the days in a month, 
	 * any yyyy-mm-dd will be treated as yyyy-mm-00 
	 * @param d0 DateTime
	 * @param d1 DateTime
	 * @return different months
	 */
	public static int diffMonths(DateTime d0, DateTime d1) {
		return Math.abs(
			(d1.year()-d0.year())*12 + d1.month() - d0.month()
		);
	}
	
	private static DateTime getMost(boolean early, DateTime ... times) {
		DateTime dt = null;
		int i=0; 
		while(dt==null && i<times.length) {
			dt = times[i++];
		}
		while(i<times.length) {
			if(times[i]!=null && (early ? times[i].before(dt) : times[i].after(dt))) {
				dt = times[i];
			}
			i++;
		}
		return dt;
	}
	
	/**
	 * get the earliest DateTime
	 * @param times time list
	 * @return the earliest
	 */
	public static DateTime getEarliest(DateTime ... times) {
		return getMost(true, times);
	}
	
	/**
	 * get the latest DateTime
	 * @param times time list
	 * @return the latest
	 */
	public static DateTime getLatest(DateTime ... times) {
		return getMost(false, times);
	}
	
	private static boolean nonDigitSeperated(String str) {
		for(char c : str.toCharArray()) {
			if( ! Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * test whether the time string is parseable
	 * @param time a string that represents time
	 * @return true:parseable, false:not parseable
	 */
	public static boolean isParseable(String time) {
		return getInstance(time)!=null;
	}
	
	/**
	 * same to the constructor DateTime(String) but returns null when catch exception
	 * @param str a string that represents time
	 * @return DateTime instanceo or null
	 */
	public static DateTime getInstance(String str) {
		try {
			return new DateTime(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	private long timemillis = -1;
	private Date date = null;
	private Calendar calendar = null;
	
	public DateTime(long time) {
		this.timemillis = time;
		this.date = new Date(this.timemillis);
		this.calendar = Calendar.getInstance();
		this.calendar.setTimeInMillis(this.timemillis);
	}
	
	/**
	 * <p>
	 * construct from time string.<br>
	 * the time string should be sequence of space seperated 
	 * <code>DATE+TIME+MILLISECOND</code> 
	 * where <code>MILLISECOND</code> or both <code>TIME+MILLISECOND</code> can be absent.<br>
	 * e.g. <code>yyyyMMdd HHmmss SSS</code> <br>
	 * </p>
	 * <p>
	 * <code>DATE</code> is a sequence of <code>YEAR+MONTH+DAY</code><br>
	 * <code>TIME</code> is a sequence of <code>HOUR+MINUTE+SECOND</code><br>
	 * <code>MILLISECOND</code> is an integer less than 1000<br>
	 * </p>
	 * <p>
	 * different parts IN <code>DATE</code> or <code>TIME</code> 
	 * can be seperated by any non digit character (except space).<br>
	 * e.g. <code>yyyy-MM-dd, yy/MM.dd</code><br>
	 * e.g. <code>HH:mm:ss, HH#mm;ss</code><br>
	 * if <code>DATE</code> does not contains separator, it should has a fixed length 8 or 6<br>
	 * e.g. <code>yyyyMMdd or yyMMdd</code><br>
	 * if <code>TIME</code> does not contains separator, it should has a fixed length 6<br>
	 * e.g. HHmmss<br>
	 * </p>
	 * @param datetime a representation of datetime
	 */
	public DateTime(String datetime) {
		
		String err = "Invalide DateTime format \""+datetime+"\"";
		
		if(datetime==null || datetime.trim().length()==0) {
			throw new DateTimeException(err);
		}
		
		String year = null;
		String month = null;
		String day = null;
		String hour = "00";
		String minute = "00";
		String second = "00";
		String msec = "000";
		
		String[] parts = datetime.split("(\\s)+"); // seperated by space
		if(parts.length>=1) { // contains date
			String date = parts[0];
			if(nonDigitSeperated(date)) {
				String[] dparts = date.split("(\\D)+"); // seperated by non digit
				if(dparts.length!=3) {
					throw new DateTimeException(err);
				}
				year = dparts[0];
				month = dparts[1];
				day = dparts[2];
			} else if(date.length()==8) {
				year = date.substring(0, 4);
				month = date.substring(4, 6);
				day = date.substring(6);
			} else if(date.length()==6) {
				year = date.substring(0, 2);
				month = date.substring(2, 4);
				day = date.substring(4);
			} else {
				throw new DateTimeException(err);
			}
			
			if(parts.length>=2) { // contains time
				String time = parts[1];
				int tlen = time.length();
				int i=0;
				if(i<tlen && Character.isDigit(time.charAt(i))) {
					hour = String.valueOf(time.charAt(i++)); // read hour
					if(i<tlen && Character.isDigit(time.charAt(i))) {
						hour += time.charAt(i++);
					}
					i++;
					
					if(i<tlen) {
						if(Character.isDigit(time.charAt(i))) {
							minute = String.valueOf(time.charAt(i++)); // read minute
							if(i<tlen && Character.isDigit(time.charAt(i))) {
								minute += time.charAt(i++);
							}
							i++;
							
							if(i<tlen) {
								if(Character.isDigit(time.charAt(i))) {
									second = String.valueOf(time.charAt(i++)); // read second
									if(i<tlen && Character.isDigit(time.charAt(i))) {
										second += time.charAt(i++);
									}
									i++;
									
									if(i<tlen) {
										msec = time.substring(i);
									} else if(parts.length>=3) {
										msec = parts[2];
									}
								} else {
									throw new DateTimeException(err);
								}
							}
						} else {
							throw new DateTimeException(err);
						}
					}
				} else {
					throw new DateTimeException(err);
				}
			}
		} else {
			throw new DateTimeException(err);
		}

		Calendar cc = Calendar.getInstance();
		int lyear = cc.get(Calendar.YEAR);
		
		int iyear = Integer.parseInt(year);
		if(iyear<10) {
			iyear += (lyear/10)*10;
		} else if(iyear<100) {
			iyear += (lyear/100)*100;
		} else if(iyear<1000) {
			iyear += (lyear/1000)*1000;
		}

		cc.set(iyear, 
			Integer.parseInt(month)-1, 
			Integer.parseInt(day), 
			Integer.parseInt(hour), 
			Integer.parseInt(minute),
			Integer.parseInt(second));
		cc.set(Calendar.MILLISECOND, Integer.parseInt(msec));
		
		this.timemillis = cc.getTimeInMillis();
		this.calendar = cc;
		this.date = new Date(this.timemillis);
	}

	public long getTimeMillis() {
		return this.timemillis;
	}
	
	public boolean before(DateTime time) {
		if(time==null) {
			throw new IllegalArgumentException(DateTime.class.getName()+" null");
		}
		return this.timemillis < time.timemillis;
	}
	
	public boolean after(DateTime time) {
		if(time==null) {
			throw new IllegalArgumentException(DateTime.class.getName()+" null");
		}
		return this.timemillis > time.timemillis;
	}
	
	public boolean equals(Object o) {
		return (o instanceof DateTime) && (((DateTime)o).timemillis == this.timemillis);
	}
	
	public DateTime daysAgo(int days) {
		return new DateTime(this.timemillis - 1000L * 3600 * 24 * days);
	}
	
	public DateTime daysLater(int days) {
		return new DateTime(this.timemillis + 1000L * 3600 * 24 * days);
	}
	
	public DateTime hoursBefore(int hours) {
		return new DateTime(this.timemillis - 1000L * 3600 * hours);
	}
	
	public DateTime hoursAfter(int hours) {
		return new DateTime(this.timemillis + 1000L * 3600 * hours);
	}

	public DateTime secondsBefore(int seconds) {
		return new DateTime(this.timemillis - 1000L * seconds);
	}
	
	public DateTime secondsAfter(int seconds) {
		return new DateTime(this.timemillis + 1000L * seconds);
	}
	
	public int year() {
		return this.toCalendar().get(Calendar.YEAR);
	}
	
	/**
	 * month from 1 to 12
	 * @return month
	 */
	public int month() {
		return this.toCalendar().get(Calendar.MONTH)+1;
	}
	
	/**
	 * day of week from 0 (sunday) to 6 (saturday)
	 * @return day of week
	 */
	public int dayOfWeek() {
		Calendar c = this.toCalendar();
		return c.get(Calendar.DAY_OF_WEEK)-1;
	}
	
	/**
	 * day of month from 1 to 31
	 * @return day of month
	 */
	public int dayOfMonth() {
		Calendar c = this.toCalendar();
		return c.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * hour from 0 to 23
	 * @return hour
	 */
	public int hour() {
		return this.toCalendar().get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * minute from 0 to 59
	 * @return minute
	 */
	public int minute() {
		return this.toCalendar().get(Calendar.MINUTE);
	}
	
	/**
	 * second from 0 to 59
	 * @return second
	 */
	public int second() {
		return this.toCalendar().get(Calendar.SECOND);
	}
	
	public int millisecond() {
		return this.toCalendar().get(Calendar.MILLISECOND);
	}
	
	public Date toDate() {
		return this.date;
	}
	
	public Calendar toCalendar() {
		return this.calendar;
	}

	public String toString() {
		return this.toString(TIME_FORMAT);
	}
	
	/**
	 * to the formated string
	 * @param format yyyy(year) MM(month) dd(day) HH(hour) mm(minute) ss(second) SSS(millisecond)
	 * @return the formated string
	 */
	public String toString(String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(this.toDate());
	}

	public String toDate10(char delimiter) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy"+delimiter+"MM"+delimiter+"dd");
		return f.format(this.toDate());
	}
	
	/**
	 * to date string as yyyy-MM-dd
	 * @return date string
	 */
	public String toDate10() {
		return toDate10('-');
	}
	
	public String toDate8() {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		return f.format(this.toDate());
	}
	
	public String toDate6() {
		SimpleDateFormat f = new SimpleDateFormat("yyMMdd");
		return f.format(this.toDate());
	}
	
	public String toTime6() {
		SimpleDateFormat f = new SimpleDateFormat("HHmmss");
		return f.format(this.toDate());
	}

	public String toTime8(char delimiter) {
		SimpleDateFormat f = new SimpleDateFormat("HH"+delimiter+"mm"+delimiter+"ss");
		return f.format(this.toDate());
	}
	
	public String toTime8() {
		return this.toTime8(':');
	}
	
	public String toTime9() {
		SimpleDateFormat f = new SimpleDateFormat("HHmmssSSS");
		return f.format(this.toDate());
	}
}
