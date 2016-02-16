package com.yzx.im_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.yzx.db.UserInfoDBManager;
import com.yzx.db.domain.UserInfo;
import com.yzx.im_demo.userdata.LoginHandler;
import com.yzx.mydefineview.MyToast;
import com.yzx.tools.NetWorkTools;
import com.yzx.tools.RestTools;
import com.yzx.tools.StringUtils;

public class IMLoginV2Activity extends Activity {

	private static final String TAG = "IMLoginV2Activity";
	private EditText edt_account;
	private Button btn_login;
	private ImageView address_setting;
	private Handler mHandler;
	private String mAccount;
	//是否主动退出
	private boolean isBack;
	
	private UserInfo user;

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "IMLoginV2Activity onNewIntent()");
		isBack = false;
		initView(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "IMLoginV2Activity onCreate()");
		setContentView(R.layout.activity_login_v2);
		initView(this);
		initData(this);
		isBack = false;
	}
	private void initData(IMLoginV2Activity imLoginActivity) {

		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (NetWorkTools.isNetWorkConnect(IMLoginV2Activity.this) == false) {
					MyToast.showLoginToast(IMLoginV2Activity.this,
							"网络连接不可用，请重试");
					return;
				}
				mAccount = edt_account.getText().toString();

				if (StringUtils.isEmpty(mAccount)) {
					MyToast.showLoginToast(IMLoginV2Activity.this, "手机号不能为空");
					return;
				}
				if (mAccount.length() != 11 || !StringUtils.isPhoneNumber(mAccount)) {
					MyToast.showLoginToast(IMLoginV2Activity.this, "请输入正确的手机号码");
					return;
				}
				mHandler.sendEmptyMessage(RestTools.LOGIN_REST_STARTGETTOKEN);
				btn_login.setClickable(false);
				getSharedPreferences("YZX_DEMO", 1).edit()
				.putString("YZX_ACCOUNT_INDEX", mAccount).commit();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						RestTools.getToken(IMLoginV2Activity.this,mAccount, mHandler);
					}
				}, 100);
			}
		});
		
		mHandler = new LoginHandler(this){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case RestTools.LOGIN_STATUS_FAIL:
//				case RestTools.LOGIN_REST_UNREGISTER:
					this.closeProgressDialog();
					btn_login.setClickable(true);
					break;
				case RestTools.LOGIN_REST_STARTGETTOKEN:
					this.showProgressDialog();
					//this.startLoginTimer();
					break;
				case RestTools.LOGIN_REST_UNREGISTER:
					this.closeProgressDialog();
					stopLoginTimer();
					btn_login.setClickable(true);
					Intent intent = new Intent(IMLoginV2Activity.this, IMRegisterV2Activity.class);
					intent.putExtra("phoneNum", mAccount);
					startActivity(intent);
					break;
				case RestTools.LOGIN_REST_FINISH:
					//IMLoginV2Activity.this.finish();
					break;
				case RestTools.LOGIN_STATUS_TIMEOUT:
					btn_login.setClickable(true);
					break;
				case RestTools.LOGIN_REST_TOKEN_FAIL:
					btn_login.setClickable(true);
					break;
				}
				super.handleMessage(msg);
			}
		};
		
		
	}

	private void initView(Context context) {
		//清空登录用户
		UserInfo oldUser = UserInfoDBManager.getInstance().getCurrentLoginUser();
		if(oldUser != null){
			oldUser.setLoginStatus(0);
			UserInfoDBManager.getInstance().update(oldUser);
		}
		user = UserInfoDBManager.getInstance().getDefaultLoginUser(false);
		edt_account = (EditText) findViewById(R.id.edt_account);
		btn_login = (Button) findViewById(R.id.btn_login);
		address_setting = (ImageView) findViewById(R.id.address_setting);
		btn_login.setClickable(true);
		if(user != null){
			mAccount = user.getAccount();
		}else{
			mAccount = "";
		}
		if(mAccount != null){
			edt_account.setText(mAccount);
			edt_account.setSelection(mAccount.length());
		}
		address_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(IMLoginV2Activity.this, AddressConfigActivity.class);
				//启动配置页面
				startActivity(intent);
			}
		});
	}
}
