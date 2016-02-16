package com.yzx.im_demo;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.yzx.db.OnDbChangeListener;
import com.yzx.db.UserInfoDBManager;
import com.yzx.db.UserSettingsDbManager;
import com.yzx.db.domain.UserInfo;
import com.yzx.db.domain.UserSetting;
import com.yzx.im_demo.contact.ContactFrament;
import com.yzx.im_demo.fragment.ConversationFragment;
import com.yzx.im_demo.fragment.ConversationFragment.refreshUnReadMessageListener;
import com.yzx.im_demo.fragment.SettingFragment;
import com.yzx.listener.IObserverListener;
import com.yzx.mydefineview.MainBottomBar;
import com.yzx.mydefineview.MyToast;
import com.yzx.mydefineview.YzxTopBar;
import com.yzx.tools.NotificationTools;
import com.yzx.tools.RestTools;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.listener.MessageListener;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;
import com.yzxtcp.listener.ISdkStatusListener;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.StringUtils;

@SuppressLint("NewApi")
public class IMChatActivity extends FragmentActivity implements
		OnPageChangeListener, OnClickListener, ISdkStatusListener,
		MessageListener, refreshUnReadMessageListener, ILoginListener, OnDbChangeListener {
	private ViewPager mViewPager;
	private List<Fragment> mTabs = new ArrayList<Fragment>();
	private FragmentPagerAdapter mAdapter;
	private String mLocalUser;
	private ImageButton mChatroom;
	private String[] actionTitle = new String[] { "��Ϣ", "ͨѶ¼", "����" };
	private IMManager imManager;
	private MainBottomBar bottomMsg, bottomContact, bottomSetting;
	
	private List<IObserverListener> observes = new ArrayList<IObserverListener>();
	
	private List<MainBottomBar> mTabIndicator = new ArrayList<MainBottomBar>();
	// private ActionBar mActionBar;
	private YzxTopBar mActionBar;

	private UserSetting userSetting;

	private ContactFrament mContactFrament;

	private TextView conversations_cout;
	private ImageView conversations_cout_bg;

	private static IMChatActivity mInstance;
	
	private static final int NOTIFAY_VOICE_VIBRATOR = 406;
	private static final String TAG = "IMChatActivity";

	
	private boolean isConnect;
	
	private ConversationFragment conversationFragment;
	private SettingFragment settingFragment;
	
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG,"IMChatActivity onNewIntent() taskId = "+getTaskId());
	}
	protected void onDestroy() {
		Log.i(TAG,"IMChatActivity onDestroy()");
		mHandler.removeCallbacks(null);
		UserSettingsDbManager.getInstance().removeObserver(this);
		//setNetErrorMsg();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG,"IMChatActivity onCreate() taskId = " + getTaskId());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imchat);
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		
		UserSettingsDbManager.getInstance().addObserver(this);
		
		mInstance = this;
		mLocalUser = getIntent().getStringExtra("mLocalUser");
		if (StringUtils.isEmpty(mLocalUser)) {
			CustomLog.d("IMChatActivity ��������");
			UserInfo user = UserInfoDBManager.getInstance().getCurrentLoginUser();
			if(user != null){
				mLocalUser = user.getAccount();
			}else{
				mLocalUser = "";
			}
			
		}
		
		imManager = IMManager.getInstance(getApplicationContext());
		initView();

		
		initDatas();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);

		imManager.setISdkStatusListener(this);
		imManager.setSendMsgListener(this);
		
		isConnect = getIntent().getBooleanExtra("connectTcp", false);
		if(isConnect || !UCSManager.isConnect()){
			if(!UCSManager.isConnect()){
				CustomLog.d("------------------����Kill-------------------");
			}
			//����ƽ̨
			new Thread(){
				public void run() {
					connect();
				}
			}.start();
		}else{
			if(!getIntent().hasExtra("connectTcp")){
				CustomLog.d("-------------���汻����connectTcp == null------------");
				//����ƽ̨
				new Thread(){
					public void run() {
						connect();
					}
				}.start();
			}
		}
	}
	public boolean isConnect() {
		return isConnect;
	}

	public static IMChatActivity getmInstance() {
		return mInstance;
	}

	private void initView() {
		bottomMsg = (MainBottomBar) findViewById(R.id.id_bottombar_msg);
		bottomContact = (MainBottomBar) findViewById(R.id.id_bottombar_contact);
		bottomSetting = (MainBottomBar) findViewById(R.id.id_bottombar_setting);
		bottomMsg.setOnClickListener(this);
		bottomContact.setOnClickListener(this);
		bottomSetting.setOnClickListener(this);

		bottomMsg.setBottomViewNormalAlpha(0.0f);

		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		mActionBar = (YzxTopBar) findViewById(R.id.actionBar_chat);
		mActionBar.setBackVisibility(View.GONE);
		mActionBar.setTitle(actionTitle[0]);
		conversations_cout = (TextView) findViewById(R.id.conversations_cout);
		conversations_cout_bg = (ImageView) findViewById(R.id.conversations_cout_bg);
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 101:
//				//��������
//				if(MainApplication.getInstance().getResumeActivity() instanceof IMMessageBgActivity){
//					//���IMMessageBgActivity����dialog
//					((IMMessageBgActivity)MainApplication.getInstance().getResumeActivity()).setHaveDialog(true);
//				}
				//�����ǳ�����
				Intent intent = new Intent();
				Uri uri = Uri.parse("yzx://"+getApplicationInfo().packageName).buildUpon().appendPath("login_out").build();
				Log.i(TAG, "�յ�������Ϣ�����ǳ�Activty uri = "+uri.toString());
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(intent);
				break;
			case 102:
				MyToast.showLoginToast(IMChatActivity.this, "token��ʱ,�����µ�½");
				break;
			case NOTIFAY_VOICE_VIBRATOR:
				//���������
				Log.i(TAG, "�յ���Ϣ���������");
				NotificationTools.showNotification(userSetting,(ChatMessage)msg.obj);
				break;
			default:
				break;
			}
		}
	};
	
	private void initDatas() {
		userSetting = UserSettingsDbManager.getInstance().getById(UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount());
		// ����ỰFragment
		conversationFragment = new ConversationFragment();
		// ����ͨѶ¼Fragment
		mContactFrament = new ContactFrament();
		// ��������Fragment
		settingFragment = new SettingFragment();
		mTabs.add(conversationFragment);
		
		Bundle args = new Bundle();
		args.putString("mLocalUser", mLocalUser);
		mContactFrament.setArguments(args);
		mTabs.add(mContactFrament);
		
		settingFragment.setArguments(args);
		mTabs.add(settingFragment);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mTabs.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mTabs.get(arg0);
			}
		};
		initTabIndicator();
		conversationFragment.setRefreshUnReadMessageListener(this);
		// RestTools.queryGroupInfo(LoginUserBean.getLocalUserId(getApplicationContext()),null);
	}

	private void initTabIndicator() {

		mTabIndicator.add(bottomMsg);
		mTabIndicator.add(bottomContact);
		mTabIndicator.add(bottomSetting);

		bottomMsg.setOnClickListener(this);
		bottomContact.setOnClickListener(this);
		bottomSetting.setOnClickListener(this);

	}
	protected void onResume() {
		super.onResume();
		MainApplication.getNotificationManager().cancelAll();
		checkUnReadMsg();
	}

	@Override
	public void onClick(View v) {
		resetOtherTabs();
		switch (v.getId()) {
		case R.id.id_bottombar_msg:
			mTabIndicator.get(0).setBottomViewNormalAlpha(0.0f);
			mViewPager.setCurrentItem(0, false);
			mActionBar.setTitle(actionTitle[0]);
			break;
		case R.id.id_bottombar_contact:
			mTabIndicator.get(1).setBottomViewNormalAlpha(0.0f);
			mViewPager.setCurrentItem(1, false);
			mActionBar.setTitle(actionTitle[1]);
			break;
		case R.id.id_bottombar_setting:
			mTabIndicator.get(2).setBottomViewNormalAlpha(0.0f);
			mViewPager.setCurrentItem(2, false);
			mActionBar.setTitle(actionTitle[2]);
			break;
		}

	}

	@Override
	public void onPageScrollStateChanged(int status) {
		if (status == 2) {
			mContactFrament.dismissSideBarDialog();
			mContactFrament.resumeSortList();
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

		if (positionOffset > 0) {
			MainBottomBar left = mTabIndicator.get(position);
			MainBottomBar right = mTabIndicator.get(position + 1);

			right.setBottomViewNormalAlpha(1 - positionOffset);
			left.setBottomViewNormalAlpha(positionOffset);
		}
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		mActionBar.setTitle(actionTitle[position]);
	}

	/**
	 * ����������Tab
	 */
	private void resetOtherTabs() {
		for (int i = 0; i < mTabIndicator.size(); i++) {
			mTabIndicator.get(i).setBottomViewNormalAlpha(1.0f);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			//moveTaskToBack(false);
			//��������
			Intent home = new Intent(Intent.ACTION_MAIN);  
			home.addCategory(Intent.CATEGORY_HOME);   
			startActivity(home);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSdkStatus(UcsReason reason) {
		Log.i(TAG, "onSdkStatus status = "+reason.getReason());
		CustomLog.e("onSdkStatus reason: " + reason.getReason() + "    "
				+ reason.getMsg());
		int status = 0;
		if (reason.getReason() == UcsErrorCode.NET_ERROR_KICKOUT) {// ������ǿ������֪ͨ
			CustomLog.i("�յ�������ǿ������֪ͨ");
			mHandler.sendEmptyMessage(101);
			//UCSManager.disconnect();
		} else if (reason.getReason() == UcsErrorCode.NET_ERROR_TOKENERROR) {
			CustomLog.i("token��ʱ,�����µ�¼");
			mHandler.sendEmptyMessage(102);
			//UCSManager.disconnect();
			startActivity(new Intent(IMChatActivity.this,
					IMLoginV2Activity.class));
			if(isConnect()){
        		finish();
        	}
		} else if(reason.getReason() == UcsErrorCode.NET_ERROR_TCPCONNECTOK){
			CustomLog.i("TCPCONNECTOK errorcode = " +reason.getReason());
			//((ConversationFragment)mTabs.get(0)).handSdkStatus(400);
			status = 400;
		} else if(reason.getReason() == UcsErrorCode.NET_ERROR_TCPCONNECTFAIL){
			CustomLog.i("TCPCONNECTFAIL errorcode = " +reason.getReason());
			//((ConversationFragment)mTabs.get(0)).handSdkStatus(408);
			status = 408;
		} else if(reason.getReason() == UcsErrorCode.NET_ERROR_TCPCONNECTING){
			CustomLog.i("TCPCONNECTING errorcode = " +reason.getReason());
			//((ConversationFragment)mTabs.get(0)).handSdkStatus(406);
			status = 406;
		} else if(reason.getReason() == UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT){
			CustomLog.i("NETUNCONNECT errorcode = " +reason.getReason());
			//((ConversationFragment)mTabs.get(0)).handSdkStatus(402);
			status = 402;
		} else if(reason.getReason() == UcsErrorCode.PUBLIC_ERROR_NETCONNECTED){
			CustomLog.i("NETCONNECTED errorcode = " +reason.getReason());
			//((ConversationFragment)mTabs.get(0)).handSdkStatus(406);
			if(UCSManager.isConnect()){
				status = 400;
			}else{
				status = 406;
			}
		}
		notifyObserver(status);
	}
	public void onSendMsgRespone(ChatMessage msg) {
		Log.i(TAG, "������Ϣ�� "+msg.getContent()+"����Ӧ�룺"+msg.getSendStatus());
	}

	@Override
	public void onReceiveMessage(List msgs) {
		List<ChatMessage> messages = (List<ChatMessage>) msgs;
		Log.i(TAG, "onReceiveMessage msg size : "+msgs.size());
		if(mHandler.hasMessages(NOTIFAY_VOICE_VIBRATOR)){
			mHandler.removeMessages(NOTIFAY_VOICE_VIBRATOR);
		}
		Message msg = mHandler.obtainMessage();
		msg.obj = messages.get(messages.size() - 1);
		msg.what = NOTIFAY_VOICE_VIBRATOR;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onDownloadAttachedProgress(String msgId, String filePaht,
			int sizeProgrss, int currentProgress) {

	}

	@Override
	public void onRefreshUnReadMessage() {
		checkUnReadMsg();
	}
	/**
	 * ���δ����Ϣ
	 */
	private void checkUnReadMsg() {
		int unreadcount = IMManager.getInstance(this).getUnreadCountAll();
		if (unreadcount != 0) {
			if (unreadcount > 99) {
				conversations_cout.setText("99+");
			} else {
				conversations_cout.setText(String.valueOf(unreadcount));
			}
			if (unreadcount < 10) {
				conversations_cout_bg.setVisibility(View.VISIBLE);
				conversations_cout_bg
						.setBackgroundResource(R.drawable.unreadsmall);
			} else {
				conversations_cout_bg.setVisibility(View.VISIBLE);
				conversations_cout_bg
						.setBackgroundResource(R.drawable.unreadbig);
			}
		} else {
			conversations_cout_bg.setVisibility(View.GONE);
			conversations_cout.setText("");
		}
	}

	public void onLogin(UcsReason reason) {
		//CustomLog.v("onLogin status errorCode = "+reason.getReason());
		Log.i(TAG, "onLogin status errorCode = "+reason.getReason());
		int sdkStatus = 0;
		if (reason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK){
			CustomLog.i("connect sdk successfully -----  enjoy --------");
			isConnect = false;
//			((ConversationFragment)mTabs.get(0)).handSdkStatus(400);
			sdkStatus = 400;
		}else if(reason.getReason() == UcsErrorCode.PUBLIC_ERROR_NETUNCONNECT){
//			((ConversationFragment)mTabs.get(0)).handSdkStatus(402);
			sdkStatus = 402;
		}else{
			((ConversationFragment)mTabs.get(0)).handSdkStatus(408);
			sdkStatus = 408;
		}
		notifyObserver(sdkStatus);
	}
	public void connect() {
		notifyObserver(406);
		new Thread(){
			public void run() {
				try {
					Thread.sleep(1500);
					String token = userSetting.getToken();
					RestTools.queryGroupInfo(IMChatActivity.this,UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount(),null);
					UCSManager.connect(token, IMChatActivity.this);
				} catch (InterruptedException e) {
				}
			};
		}.start();
	}
	public void putObserver(IObserverListener mObserver){
		synchronized (observes) {
			if(!observes.contains(mObserver)){
				boolean successful = observes.add(mObserver);
				if(successful){
					Log.d(TAG, "add mObserver successful hashCode = "+mObserver.hashCode());
				}else{
					Log.d(TAG, "add mObserver fail hashCode = "+mObserver.hashCode());
				}
			}
		}
	}
	
	public void removeObserver(IObserverListener mObserver){
		synchronized (observes) {
			if(observes.contains(mObserver)){
				boolean successful = observes.remove(mObserver);
				if(successful){
					Log.d(TAG, "remove mObserver successful hashCode = "+mObserver.hashCode());
				}else{
					Log.d(TAG, "remove mObserver fail hashCode = "+mObserver.hashCode());
				}
			}
		}
	}
	
	public void notifyObserver(int sdkStatus){
		if(observes == null || observes.size() == 0){
			return;
		}
		synchronized (observes) {
			for(IObserverListener observer : observes){
				observer.notify(sdkStatus);
			}
		}
	}
	@Override
	public void onChange(String notifyId) {
		Log.i(TAG, "reQuery userSettings onChange id = "+notifyId);
		userSetting = UserSettingsDbManager.getInstance().getById(UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount());
	}
}
