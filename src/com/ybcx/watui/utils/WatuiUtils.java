package com.ybcx.watui.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class WatuiUtils {

	private static final  SimpleDateFormat simpledDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//TODO, some static functions here;
	
	public static String generateUID(){
		String uid = UUID.randomUUID().toString().replace("-", "").substring(16);
		return uid;
	}
	
	public static String getFormatNowTime(){
		String now = simpledDateFormat.format(new Date().getTime());
		return now;
	}
	
	public static String formatDate(Date date){
		return simpledDateFormat.format(date);
	}

	public static String formatLong(Long time){
		return simpledDateFormat.format(time);
	}
	
	
	public static String replace(String str){
		String s=str.replace("\\","\\\\");
		return s;
	}
	
	public static void main(String[] args) {
		String url="http: //thirdapp3.qlogo.cn/qzopenapp/9c4f93a14c4a6ca2005f2f2e1e9f50bcb91d789b2fb256fc4acbc0bad2fb6198/50";
		String openId = "14219AC2BF3FCDC2D80914235C042566";
		System.out.println(openId.length());
	 
	}
}
