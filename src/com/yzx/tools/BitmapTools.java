package com.yzx.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.yzx.cache.impl.YZXImageLoader;
import com.yzx.im_demo.R;
import com.yzx.mydefineview.IMChatImageView;
import com.yzxIM.IMManager;
import com.yzxtcp.tools.CustomLog;

public class BitmapTools {
	 
	private static final String TAG = "BitmapTools";
	public static Bitmap pointBitmap;

	public static Bitmap loadImageBitmap(String url, int screenWidth, int screenHeigh){
		ImgCache mImgCache = ImgCache.getInstance();
    	Bitmap bitmap = mImgCache.getBitmapFromCache(url);
    	
        if (bitmap == null) {
        	if(screenWidth>1500){
            	screenWidth=screenWidth*7/10;
            	screenHeigh=screenHeigh*7/10;
            }else if(screenWidth>1000){
            	screenWidth=screenWidth*6/10;
            	screenHeigh=screenHeigh*6/10;
            }else if(screenWidth>700){
            	screenWidth=screenWidth*4/10;
            	screenHeigh=screenHeigh*4/10;
            }else if(screenWidth>400){
            	screenWidth=screenWidth*3/10;
            	screenHeigh=screenHeigh*3/10;
            }else {
            	screenWidth=screenWidth*2/10;
            	screenHeigh=screenHeigh*2/10;
    		}
            bitmap = BitmapFactory.decodeFile(url);
            if(null!=bitmap){
                if(bitmap.getWidth()>bitmap.getHeight()){
                	int height = (screenWidth*bitmap.getHeight())/bitmap.getWidth();
        	    	bitmap=ThumbnailUtils.extractThumbnail(bitmap, screenWidth, height);
                }else{
                	int width = (screenHeigh*bitmap.getWidth())/bitmap.getHeight();
        	    	bitmap=ThumbnailUtils.extractThumbnail(bitmap, width, screenHeigh);
                }
                return bitmap;
           }
        }
		return bitmap;
	}
	public static void loadImageBitmap(Context context, String url, ImageView imageView, 
			int screenWidth, int screenHeigh) {
		Drawable defaultDrawable = context.getResources().getDrawable(R.drawable.default_img);
		if (TextUtils.isEmpty(url)) {
    		if(imageView!=null){
    			imageView.setImageDrawable(defaultDrawable);
    		}
    		return;
        }

    	ImgCache mImgCache = ImgCache.getInstance();
    	Bitmap bitmap = mImgCache.getBitmapFromCache(url);
    	
        if (bitmap != null) {
        	if(imageView!=null){
        		Drawable drawable = new BitmapDrawable(bitmap);	
        		imageView.setImageDrawable(drawable);
        	}
        }else{
        	File mFile=new File(url);
            if(!mFile.exists()){
            	if(imageView!=null){
        			imageView.setImageDrawable(defaultDrawable);
        		}
        		return;
            }
        	if(screenWidth>1500){
            	screenWidth=screenWidth*7/10;
            	screenHeigh=screenHeigh*7/10;
            }else if(screenWidth>1000){
            	screenWidth=screenWidth*6/10;
            	screenHeigh=screenHeigh*6/10;
            }else if(screenWidth>700){
            	screenWidth=screenWidth*4/10;
            	screenHeigh=screenHeigh*4/10;
            }else if(screenWidth>400){
            	screenWidth=screenWidth*3/10;
            	screenHeigh=screenHeigh*3/10;
            }else {
            	screenWidth=screenWidth*2/10;
            	screenHeigh=screenHeigh*2/10;
    		}
            bitmap = BitmapFactory.decodeFile(url);
            if(null!=bitmap){
                if(bitmap.getWidth()>bitmap.getHeight()){
                	int height = (screenWidth*bitmap.getHeight())/bitmap.getWidth();
            		bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, height,true);
                }else{
                	int width = (screenHeigh*bitmap.getWidth())/bitmap.getHeight();
        	    	bitmap = Bitmap.createScaledBitmap(bitmap, width, screenHeigh,true);
                }
    	    	mImgCache.addBitmapToCache(url, bitmap);
    	    	if(imageView!=null){
    	    		Drawable drawable = new BitmapDrawable(bitmap);
            		imageView.setImageDrawable(drawable);
            	}
            }
        }
        bitmap = null;
    }
	
	public static void loadChatImageBitmap(Context context, String url, IMChatImageView imageView, 
			int screenWidth, int screenHeigh,boolean isMySelf) {
		if (TextUtils.isEmpty(url)) {
    		if(imageView!=null){
    			Bitmap defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
    			imageView.setImageBitmap(defaultBitmap);
    		}
    		return;
        }

    	YZXImageLoader yzxImageLoader = YZXImageLoader.newInstance(context);
    	Bitmap bitmap = yzxImageLoader.get(url);
    	
        if (bitmap != null) {
        	if(imageView!=null){
        		imageView.setImageBitmap(bitmap);
        		return;
        	}
        }else{
        	File mFile=new File(url);
            if(!mFile.exists()){
            	if(imageView!=null){
            		Bitmap defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
        			imageView.setImageBitmap(defaultBitmap);
        		}
        		return;
            }
            bitmap = BitmapFactory.decodeFile(url);
            //ѡ������ʵ����ű���
            Bitmap newBitmap = null;
            if(isMySelf){
            	newBitmap = fillterBimtapBySelf(context,bitmap,screenWidth,screenHeigh);
            }else{
            	newBitmap = fillterBimtap(context,bitmap,screenWidth,screenHeigh);
            }
            if(bitmap != null && !bitmap.isRecycled()){
            	bitmap.recycle();
            	bitmap = null;
            	System.gc();
            }
            if(null != newBitmap){
            	yzxImageLoader.put(url, newBitmap);
    	    	if(imageView!=null){
            		imageView.setImageBitmap(newBitmap);
            	}
            }
        }
        bitmap = null;
    }
	
	public static void loadImageBitmapNoCompress(Context context, String url, ImageView imageView,BitmapIsPutInCacheListener mListener) {
		if (TextUtils.isEmpty(url)) {
    		if(imageView!=null){
    			Drawable defaultDrawable = context.getResources().getDrawable(R.drawable.default_img);
    			imageView.setImageDrawable(defaultDrawable);
    		}
    		return;
        }
    	YZXImageLoader yzxImageLoader = YZXImageLoader.newInstance(context);
    	Bitmap bitmap = yzxImageLoader.get(url);
    	
        if (bitmap != null) {
        	if(imageView!=null){
        		Drawable drawable = new BitmapDrawable(bitmap);	
        		imageView.setImageDrawable(drawable);
        		return;
        	}
        }else{
        	File mFile=new File(url);
            if(!mFile.exists()){
            	if(imageView!=null){
            		Drawable defaultDrawable = context.getResources().getDrawable(R.drawable.default_img);
        			imageView.setImageDrawable(defaultDrawable);
        		}
        		return;
            }
            bitmap = BitmapFactory.decodeFile(url);
            if(null != bitmap){
            	if(mListener != null){
            		boolean isPut = yzxImageLoader.put(url, bitmap);
            		if(isPut){
            			mListener.putInCache(null);
            		}else{
            			mListener.putInCache(bitmap);
            		}
            	}else{
            		yzxImageLoader.put(url, bitmap);
            	}
    	    	if(imageView!=null){
    	    		Drawable drawable = new BitmapDrawable(bitmap);
            		imageView.setImageDrawable(drawable);
            	}
            }
        }
        bitmap = null;
    }
	
	public static void loadImageBitmapFromMySelf(Context context, String url, ImageView imageView,BitmapIsPutInCacheListener mListener) {
		Drawable defaultDrawable = context.getResources().getDrawable(R.drawable.default_img);
		if (TextUtils.isEmpty(url)) {
    		if(imageView!=null){
    			imageView.setImageDrawable(defaultDrawable);
    		}
    		return;
        }
    	YZXImageLoader yzxImageLoader = YZXImageLoader.newInstance(context);
    	Bitmap bitmap = yzxImageLoader.get(url+"_byMyself");;
    	
        if (bitmap != null) {
        	if(imageView!=null){
        		Drawable drawable = new BitmapDrawable(bitmap);	
        		imageView.setImageDrawable(drawable);
        	}
        }else{
        	File mFile=new File(url);
            if(!mFile.exists()){
            	if(imageView!=null){
        			imageView.setImageDrawable(defaultDrawable);
        		}
        		return;
            }
            bitmap = BitmapFactory.decodeFile(url);
            if(null!=bitmap){
            	url = url + "_byMyself";
            	if(mListener != null){
            		boolean isPut = yzxImageLoader.put(url, bitmap);
            		if(isPut){
            			mListener.putInCache(null);
            		}else{
            			mListener.putInCache(bitmap);
            		}
            	}else{
            		yzxImageLoader.put(url, bitmap);
            	}
    	    	if(imageView!=null){
    	    		Drawable drawable = new BitmapDrawable(bitmap);
            		imageView.setImageDrawable(drawable);
            	}
            }
        }
        bitmap = null;
    }
	
	/**
     * ����BitmapͼƬ����
     * @param bitmap
     */
    public static void recycle(Bitmap bitmap){
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            
        }
    }
    
    private static boolean cancelPotentialBitmapDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }
    
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable == null || !(drawable instanceof DownloadedDrawable)) {
                return null;
            }
            DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
            return downloadedDrawable.getBitmapDownloaderTask();
        }
        return null;
    }
    
    static class DownloadedDrawable extends BitmapDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(Drawable defaultBitmap, BitmapDownloaderTask bitmapDownloaderTask) {
            super(((BitmapDrawable) defaultBitmap).getBitmap());
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
                    bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
    
    static class BitmapDownloaderTask extends AsyncTask<Object, Void, Bitmap> {
    	private WeakReference<ImageView> imageViewReference;
    	private ImgCache mImgCache;
        private Context context;
        private String url;
        private int screenWidth;
        private int screenHeigh;
        public BitmapDownloaderTask(ImageView imageView) {
        	if(imageView!=null){
        		imageViewReference = new WeakReference<ImageView>(imageView);
        	}
            mImgCache = ImgCache.getInstance();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
        	context = (Context) params[0];
            url = (String) params[1];
            screenWidth = (Integer) params[2];
            screenHeigh = (Integer) params[3];
            if(screenWidth>1500){
            	screenWidth=screenWidth*7/10;
            	screenHeigh=screenHeigh*7/10;
            }else if(screenWidth>1000){
            	screenWidth=screenWidth*6/10;
            	screenHeigh=screenHeigh*6/10;
            }else if(screenWidth>700){
            	screenWidth=screenWidth*4/10;
            	screenHeigh=screenHeigh*4/10;
            }else if(screenWidth>400){
            	screenWidth=screenWidth*3/10;
            	screenHeigh=screenHeigh*3/10;
            }else {
            	screenWidth=screenWidth*2/10;
            	screenHeigh=screenHeigh*2/10;
			}
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            if(null!=bitmap){
	            if(bitmap.getWidth()>bitmap.getHeight()){
	            	int height = (screenWidth*bitmap.getHeight())/bitmap.getWidth();
	    	    	bitmap=ThumbnailUtils.extractThumbnail(bitmap, screenWidth, height);
	            }else{
	            	int width = (screenHeigh*bitmap.getWidth())/bitmap.getHeight();
	    	    	bitmap=ThumbnailUtils.extractThumbnail(bitmap, width, screenHeigh);
	            }
		    	mImgCache.addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        void getSize(){
        	
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = null;
                bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                if (this == bitmapDownloaderTask && bitmap != null) {
                	imageView.setImageDrawable(new BitmapDrawable(bitmap));
                }
            }
        }
    }
    
    private static Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>200) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����300kb,���ڼ���ѹ��         
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
            options -= 10;//ÿ�ζ�����10  
            if(options<0){
            	break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
        return bitmap;  
    }  
	
	private static Bitmap compressImageFromFile(String srcPath, int displayWidth, int displayHeight) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        newOpts.inJustDecodeBounds = true;//ֻ����,��������  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        float hh = displayHeight;//  
        float ww = displayWidth;//  
        
        int be = 1;  
        if (w > h && w > ww) {  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//���ò�����  
          
        newOpts.inPreferredConfig = Config.ARGB_8888;//��ģʽ��Ĭ�ϵ�,�ɲ���  
        newOpts.inPurgeable = true;// ͬʱ���òŻ���Ч  
        newOpts.inInputShareable = true;//����ϵͳ�ڴ治��ʱ��ͼƬ�Զ�������  
          
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return bitmap;  
    }  
	
	public static String getNewPath(String path, int angle, int screenWidth, int screenHeigh , String targetId) throws IOException{
		File mFile=new File(path);
        //�����ļ�����
        if (mFile.exists()) {
        	long size = 0;
  		    FileInputStream fis = null;
  		    fis = new FileInputStream(mFile);
  		    size = fis.available();
  		    fis.close();
  		    int sizeKb = (int) (size/(1024));
  		    int quality = 90;
  		    CustomLog.e("sizeKb:"+sizeKb);
  		    if(sizeKb>6154){
		    	quality=10;
		    }else if(sizeKb>3072){
  		    	quality=30;
  		    }else if(sizeKb>1024){
  		    	quality=40;
  		    }else if(sizeKb>512){
  		    	quality=70;
  		    }
            Bitmap bitmap = compressImageFromFile(path, screenWidth, screenHeigh);
            if(bitmap==null){
            	return null;
            }
            bitmap = adjustPhotoRotation(bitmap, angle);
            //saveImageFile(bitmap, path.substring(path.lastIndexOf("/")+1));
            if(!"".equals(targetId)){
            	targetId = targetId+"/";
            	File image_file = new File("/sdcard/yunzhixun/image/"+targetId);
        		if (!image_file.exists()) {
        			image_file.mkdirs();
        		}
            }
            File jpegFalseFile = new File("/sdcard/yunzhixun/image/", targetId+path.substring(path.lastIndexOf("/")+1));
            IMManager.getInstance(null).compressBitmap(bitmap, quality, jpegFalseFile.getAbsolutePath());
            path="/sdcard/yunzhixun/image/"+targetId+path.substring(path.lastIndexOf("/")+1);
        }
		return path;
	}
	
    private static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
    {
         Matrix m = new Matrix();
         m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

         try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
               return bm1;
            } catch (OutOfMemoryError ex) {
         }
         return null;
    }
    //������Ļ��ߣ�ƥ����ʵ�ͼƬ���
    private static Bitmap fillterBimtap(Context context,Bitmap srcBitmap,int screenWidth, int screenHeigh){
    	//�����ʾ��
    	int maxWidth = (int) (screenWidth * 0.372);
    	//�����ʾ��
    	int maxHeight = (int) (screenHeigh * 0.21);
    	//��С��ʾ��
    	int minWidth = (int) (screenWidth * 0.122);
    	//��С��ʾ��
    	int minHeight = (int) (screenHeigh * 0.08);
    	
    	int width = srcBitmap.getWidth();
    	int height = srcBitmap.getHeight();
    	
    	if(width > 240){
    		width = maxWidth;
    	}else if(width > 150){
    		//������ȵĵȱ�������
    		width = (int) ((float)width / 200 * (screenWidth * 0.372));
    	}else if(width > 100){
    		width = (int) (((float)width / 150) * (screenWidth * 0.312));
    	}else if(width > 80){
    		width = (int) (((float)width / 100) * (screenWidth * 0.252));
    	}else{
    		width = minWidth;
    	}
    	//���������ŵõ��ĸ߶�
    	height = (int) (width*srcBitmap.getHeight()/srcBitmap.getWidth());
    	Log.i(TAG, "bitmap width = "+width +"��height = "+height);
    	if(height > maxHeight){
    		height = maxHeight;
    	}else if(height < minHeight){
    		height = minHeight;
    	}
    	return Bitmap.createScaledBitmap(srcBitmap, width, height,true);
    }
    
  //������Ļ��ߣ�ƥ����ʵ�ͼƬ���
    private static Bitmap fillterBimtapBySelf(Context context,Bitmap srcBitmap,int screenWidth, int screenHeigh){
    	//�����ʾ��
    	int maxWidth = (int) (screenWidth * 0.372);
    	//�����ʾ��
    	int maxHeight = (int) (screenHeigh * 0.21);
    	//��С��ʾ��
    	int minWidth = (int) (screenWidth * 0.122);
    	//��С��ʾ��
    	int minHeight = (int) (screenHeigh * 0.08);
    	
    	int width = srcBitmap.getWidth();
    	int height = srcBitmap.getHeight();
    	Log.i(TAG, "src bitmap width = "+width +"��height = "+height);
    	if(width > 1500){
    		width = maxWidth;
    	}else if(width > 1200){
    		//������ȵĵȱ�������
    		width = (int) ((float)width / 1200 * (screenWidth * 0.342));
    	}else if(width > 900){
    		width = (int) (((float)width / 900) * (screenWidth * 0.322));
    	}else if(width > 600){
    		width = (int) (((float)width / 600) * (screenWidth * 0.302));
    	}else if(width > 300){
    		width = (int) (((float)width / 300) * (screenWidth * 0.282));
    	}else if(width > 100){
    		width = (int) (((float)width / 200) * (screenWidth * 0.262));
    	}else if(width > 50){
    		width = (int) (((float)width / 100) * (screenWidth * 0.222));
    	}else{
    		width = minWidth;
    	}
    	//���������ŵõ��ĸ߶�
    	height = (int) (width*srcBitmap.getHeight()/srcBitmap.getWidth());
    	if(height > maxHeight){
    		height = maxHeight;
    		width = srcBitmap.getWidth() * height / srcBitmap.getHeight();
    		if(width > maxWidth){
    			width = maxWidth;
    		}else if(width < minWidth){
    			width = minWidth;
    		}
    	}else if(height < minHeight){
    		height = minHeight;
    	}
    	Log.i(TAG, "dest bitmap width = "+width +"��height = "+height);
    	return Bitmap.createScaledBitmap(srcBitmap, width, height,true);
    }
    
    /**
     * ��ȡͼƬ����
     * @param ͼƬ·��
     * @return
     */
    public static int readImageDegree(String path){
    	int degree = 0;
    	try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return degree;
    }
    /**
    * @Description ����ͼƬ������
    * @param src
    * @param targetId
    * @date 2015-12-14 ����9:00:13 
    * @author zhuqian  
    * @return String
     */
    public static String saveLocation(Bitmap src,String targetId){
    	String yzxDir = FileTools.getSdCardDir();
    	String srcPath = "";
    	File locationDir = new File(yzxDir+"/location");
    	if(!locationDir.exists()){
    		locationDir.mkdirs();
    	}
    	File targetDir = new File(locationDir.getAbsolutePath()+"/"+targetId);
    	if(!targetDir.exists()){
    		targetDir.mkdirs();
    	}
    	String srcName = Md5FileNameGenerator.generate(UUID.randomUUID().toString().substring(0, 32)+"_"+src.hashCode());
    	srcPath = targetDir.getAbsolutePath()+"/"+srcName+".png";
    	try {
    		//��ѹ��
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		Bitmap locationBitmap = null;
    		src.compress(Bitmap.CompressFormat.PNG, 100, bos);
    		//������ʾͼƬ����������
			BitmapRegionDecoder bitmapRegionDecoder;
			bitmapRegionDecoder = BitmapRegionDecoder.newInstance(bos.toByteArray(),0,bos.toByteArray().length, false);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			locationBitmap = bitmapRegionDecoder.decodeRegion(new Rect(src.getWidth() / 6, src.getHeight() / 4, src.getWidth() * 5 / 6, src.getHeight() * 3 / 4), options);
			bos.reset();
			locationBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
    		int quality = 90;
    		int sizeKb = bos.toByteArray().length / 1024;
    		Log.i(TAG,"sizeKb = "+sizeKb);
    		if(sizeKb>1024){
		    	quality=5;
		    }else if(sizeKb > 512){
  		    	quality=10;
  		    }else if(sizeKb > 256){
  		    	quality=15;
  		    }else if(sizeKb > 128){
  		    	quality=20;
  		    }else if(sizeKb > 64){
  		    	quality=30;
  		    }
    		IMManager.compressBitmap(locationBitmap, quality, srcPath);
    		bos.reset();
			return srcPath;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }
    public static void loadChatLocationBitmap(Context context,final String url,final IMChatImageView imageView, 
			int screenWidth, int screenHeigh){
    	final YZXImageLoader yzxImageLoader = YZXImageLoader.newInstance(context);
    	if (TextUtils.isEmpty(url)) {
    		if(imageView!=null){
    			//ѡ������ʵ����ű���
                clipLocationBitmap(context,"",screenWidth,screenHeigh,new OnBitmapClipListener() {
    				@Override
    				public void onClip(Bitmap bitmap) {
    					if(null != bitmap){
    						yzxImageLoader.put(url, bitmap);
    						if(imageView!=null){
    							imageView.setImageBitmap(bitmap);
    						}
    					}
    				}
    			});
    		}
    		return;
        }
    	Bitmap bitmap = yzxImageLoader.get(url);
    	
        if (bitmap != null) {
        	if(imageView!=null){
        		imageView.setImageBitmap(bitmap);
        		return;
        	}
        }else{
        	File mFile=new File(url);
            if(!mFile.exists()){
            	if(imageView!=null){
            		//ѡ������ʵ����ű���
                    clipLocationBitmap(context,"",screenWidth,screenHeigh,new OnBitmapClipListener() {
        				@Override
        				public void onClip(Bitmap bitmap) {
        					if(null != bitmap){
        						yzxImageLoader.put(url, bitmap);
        						if(imageView!=null){
        							imageView.setImageBitmap(bitmap);
        						}
        					}
        				}
        			});
        		}
        		return;
            }
            //ѡ������ʵ����ű���
            clipLocationBitmap(context,url,screenWidth,screenHeigh,new OnBitmapClipListener() {
				@Override
				public void onClip(Bitmap bitmap) {
					if(null != bitmap){
						yzxImageLoader.put(url, bitmap);
						if(imageView!=null){
							imageView.setImageBitmap(bitmap);
						}
					}
				}
			});
        }
    }
    /**
    * @Description ��λ��ͼƬ�м�Ϊ���ģ���ȡͼƬ
    * @param srcPath	ͼƬ·��
    * @date 2015-12-8 ����10:56:09 
    * @author zhuqian  
    * @return void
     */
    private static void clipLocationBitmap(Context context,String path,int screenWidth, int screenHeigh,OnBitmapClipListener listener){
    	//�Ƚ�ȡͼƬ���ٽ��п�ߵ�����
    	//���ͼƬ�Ŀ���
    	File locationFile = new File(path);
    	Bitmap locationBitmap = null;
    	if(!locationFile.exists()){
    		CustomLog.d("Ҫ���ص�ͼƬλ�ò�����");
    		locationBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img, null);
    	}else{
			try {
				BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
				tmpOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, tmpOptions);
				int width = tmpOptions.outWidth;
				int height = tmpOptions.outHeight;
				//������ʾͼƬ����������
				BitmapRegionDecoder bitmapRegionDecoder;
				bitmapRegionDecoder = BitmapRegionDecoder.newInstance(path, false);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				locationBitmap = bitmapRegionDecoder.decodeRegion(new Rect(0,0, width, height), options);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	if(locationBitmap != null){
    		//λ��ͼƬ��СΪ��*0.625����*0.228
    		int width = (int) (0.625 * screenWidth);
    		int height = (int) (0.228 * screenHeigh);
    		if(listener != null){
    			listener.onClip(Bitmap.createScaledBitmap(locationBitmap, width, height,true));
    		}
    		if(!locationBitmap.isRecycled()){
    			locationBitmap.recycle();
    			locationBitmap = null;
    		}
    	}else{
    		if(listener != null){
    			listener.onClip(null);
    		}
    	}
    }
    /**
    * @Description ��View�л�ȡͼƬ
    * @param view
    * @date 2015-12-10 ����4:59:39 
    * @author zhuqian  
    * @return Bitmap
     */
    public static Bitmap getBitmapFromView(View view){
    	view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }
    /**
    * @Description �ϳ�λ��ͼƬ
    * @param srcBitmap
    * @date 2015-12-11 ����6:43:44 
    * @author zhuqian  
    * @return Bitmap
     */
    public static Bitmap getThumbnailLoctionBitmap(Context context,Bitmap srcBitmap){
    	Bitmap desBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), srcBitmap.getConfig());
    	Canvas canvas = new Canvas(desBitmap);
    	canvas.drawBitmap(srcBitmap, new Matrix(), null);  
    	if(pointBitmap == null){
    		BitmapTools.pointBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_local);
    	}
    	
    	int x = (srcBitmap.getWidth() - pointBitmap.getWidth()) / 2;
    	int y = srcBitmap.getHeight() / 2 - pointBitmap.getHeight();
    	
    	int xOffset = DensityUtil.dip2px(context, 8);
    	x += xOffset;
    	canvas.drawBitmap(pointBitmap, x, y, null);  //120��350Ϊbitmap2д����x��y���� 
    	
    	if(srcBitmap != null && !srcBitmap.isRecycled()){
    		Log.i(TAG, "recycled srcBitmap");
    		srcBitmap.recycle();
    		srcBitmap = null;
    	}
    	return desBitmap;
    }
    /**
    * @Description �ӵ�ǰActivity��ȡͼƬ
    * @return	ͼƬ
    * @date 2015-12-11 ����6:02:28 
    * @author zhuqian  
    * @return Bitmap
     */
    public static Bitmap getBitmapFromActivity(Activity activity){
    	 activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
    	 Bitmap bmp= activity.getWindow().getDecorView().getDrawingCache();
    	 return bmp;
    }
    /**
    * @Title BitmapIsPutInCacheListener 
    * @Description ��Ҫ�����յ�ͼƬ����
    * @Company yunzhixun
    * @author zhuqian
    * @date 2015-12-7 ����3:13:10
     */
    public interface BitmapIsPutInCacheListener{
    	void putInCache(Bitmap recycleBitmap);
    }
    /**
    * @Title BitmapClipListener 
    * @Description ͼƬ��ȡ�ص�
    * @Company yunzhixun
    * @author zhuqian
    * @date 2015-12-8 ����11:41:15
     */
    public interface OnBitmapClipListener{
    	void onClip(Bitmap bitmap);
    }
}
