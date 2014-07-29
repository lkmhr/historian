package com.lkmhr.historian.utils;

import java.util.Calendar;
import java.util.Locale;

public class UrlProvider {
	
	private final static String urlPrefix = "http://en.wikipedia.org//w/api.php?action=parse&format=txt&prop=text&uselang=en&section=";			
	private final static String urlSuffix = "&contentformat=text%2Fplain&contentmodel=text&mobileformat=html&noimages=&mainpage=&page=";
	
	public UrlProvider() { }
	
	public static String getUrl(int position){
		
		
		String bakedUrl =  urlPrefix + position + urlSuffix + getDate();
		
		return bakedUrl;
	}
	
	private static String getDate(){
		Calendar cal = Calendar.getInstance();
		
		String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
		int date = cal.get(Calendar.DAY_OF_MONTH);

		String today = month + "_" + date;
		return today;
	}
	
}
