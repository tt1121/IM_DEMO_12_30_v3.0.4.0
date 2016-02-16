package com.yzx.im_demo;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.yzx.im_demo.userdata.LoginHandler;
import com.yzx.mydefineview.MyToast;
import com.yzx.mydefineview.YzxTopBar;
import com.yzx.tools.RestTools;
import com.yzxtcp.tools.StringUtils;

public class IMRegisterV2Activity extends Activity {
	private ImageButton ib_back;
	private Button btn_register;
	private Handler mHandler;
	private EditText mEditText;
	private String phoneNum;
	private String nickName;
	private YzxTopBar yzxTopBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_v2);
		
		yzxTopBar = (YzxTopBar)findViewById(R.id.topbar_register);
	
		phoneNum = getIntent().getStringExtra("phoneNum");
		
		mEditText = (EditText)findViewById(R.id.edt_nick);
		yzxTopBar.setBackBtnOnclickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				IMRegisterV2Activity.this.finish();
			}
		});
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (NetWorkTools.isNetWorkConnect(IMRegisterV2Activity.this) == false) {
//					MyToast.showLoginToast(IMRegisterV2Activity.this,
//							"�������Ӳ����ã�������");
//					return;
//				}
				nickName = mEditText.getText().toString();

				if (StringUtils.isEmpty(nickName)) {
					MyToast.showLoginToast(IMRegisterV2Activity.this, "�ǳƲ���Ϊ��");
					return;
				}
				if(!isLegal(nickName)){
					MyToast.showLoginToast(IMRegisterV2Activity.this, "������ǳƲ��Ϸ�");
					return;
				}
				try {
					nickName = new String(nickName.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(RestTools.LOGIN_REST_STARTREGISTER);
				btn_register.setClickable(false);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						RestTools.accountRegister(IMRegisterV2Activity.this,phoneNum, nickName, mHandler);
					}
				}, 100);
				
				
			}
		});

		mHandler = new LoginHandler(this) {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case RestTools.LOGIN_STATUS_FAIL:
				case RestTools.LOGIN_REST_REGISTER_FAIL:
					this.closeProgressDialog();
					btn_register.setClickable(true);
					break;
				case RestTools.LOGIN_REST_STARTREGISTER:
					showProgressDialog();
					startLoginTimer();
					break;
				case RestTools.LOGIN_REST_FINISH:
					System.out.println("reg LOGIN_REST_FINISH");
					IMRegisterV2Activity.this.finish();
					break;
				case RestTools.LOGIN_STATUS_TIMEOUT:
					btn_register.setClickable(true);
					break;
				}
				
				super.handleMessage(msg);

			}
		};
	}
	/**
	* @Description �жϴ�����ַ����Ƿ�Ϸ�(����+����+��ĸ���������������)
	* @param text
	* @return	True:�Ϸ���false:���Ϸ�
	* @date 2015-12-16 ����4:17:04 
	* @author zhuqian  
	* @return boolean
	 */
	private boolean isLegal(String text){
		//(����+��ĸ)����(����+_)�����������ƥ��һ��������£�ƥ������
		Pattern pattern = Pattern.compile("^([a-zA-Z_0-9]{0,1}[\u4e00-\u9fa5]{0,1}[_]{0,1})+$");
		Matcher matcher = pattern.matcher(text);
		return matcher.matches();
	}

}
