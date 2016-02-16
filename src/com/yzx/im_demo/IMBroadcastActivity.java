package com.yzx.im_demo;

import java.util.ArrayList;
import java.util.List;
import com.yzx.adapter.MessageAdapter;
import com.yzx.mydefineview.MyListView;
import com.yzx.mydefineview.MyListView.OnRefreshListener;
import com.yzx.mydefineview.YzxTopBar;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.listener.MessageListener;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
/**
* @Title IMBroadcastActivity 
* @Description �㲥��ϢActivity
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-17 ����2:18:49 
*/
public class IMBroadcastActivity extends Activity implements MessageListener{
	
	private static final String TAG = IMBroadcastActivity.class.getSimpleName();
	private List<ChatMessage> messages;
	private ConversationInfo conversationInfo;
	private MyListView msgListView;
	private YzxTopBar topBar;
	private MessageAdapter mAdapter;
	private IMManager imManager;
	private int unreadcount;//δ����Ϣ��
	
	//�յ�����Ϣ
	private static final int RECEIVE_MSG_SUCCESS = 200;
	//ˢ��δ����Ϣ��
	private static final int REFRESH_UNREAD_COUNT = 202;
	
	private static final int SET_SETECTION = 203;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RECEIVE_MSG_SUCCESS:
				List<ChatMessage> msgs = (List<ChatMessage>) msg.obj;
				synchronized (messages) {
					messages.addAll(msgs);
					mAdapter.initTimes();
					msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
					mAdapter.notifyDataSetChanged();
					msgListView.setSelection(messages.size());
				}
				//ˢ��δ����Ϣ��
				mHandler.sendEmptyMessage(REFRESH_UNREAD_COUNT);
				break;
			case REFRESH_UNREAD_COUNT:
				conversationInfo.clearMessagesUnreadStatus();
				unreadcount = imManager.getUnreadCountAll();
				topBar.setUnReadCount(unreadcount);
				break;
			case SET_SETECTION:
				msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				msgListView.setSelection(messages.size());
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast);
		imManager = IMManager.getInstance(this);
		imManager.setSendMsgListener(this);
		initViews();
		
		initDatas();
	}
	private void initViews() {
		msgListView = (MyListView) findViewById(R.id.broadcast_msgs);
		topBar = (YzxTopBar) findViewById(R.id.broadcast_top);
		
		topBar.setTitle("�������㲥��Ϣ");
		topBar.setBackBtnOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//����������
				Intent intent = new Intent(IMBroadcastActivity.this, IMChatActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		//ListViewˢ�¼���
		msgListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, List<ChatMessage>>() {
					@Override
					protected List<ChatMessage> doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
							if(messages != null && messages.size() > 0){
								List<ChatMessage> msgs = conversationInfo
										.getLastestMessages(
												messages.size(), 20);
								return msgs;
							}
							return null;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return null;
					}
					@Override
					protected void onPostExecute(List<ChatMessage> result) {
						if(result != null && result.size() > 0){
							messages.addAll(0, result);
							mAdapter.initTimes();
							msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
							mAdapter.notifyDataSetChanged();
							msgListView.setSelection(result.size());
						}else{
//							Toast.makeText(IMBroadcastActivity.this, "���޸�����Ϣ", 0).show();
						}
						msgListView.onRefreshComplete();
					}
				}.execute();
			}
		});
	}
	/**
	* @Description ��ʼ������
	* @date 2015-12-17 ����3:22:10 
	* @author zhuqian  
	* @return void
	 */
	private void initDatas() {
		conversationInfo = (ConversationInfo) getIntent().getSerializableExtra("conversation");
		messages = conversationInfo.getLastestMessages(0, 20);
		
		if(messages != null){
			mAdapter = new MessageAdapter(this, messages, R.layout.broadcast_message_item);
			mHandler.sendEmptyMessageDelayed(SET_SETECTION, 10);
			msgListView.setAdapter(mAdapter);
		}
	}
	
	@Override
	protected void onResume() {
		//����ûỰ��δ����Ϣ��
		conversationInfo.clearMessagesUnreadStatus();
		//��ȡȫ��δ����Ϣ��
		unreadcount = IMManager.getInstance(this).getUnreadCountAll();
		topBar.setUnReadCount(unreadcount);
		super.onResume();
	}
	@Override
	public void onDownloadAttachedProgress(String arg0, String arg1, int arg2,
			int arg3) {
		
	}
	@Override
	public void onReceiveMessage(List msgLists) {
		//��ȡ��ǰ�Լ��Ự��Ϣ
		List<ChatMessage> msgs = msgLists;
		List<ChatMessage> currentMsgs = null;
		for(ChatMessage msg : msgs){
			if(msg.getParentID().equals(conversationInfo.getTargetId())){
				if(currentMsgs == null){
					currentMsgs = new ArrayList<ChatMessage>();
				}
				currentMsgs.add(msg);
			}
		}
		if(currentMsgs != null){
			Message msg = mHandler.obtainMessage();
			msg.obj = currentMsgs;
			msg.what = RECEIVE_MSG_SUCCESS;
			Log.i(TAG, "�յ���ǰ�Ự����Ϣ size : "+currentMsgs.size());
			mHandler.sendMessage(msg);
		}else{
			mHandler.sendEmptyMessage(REFRESH_UNREAD_COUNT);
		}
		Log.i(TAG, "�յ�����Ϣ size : "+msgs.size());
	}
	@Override
	public void onSendMsgRespone(ChatMessage arg0) {
		
	}
	
	@Override
	protected void onDestroy() {
		imManager.removeSendMsgListener(this);
		super.onDestroy();
	}
}
