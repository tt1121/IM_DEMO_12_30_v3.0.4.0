package com.yzx.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * Æ¥ÅäÊÖ»ú
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isPhoneNumber(String number){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(1[4,7]7)|)\\d{8}$");  
		Matcher m = p.matcher(number); 
		return m.matches();  
	}
}
