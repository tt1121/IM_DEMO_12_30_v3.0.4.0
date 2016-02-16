package com.yzx.im_demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import com.yzx.crash.CrashHandler;
import com.yzx.tools.ImgCache;
import com.yzx.tools.YZXContents;
import com.yzxIM.IMApplication;

/**
 * @author zhuqian
 */
public class MainApplication extends IMApplication {
	
	private static final String TAG = "MainApplication";
	public static MainApplication mInstance;
	public static NotificationManager nm;
	
	private Map<String, Thread> downLoadThreads;
	
	public String targetId = "";
	
	
	//存放下载线程
	public synchronized void putThread(String msgId, Thread t){
		if(downLoadThreads == null){
			downLoadThreads = Collections.synchronizedMap(new HashMap<String, Thread>());
		}
		downLoadThreads.put(msgId, t);
		Log.i(TAG, "putThread msgId = "+msgId);
	}
	//获取下载线程
	public synchronized Thread getThread(String msgId){
		if(msgId != null && downLoadThreads != null){
			Log.i(TAG, "getThread msgId = "+msgId);
			return downLoadThreads.get(msgId);
		}
		return null;
	}
	public synchronized Thread removeThread(String msgId){
		if(msgId != null && downLoadThreads != null && downLoadThreads.containsKey(msgId)){
			Log.i(TAG, "removeThread msgId = "+msgId);
			return downLoadThreads.remove(msgId);
		}
		return null;
	}
	

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "MainApplication onCreate()");
		mInstance =this;
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		ImgCache.getInstance().init(this);
		
		//崩溃日志保存
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());
        
        //保存一些系统常量
        YZXContents.setContext(this);
	}

	public static MainApplication getInstance() {
		return mInstance;
	}
	
	public static NotificationManager getNotificationManager() {
		return nm;
	}
}
