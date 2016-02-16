package com.yzx.im_demo;

import com.yzx.im_demo.R;
import com.yzx.mydefineview.YZXDialog;
import com.yzx.mydefineview.YZXDialog.OnAlertViewClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
* @Title IMLoginOutActivity 
* @Description ��������Activity
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-18 ����9:32:09 
*/
public class IMLoginOutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginout);
		YZXDialog.showAlertView(this, 0, "��������", "�����˺��Ѿ��ڱ�ĵط���¼��", "��֪����", null, new OnAlertViewClickListener() {
			public void confirm(String result) {
				//nothing to do
			}
			public void cancel() {
				//��ת����¼ҳ��
				startActivity(new Intent(IMLoginOutActivity.this,
						IMLoginV2Activity.class));
				finish();
			}
		});
	}

}
