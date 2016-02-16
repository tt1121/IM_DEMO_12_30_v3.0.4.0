package com.yzx.db;

import java.util.List;
import com.yzx.db.domain.ConversationBg;
import com.yzx.im_demo.MainApplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
/**
* @Title ConversationBgDbManager 
* @Description �Ự�������ݿ������
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 ����10:35:07 
*/
public class ConversationBgDbManager extends AbsDBManager implements IDBManager<ConversationBg>{
	
	private static final String TAG = ConversationBgDbManager.class.getSimpleName();

	private static ConversationBgDbManager instance;
	
	public static ConversationBgDbManager getInstance(){
		if(instance == null){
			instance = new ConversationBgDbManager(MainApplication.getInstance());
		}
		return instance;
	}
	public ConversationBgDbManager(Context context) {
		super(context);
	}
	@Override
	public List<ConversationBg> getAll() {
		return null;
	}
	
	
	/**
	 * ���ݻỰid�ҵ���Ӧ�ĻỰ����
	 */
	public ConversationBg getById(String targetId) {
		String account = UserInfoDBManager.getInstance().getCurrentLoginUser().getAccount();
		String sql = null;
		Cursor cursor = null;
		ConversationBg conversationBg = null;
		try {
			sql = "select * from " + YzxDbHelper.TABLE_CONVERSATION_BG
					+ " where _account = "+account+" and _targetId = "+targetId;
			cursor = getInstance().sqliteDB().rawQuery(sql, null);
			while (cursor.moveToFirst()) {
				conversationBg = new ConversationBg();
				conversationBg.setId(cursor.getString(cursor.getColumnIndex(ConversationBgColume._ID)));
				conversationBg.setAccount(cursor.getString(cursor.getColumnIndex(ConversationBgColume._ACCOUNT)));
				conversationBg.setTargetId(cursor.getString(cursor.getColumnIndex(ConversationBgColume._TARGETID)));
				conversationBg.setBgPath(cursor.getString(cursor.getColumnIndex(ConversationBgColume._BGPATH)));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return conversationBg;
	}
	@Override
	public int deleteById(String id) {
		return 0;
	}
	@Override
	public int insert(ConversationBg t) {
		if(getById(t.getTargetId()) != null){
			//����
			return update(t);
		}
		ContentValues values = null;
		int result = 0;
		try {
			values = new ContentValues();
			values.put(ConversationBgColume._ACCOUNT, t.getAccount());
			values.put(ConversationBgColume._TARGETID, t.getTargetId());
			values.put(ConversationBgColume._BGPATH, t.getBgPath());
			result = (int) getInstance().sqliteDB().insert(YzxDbHelper.TABLE_CONVERSATION_BG, null, values);
			Log.i(TAG, "insert successfully result = "+result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	@Override
	public int update(ConversationBg t) {
		ContentValues values = null;
		int result = 0;
		try {
			values = new ContentValues();
			values.put(ConversationBgColume._BGPATH, t.getBgPath());
			result = (int) getInstance().sqliteDB().update(YzxDbHelper.TABLE_CONVERSATION_BG, values, "_account = ? and _targetId = ?", new String[]{t.getAccount(),t.getTargetId()});
			Log.i(TAG, "update successfully result = "+result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
}
