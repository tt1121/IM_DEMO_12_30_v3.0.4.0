package com.yzx.db;


/**
* @Title DBObserver 
* @Description ���ݿ�ı�۲���
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 ����12:25:01 
*/
public class DBObserver extends YZXObservable<OnDbChangeListener> {

	/**
	* @Description ֪ͨ�۲���
	* @param notifyId	֪ͨid
	* @date 2015-12-2 ����12:26:51 
	* @author zhuqian  
	* @return void
	 */
	public void notify(String notifyId){
		synchronized (observers) {
			for(int i = observers.size() - 1;i>=0;i--){
				if(observers.get(i) != null){
					observers.get(i).onChange(notifyId);
				}
			}
		}
	}
}
