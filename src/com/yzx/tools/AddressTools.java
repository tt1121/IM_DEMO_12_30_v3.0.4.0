package com.yzx.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * �����ַ�жϹ���
 * @author zhuqian
 *
 */
public class AddressTools {

	/**
	 * �Ƿ��������ַ
	 * @param address �����ַ�ַ���
	 * @return true��ƥ����ȷ��false��ƥ�����
	 */
	public static boolean isNetAddress(String address){
		Pattern pattern = Pattern.compile("^[[a-z0-9]{1,}//.]{1}[[a-z0-9]{1,}//.]{1,}[a-z0-9]{1,}$");
		Matcher matcher = pattern.matcher(address);
		if(matcher.matches()){
			//����ƥ��
			pattern = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
			matcher = pattern.matcher(address);
			while(matcher.find()){
				if(Integer.parseInt(matcher.group(1)) > 255 || Integer.parseInt(matcher.group(2)) > 255
						|| Integer.parseInt(matcher.group(3)) > 255 || Integer.parseInt(matcher.group(4)) > 255){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * �Ƿ���Int���͵�����
	 * @param port �˿ں�
	 * @return true��ƥ����ȷ��false��ƥ�����
	 */
	public static boolean isPort(String port){
		Pattern pattern = Pattern.compile("^[1-9]{1}[0-9]+$");
		Matcher matcher = pattern.matcher(port);
		return matcher.matches();
	}
}
