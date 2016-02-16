package com.yzx.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;



/**
* @Title ScreenShotTools 
* @Description ��Ļ��ͼ����
* @Company yunzhixun
* @author zhuqian
* @date 2015-11-16 ����5:56:42 
*/
public class ScreenShotTools {

	/**
	 * ��ͼ
	 * @param activity
	 * @return ͼƬ����
	 */
	public static Bitmap shot(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		
		Bitmap b1 = view.getDrawingCache();
		
		//��ȡ״̬�����
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		
		
		int statusBarHeight = frame.top;

		//��ȡ��Ļ���
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();

		int height = activity.getWindowManager().getDefaultDisplay().getHeight();//ȥ��������
		
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);

		view.destroyDrawingCache();

		return b;
	}
	
	private static void savePic(Bitmap bitmap,String strFileName){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
