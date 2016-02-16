package com.yzx.im_demo.userdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yzx.db.UserInfoDBManager;
import com.yzx.db.domain.UserInfo;
import com.yzx.im_demo.IMChatActivity;
import com.yzx.mydefineview.MyToast;
import com.yzx.tools.RestTools;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

public class LoginHandler extends Handler implements ILoginListener {
	private static final String TAG = "LoginHandler";
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private Timer mTimer;
	private String mAccount;
	private static List<Handler> handlers = new ArrayList<Handler>();
	private String token;
	
	public LoginHandler(Context context) {
		mContext = context;
		handlers.add(this);
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Bundle data = null;
		switch (msg.what) {
		case RestTools.LOGIN_STATUS_INPROCESS:
			
			break;
		case RestTools.LOGIN_STATUS_SUCCESS:
			stopLoginTimer();
			sendFinishMsg();
//			((Activity) mContext).finish();
			break;
		case RestTools.LOGIN_STATUS_FAIL:
			//�Ƴ���¼�����ص�
			stopLoginTimer();
			MyToast.showLoginToast(mContext, "��¼ʧ��");
			closeProgressDialog();
			break;
		case RestTools.LOGIN_STATUS_TIMEOUT:
			MyToast.showLoginToast(mContext, "��¼��ʱ");
			closeProgressDialog();
			break;
		case RestTools.LOGIN_REST_TOKEN_FAIL:
			stopLoginTimer();
			MyToast.showLoginToast(mContext, "��ȡTokenʧ��");
			closeProgressDialog();
			break;

		case RestTools.LOGIN_REST_TOKEN_OK:
//			showProgressDialog();
			data = msg.getData();
			token = data.getString("imtoken");
			Log.i(TAG, "��ȡtoken�ɹ�����ʼ��¼");
			UCSManager.connect(token, this);
			break;
		case RestTools.LOGIN_REST_HAS_REGISTER:
			break;
		case RestTools.LOGIN_REST_REGISTER_FAIL:
			stopLoginTimer();
			MyToast.showLoginToast(mContext, "ע��ʧ��");
			break;
		}
	}

	public void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
		}

		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("��¼��...");
		mProgressDialog.show();
	}

	public void startLoginTimer(){
		if (mTimer == null) {
			mTimer = new Timer();
		}
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mProgressDialog.dismiss();
				mTimer = null;
				sendEmptyMessage(RestTools.LOGIN_STATUS_TIMEOUT);
			}
		}, 20000);
	}
	
	public void stopLoginTimer(){
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	public void closeProgressDialog(){
		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}
	
	private void sendFinishMsg(){
		for (Handler h : handlers) {
			h.sendEmptyMessage(RestTools.LOGIN_REST_FINISH);
		}
	}
	
	@Override
	public void onLogin(UcsReason reason) {
		if (null != mProgressDialog) {
			mProgressDialog.dismiss();
		}
		if (reason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK) {
			Log.i(TAG, "��¼�ɹ�");
			UserInfo user = new UserInfo(RestTools.mPhoneNum, RestTools.mNickName, 1, 1);
			//���浽���ݿ�
			UserInfoDBManager.getInstance().insert(user,token);
			RestTools.queryGroupInfo(mContext,user.getAccount(),null);
			sendEmptyMessageDelayed(RestTools.LOGIN_STATUS_SUCCESS, 1000);
			Intent intent = new Intent(mContext, IMChatActivity.class);
			intent.putExtra("mLocalUser", user.getAccount());
			intent.putExtra("connectTcp", false);
			mContext.startActivity(intent);
			
			//�Ƴ���¼�����ص�
			UCSManager.removeLoginListener(this);
		} else{
			Log.i(TAG, "��¼ʧ��");
			sendEmptyMessage(RestTools.LOGIN_STATUS_FAIL);
		}
	}
}
