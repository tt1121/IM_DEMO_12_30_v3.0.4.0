package com.yzx.tools;

import com.yzxtcp.tools.CustomLog;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;


/**
 * ContentResolver������
 * @author zhuqian
 *
 */
public class ContentResolverUtils {
	
	private static final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {  
		CustomLog.i("ContentResolverUtils select image uri : "+uri.toString());
	    // DocumentProvider  
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {  
	        // ExternalStorageProvider  
	        if (isExternalStorageDocument(uri)) {  
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            if ("primary".equalsIgnoreCase(type)) {  
	                return Environment.getExternalStorageDirectory() + "/" + split[1];  
	            }  
	  
	            // TODO handle non-primary volumes  
	        }  
	        // DownloadsProvider  
	        else if (isDownloadsDocument(uri)) {  
	  
	            final String id = DocumentsContract.getDocumentId(uri);  
	            final Uri contentUri = ContentUris.withAppendedId(  
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
	  
	            return getDataColumn(context, contentUri, null, null);  
	        }  
	        // MediaProvider  
	        else if (isMediaDocument(uri)) {  
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            Uri contentUri = null;  
	            if ("image".equals(type)) {  
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("video".equals(type)) {  
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("audio".equals(type)) {  
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
	            }  
	  
	            final String selection = "_id=?";  
	            final String[] selectionArgs = new String[] {  
	                    split[1]  
	            };  
	  
	            return getDataColumn(context, contentUri, selection, selectionArgs);  
	        }  
	    }  
	    // MediaStore (and general)  
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {  
	  
	        // Return the remote address  
	        if (isGooglePhotosUri(uri))  
	            return uri.getLastPathSegment();  
	  
	        return getDataColumn(context, uri, null, null);  
	    }  
	    // File  
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {  
	        return uri.getPath();  
	    }  
	  
	    return null;  
	}  
	
	
	public static String getDataColumn(Context context, Uri uri, String selection,  
	        String[] selectionArgs) {  
	  
	    Cursor cursor = null;  
	    final String column = "_data";  
	    final String[] projection = {  
	            column  
	    };  
	  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,  
	                null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            final int index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(index);  
	        }  
	    } finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {  
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	}  
	/**
	 * ��ȡ���µ�ͼƬ
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getImagPath(Context context){
		//ֻ��ѯjpeg��png��ͼƬ  ,���µ�ͼƬ
		 Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
         ContentResolver mContentResolver = context.getContentResolver();  

         //ֻ��ѯjpeg��png��ͼƬ  
         Cursor mCursor = mContentResolver.query(mImageUri, null,  
                 MediaStore.Images.Media.MIME_TYPE + "=? or "  
                         + MediaStore.Images.Media.MIME_TYPE + "=?",  
                 new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED+" desc");  
           
         if(mCursor == null){  
             return "";
         }  
         String filePath = "";
		if(mCursor != null && mCursor.moveToFirst()){
			if (mCursor.getColumnIndex(MediaStore.Images.Media.DATA) > -1) {
				int column_index = mCursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				filePath = mCursor.getString(column_index);
			}
			mCursor.close();
		}
		return filePath;
	}
	
}