package com.yzx.db;

import java.util.List;
import com.yzx.db.domain.UserSetting;
import com.yzx.im_demo.MainApplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


/**
* @Title UserSettingsDbManager 
* @Description �û��������ݿ������
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 ����10:10:56 
*/
public class UserSettingsDbManager extends AbsDBManager implements IDBManager<UserSetting>{
	
	private static final String TAG = UserSettingsDbManager.class.getSimpleName();
	
	private static UserSettingsDbManager instance;
	
	public static UserSettingsDbManager getInstance(){
		if(instance == null){
			instance = new UserSettingsDbManager(MainApplication.getInstance());
		}
		return instance;
	}

	public UserSettingsDbManager(Context context) {
		super(context);
	}

	@Override
	public List<UserSetting> getAll() {
		return null;
	}

	/**
	 * ���ݵ�ǰ�˺ŵ�����
	 */
	public UserSetting getById(String id) {
		String sql = null;
		Cursor cursor = null;
		UserSetting setting = null;
		try {
			sql = "select * from " + YzxDbHelper.TABLE_USER_SETTINGS
					+ " where _account = "+id;
			cursor = getInstance().sqliteDB().rawQuery(sql, null);
			while (cursor.moveToFirst()) {
				setting = new UserSetting();
				setting.setId(cursor.getString(cursor.getColumnIndex(UserSettingColume._ID)));
				setting.setAccount(cursor.getString(cursor.getColumnIndex(UserSettingColume._ACCOUNT)));
				setting.setAsAddressAndPort(cursor.getString(cursor.getColumnIndex(UserSettingColume._ASADDRESSANDPORT)));
				setting.setTcpAddressAndPort(cursor.getString(cursor.getColumnIndex(UserSettingColume._TCPADDRESSANDPORT)));
				setting.setToken(cursor.getString(cursor.getColumnIndex(UserSettingColume._TOKEN)));
				setting.setMsgNotify(cursor.getInt(cursor.getColumnIndex(UserSettingColume._MSGNOTIFY)));
				setting.setMsgVitor(cursor.getInt(cursor.getColumnIndex(UserSettingColume._MSGVITOR)));
				setting.setMsgVoice(cursor.getInt(cursor.getColumnIndex(UserSettingColume._MSGVOICE)));
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
		return setting;
	}

	@Override
	public int deleteById(String id) {
		return 0;
	}

	@Override
	public int insert(UserSetting t) {
		ContentValues values = null;
		int result = 0;
		try {
			values = new ContentValues();
			values.put(UserSettingColume._ACCOUNT, t.getAccount());
			values.put(UserSettingColume._ASADDRESSANDPORT, t.getAsAddressAndPort());
			values.put(UserSettingColume._TCPADDRESSANDPORT, t.getTcpAddressAndPort());
			values.put(UserSettingColume._MSGNOTIFY, t.getMsgNotify());
			values.put(UserSettingColume._MSGVITOR, t.getMsgVitor());
			values.put(UserSettingColume._MSGVOICE, t.getMsgVoice());
			values.put(UserSettingColume._TOKEN, t.getToken());
			result = (int) getInstance().sqliteDB().insert(YzxDbHelper.TABLE_USER_SETTINGS, null, values);
			Log.i(TAG, "insert successfully result = "+result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}

	@Override
	public int update(UserSetting t) {
		ContentValues values = null;
		int result = 0;
		try {
			values = new ContentValues();
			values.put(UserSettingColume._ASADDRESSANDPORT, t.getAsAddressAndPort());
			values.put(UserSettingColume._TCPADDRESSANDPORT, t.getTcpAddressAndPort());
			values.put(UserSettingColume._MSGNOTIFY, t.getMsgNotify());
			values.put(UserSettingColume._MSGVITOR, t.getMsgVitor());
			values.put(UserSettingColume._MSGVOICE, t.getMsgVoice());
			values.put(UserSettingColume._TOKEN, t.getToken());
			result = (int) getInstance().sqliteDB().update(YzxDbHelper.TABLE_USER_SETTINGS, values, "_account = ?", new String[]{t.getAccount()});
			Log.i(TAG, "update successfully result = "+result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		notify(t.getId());
		return result;
	}
}
