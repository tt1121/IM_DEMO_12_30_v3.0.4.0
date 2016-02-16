package com.yzx.tools;

import java.io.File;
import android.os.Environment;


/**
* @Title FileTools 
* @Description 文件管理工具
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-8 上午10:31:48 
*/
public class FileTools {

	public static String getSdCardDir(){
		String path = "";
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/yunzhixun");
			if (!file.exists()) {
				file.mkdirs();
			}
			path = file.getAbsolutePath();
		} else {
			File mntFile = new File("/mnt");
			File[] mntFileList = mntFile.listFiles();
			if (mntFileList != null) {
				for (int i = 0; i < mntFileList.length; i++) {
					String mmtFilePath = mntFileList[i].getAbsolutePath();
					String sdPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
					if (!mmtFilePath.equals(sdPath)
							&& mmtFilePath.contains(sdPath)) {
						File file = new File(mmtFilePath + "/yunzhixun");
						if (!file.exists()) {
							file.mkdirs();
						}
						path = file.getAbsolutePath();
					}
				}
			}
		}
		return path;
	}
}
