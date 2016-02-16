package com.yzx.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
* @Title DateTools 
* @Description ���ڹ���
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-17 ����3:55:29 
*/
public class DateTools {

	/**
	* @Description ��ȡʱ���ַ���
	* @param time ʱ�䣬�Ƿ���Ҫʱ���׺
	* @return	���ؽ�ȡ���ַ���(�磺����12:00������13:29��12-17 16:14)
	* @date 2015-12-17 ����4:13:16 
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
		
		Calendar today = Calendar.getInstance();	//����
		
		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
		//  Calendar.HOUR����12Сʱ�Ƶ�Сʱ�� Calendar.HOUR_OF_DAY����24Сʱ�Ƶ�Сʱ��
		today.set( Calendar.HOUR_OF_DAY, 0);
		today.set( Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		
		Calendar yesterday = Calendar.getInstance();	//����
		
		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
		yesterday.set( Calendar.HOUR_OF_DAY, 0);
		yesterday.set( Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		
		current.setTime(date);
		
		if(current.after(today)){
			return "���� "+time.split(" ")[1];
		}else if(current.before(today) && current.after(yesterday)){
			return "���� "+time.split(" ")[1];
		}else{
			int index = time.indexOf("-")+1;//"-"������
			int nIndex = time.indexOf(" ");//" "������
			
			Calendar oldYear = Calendar.getInstance();	//ȥ��(�����һ��һ�գ�00:00:00)֮ǰ
			
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
