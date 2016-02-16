package com.yzx.im_demo;

import java.io.File;
import java.util.List;
import com.yzx.mydefineview.YZXProgressBar;
import com.yzx.tools.BitmapTools;
import com.yzx.tools.BitmapTools.BitmapIsPutInCacheListener;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.listener.MessageListener;
import com.yzxIM.tools.DownloadThread;
import com.yzxtcp.tools.CustomLog;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class IMImageActivity extends Activity implements MessageListener, BitmapIsPutInCacheListener {

	protected static final String TAG = "IMImageActivity";
	private ImageView imimage;
	private PhotoViewAttacher mAttacher;
	private YZXProgressBar yzx_progressBar;
	private String msgid;
	private String msgParentID;
	private String imagepath;
	private String new_path;
	private String imagedownpath;
	private int progress = 0;
	//ҳ����ʧʱ��Ҫ�����յ�ͼƬ
	private Bitmap recycleBitmap = null;

	@Override
	protected void onResume() {
		super.onResume();
	}

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_imimage);

		imimage = (ImageView) findViewById(R.id.imimage);
		yzx_progressBar = (YZXProgressBar) findViewById(R.id.yzx_progressBar);
		mAttacher = new PhotoViewAttacher(imimage);

		imagepath = getIntent().getStringExtra("imagepath");
		msgParentID = getIntent().getStringExtra("msgParentID");
		imagedownpath = getIntent().getStringExtra("imagedownpath");
		msgid = getIntent().getStringExtra("msgid");
		// �ȴ�����Ŀ¼
		File new_dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/yunzhixun/image/" + msgParentID);
		if (!new_dir.exists()) {
			new_dir.mkdirs();
		}
		new_path = new_dir.getAbsolutePath() + "/yzx_image_downnewpath_"
				+ msgid + ".png";
		
		Log.i(TAG, "imagepath = "+imagepath);
		Log.i(TAG, "imagedownpath = "+imagedownpath);

		File mFile = new File(imagepath);
		File mFile1 = new File(new_path);
		// �����ļ����ڣ�����û����������
		Thread t = MainApplication.getInstance().getThread(msgid);
		if (mFile1.exists() && mFile1.length() > 0) {
			if (t != null && t.isAlive()) {
				Log.i(TAG, "�������أ��Ѿ�д���ļ����滻����");
				((DownloadThread) t).replaceListener(this);
				BitmapTools.loadImageBitmapNoCompress(this, imagepath, imimage,this);
			} else {
				BitmapTools.loadImageBitmapNoCompress(this, new_path, imimage,this);
			}
		} else if (mFile.exists()) {
			Log.i(TAG, "imagepath = " + imagepath);
			if (!TextUtils.isEmpty(imagedownpath)) {
				// �鿴����ͼƬ
				BitmapTools.loadImageBitmapNoCompress(this, imagepath, imimage,this);
			} else {
				// �鿴����ͼƬ
				BitmapTools.loadImageBitmapFromMySelf(this, imagepath, imimage,this);
			}
			if (t != null && t.isAlive()) {
				Log.i(TAG, "�������أ���δд���ļ����滻����");
				((DownloadThread) t).replaceListener(this);
			} else {
				if (!"".equals(imagedownpath)
						&& !imagepath.contains("downnewpath")) {
					yzx_progressBar.setVisibility(View.VISIBLE);
					yzx_progressBar.setProcess(progress);
					downloadThread = IMManager.getInstance(
							getApplicationContext()).downloadAttached(
							imagedownpath, new_path, msgid, this);
				}
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Log.i(TAG, "�������");
				MainApplication.getInstance().removeThread(msgid);
				progress = 0;
				yzx_progressBar.setProcess(100);
				yzx_progressBar.setVisibility(View.GONE);
				File mFile = new File(new_path);
				if (mAttacher != null) {
					mAttacher = null;
				}
				// ͼƬ�ֱ��ʸı䣬��ֹ����
				mAttacher = new PhotoViewAttacher(imimage);
				Log.i(TAG, "new_path =" + new_path);
				if (mFile.exists()) {
					BitmapTools.loadImageBitmapNoCompress(IMImageActivity.this,
							new_path, imimage,IMImageActivity.this);
				}
				break;
			case 2:
				int curProgress = (Integer) msg.obj;
				yzx_progressBar.setVisibility(View.VISIBLE);
				yzx_progressBar.setProcess(curProgress);
				break;
			case 4:
				// ����ʧ��
				CustomLog.i("�����쳣");
				MainApplication.getInstance().removeThread(msgid);
				yzx_progressBar.setProcess(0);
				yzx_progressBar.setVisibility(View.GONE);
				Toast.makeText(IMImageActivity.this, "����ʧ��", 0).show();
				if (!TextUtils.isEmpty(new_path)) {
					File downFile = new File(new_path);
					boolean isDelete = downFile.delete();
					if (downFile.exists() && isDelete) {
						CustomLog.i("����ļ� -----" + new_path + "�ɹ�");
					}
					if (!isDelete) {
						CustomLog.i("����ļ� -----" + new_path + "ʧ��");
					}
				}
				break;
			}
		}
	};
	private DownloadThread downloadThread;

	@Override
	public void onDownloadAttachedProgress(String msgId, final String filePaht,
			final int sizeProgrss, final int currentProgress) {
		if (msgId.equals(msgid) && currentProgress >= sizeProgrss
				&& sizeProgrss != 0) {
			// �������
			mHandler.sendEmptyMessage(1);
		} else {
			if (currentProgress == 0 && sizeProgrss == 0) {
				// ����ʧ��
				mHandler.sendEmptyMessage(4);
				return;
			}
			// ������
			progress = (100 * currentProgress) / sizeProgrss;
			Message msg = mHandler.obtainMessage();
			msg.obj = progress;
			msg.what = 2;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void onReceiveMessage(List arg0) {

	}

	@Override
	public void onSendMsgRespone(ChatMessage arg0) {
	}

	protected void onDestroy() {
		super.onDestroy();
		// �����˳�����û�������
		if (downloadThread != null && downloadThread.isAlive()) {
			MainApplication.getInstance().putThread(msgid, downloadThread);
		}
		//���ٷ��뻺��ʧ�ܵĶ���
		if(this.recycleBitmap != null && !this.recycleBitmap.isRecycled()){
			CustomLog.d("IMImageActivity Destroy recycleBitmap");
			this.recycleBitmap.recycle();
			this.recycleBitmap = null;
			System.gc();
		}
	}
	
	@Override
	public void putInCache(Bitmap recycleBitmap) {
		if(recycleBitmap != null){
			this.recycleBitmap = recycleBitmap;
		}
	}
}
