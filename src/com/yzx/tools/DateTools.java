package com.yzx.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
* @Title DateTools 
* @Description 日期工具
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-17 下午3:55:29 
*/
public class DateTools {

	/**
	* @Description 截取时间字符串
	* @param time 时间，是否需要时间后缀
	* @return	返回截取的字符串(如：今天12:00，昨天13:29，12-17 16:14)
	* @date 2015-12-17 下午4:13:16 
	* @author zhuqian  
	* @return String
	 */
	public static String formatDate(String time,boolean needTime){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		if(time==null ||"".equals(time)){
			return "";
		}
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Calendar current = Calendar.getInstance();
		
		Calendar today = Calendar.getInstance();	//今天
		
		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
		//  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set( Calendar.HOUR_OF_DAY, 0);
		today.set( Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		
		Calendar yesterday = Calendar.getInstance();	//昨天
		
		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
		yesterday.set( Calendar.HOUR_OF_DAY, 0);
		yesterday.set( Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		
		current.setTime(date);
		
		if(current.after(today)){
			return "今天 "+time.split(" ")[1];
		}else if(current.before(today) && current.after(yesterday)){
			return "昨天 "+time.split(" ")[1];
		}else{
			int index = time.indexOf("-")+1;//"-"的索引
			int nIndex = time.indexOf(" ");//" "的索引
			
			Calendar oldYear = Calendar.getInstance();	//去年(今年的一月一日，00:00:00)之前
			
			oldYear.set(Calendar.YEAR, current.get(Calendar.YEAR));
			oldYear.set(Calendar.MONTH, 0);
			oldYear.set(Calendar.DAY_OF_MONTH,0);
			oldYear.set( Calendar.HOUR_OF_DAY, 23);
			oldYear.set( Calendar.MINUTE, 59);
			oldYear.set(Calendar.SECOND, 59);
			oldYear.set(Calendar.MILLISECOND, 59);
			
			
			if(needTime){
				if(current.after(oldYear)){
					return time.substring(index);
				}else{
					return time;
				}
			}else{
				if(current.after(oldYear)){
					return time.substring(index, nIndex);
				}else{
					return time.substring(0, nIndex);
				}
			}
		}
	}
}
