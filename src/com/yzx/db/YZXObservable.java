package com.yzx.db;

import java.util.ArrayList;
import java.util.List;

import com.yzxtcp.tools.CustomLog;


/**
* @Title YZXObservable 
* @Description ���ݿ�ı�۲���
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 ����12:04:59 
*/
public abstract class YZXObservable<T> {

	protected final List<T> observers = new ArrayList<T>();
	
	/**
	* @Description ��ӹ۲���
	* @param observer	Ҫ��ӵĹ۲���
	* @date 2015-12-2 ����12:15:14 
	* @author zhuqian  
	* @return void
	 */
	public void addObserver(T observer){
		if(observer == null){
			throw new IllegalArgumentException("The addObserver is null");
		}
		synchronized (observers) {
			if(observers.contains(observer)){
				throw new IllegalStateException("The observer : "+observer+" is already added");
			}
			observers.add(observer);
		}
	}
	
	/**
	* @Description �Ƴ��۲���
	* @param observer	Ҫ�Ƴ��Ĺ۲���
	* @date 2015-12-2 ����12:16:23 
	* @author zhuqian  
	* @return void
	 */
	public void removeObserver(T observer){
		if(observer == null){
			throw new IllegalArgumentException("The addObserver is null");
		}
		synchronized (observers) {
			int i = observers.indexOf(observer);
			if(i == -1){
				CustomLog.d("YZXObservable removeObserver observer "+observer+" is not existed");
				return;
			}
			observers.remove(i);
		}
	}
	/**
	* @Description ��չ۲���
	* @date 2015-12-2 ����12:16:41 
	* @author zhuqian  
	* @return void
	 */
	public void clear(){
		synchronized (observers) {
			observers.clear();
		}
	}
}
