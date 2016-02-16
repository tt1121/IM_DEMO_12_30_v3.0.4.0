package com.yzx.im_demo.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yzx.db.UserInfoDBManager;
import com.yzx.db.domain.UserInfo;
import com.yzx.im_demo.IMLoginV2Activity;
import com.yzx.im_demo.R;
import com.yzxtcp.UCSManager;

public class SettingFragment extends Fragment implements OnClickListener{
	private String mLocalUser;
	private View mView;

	private LinearLayout mUserInfo, mChangeAccount;
	private TextView mQuictAccount,mTvUser, mTvLocalUser;
	
	private String  mUserName;
	
	
	public SettingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.fragment_setting, container, false);

		mUserInfo = (LinearLayout) mView.findViewById(R.id.id_userinfo);
		mChangeAccount = (LinearLayout) mView
				.findViewById(R.id.id_textview_changeclient);
		mQuictAccount = (TextView) mView.findViewById(R.id.id_textView_quite);
		mTvUser = (TextView) mView.findViewById(R.id.id_textview_user);
		mTvLocalUser = (TextView) mView.findViewById(R.id.id_textView_localUser);
		
		mUserInfo.setOnClickListener(this);
		
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	private void initData() {
		mUserInfo.setOnClickListener(this);
		mChangeAccount.setOnClickListener(this);
		mQuictAccount.setOnClickListener(this);
		
		mUserName = UserInfoDBManager.getInstance().getCurrentLoginUser().getName();
		mLocalUser = UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount();
		if(mUserName != null){
			mTvUser.setText(mUserName);
		}else{
			mTvUser.setText("");
		}
		if(mLocalUser != null){
			mTvLocalUser.setText(mLocalUser);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.id_userinfo:
			System.out.println("id_userinfo");
			break;
		case R.id.id_textView_quite:
			final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create(); 
			dialog.show();
			dialog.getWindow().setContentView(R.layout.dialog_base);
			TextView tv= (TextView) dialog.getWindow().findViewById(R.id.dialog_tv);
			tv.setText("是否退出当前账号");
			dialog.getWindow()
				.findViewById(R.id.dialog_tv_cencel)
				.setOnClickListener(new View.OnClickListener() {  
                    @Override  
                    public void onClick(View v) {  
                    	dialog.dismiss();  
                    }
				});
			dialog.findViewById(R.id.dialog_tv_sure)
				.setOnClickListener(new View.OnClickListener() {  
                    @Override  
                    public void onClick(View v) {  
                    	dialog.dismiss();
                    	UCSManager.disconnect();
                    	Intent intent = new Intent(getActivity(), IMLoginV2Activity.class);
                    	startActivity(intent);
                    	getActivity().finish();
                    }
				});
  
			break;
		}
	}

}
