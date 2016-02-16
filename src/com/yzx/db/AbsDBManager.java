package com.yzx.db;

import com.yzx.tools.YZXContents;
import com.yzxtcp.tools.CustomLog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
* @Title AbsDBManager 
* @Description �����DBManager
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 ����2:29:11 
*/
public abstract class AbsDBManager {
	
	public static final String TAG = AbsDBManager.class.getSimpleName();
	
	private final DBObserver dbObserver = new DBObserver();
	private static SQLiteDatabase yzxDb;
	private static YzxDbHelper yzxDbHelper;
	
	public AbsDBManager(Context context){
		openDb(context,YZXContents.getVersionCode());
	}

	private void openDb(Context context, int versionCode) {
		if(yzxDbHelper == null){
			yzxDbHelper = new YzxDbHelper(context, this, versionCode);
		}
		if(yzxDb == null){
			yzxDb = yzxDbHelper.getWritableDatabase();
		}
	}
	
	private void open(boolean isReadOnly){
		if(yzxDb == null){
			if(isReadOnly){
				yzxDb = yzxDbHelper.getReadableDatabase();
			}else{
				yzxDb = yzxDbHelper.getWritableDatabase();
			}
		}
	}
	public void destroy(){
		try {
			if(yzxDbHelper != null){
				yzxDbHelper.close();
			}
			closeDb();
		} catch (Exception e) {
			CustomLog.d(e.toString());
		}
	}
	
	public final void reopen(){
		closeDb();
		open(false);
		CustomLog.d("----reopen this db----");
	}
	private void closeDb(){
		if(yzxDb != null){
			yzxDb.close();
			yzxDb = null;
		}
	}
	
	protected final SQLiteDatabase sqliteDB(){
		open(false);
		return yzxDb;
	}

	public static class YzxDbHelper extends SQLiteOpenHelper{
		
		public static final String DATABASE_NAME = "yunzhixun.db";
		
		public static final String TABLE_USER = "user";
		public static final String TABLE_USER_SETTINGS = "user_settings";
		public static final String TABLE_CONVERSATION_BG = "conversation_bg";
		public static final String TABLE_DRAFT_MSG = "draft_msg";
		
		private AbsDBManager dbManager;
		
		public YzxDbHelper(Context context,AbsDBManager dbManager,int version) {
			this(context, DATABASE_NAME, null, version, dbManager);
		}
		public YzxDbHelper(Context context, String name, CursorFactory factory,
				int version,AbsDBManager dbManager) {
			super(context, name, factory, version);
			this.dbManager = dbManager;
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			createTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
		public void createTables(SQLiteDatabase db) {
			//�����û���Ϣ��
			createUserTable(db);
			//�����û����ñ�
			createUserSettingsTable(db);
			//�����Ự������
			createConversationBgTable(db);
			//�����ݸ��
			createDraftMsgTable(db);
		}
		private void createDraftMsgTable(SQLiteDatabase db) {
			String sql = "CREATE TABLE IF NOT EXISTS " +
					TABLE_DRAFT_MSG
					+ " ("
					+ DraftMsgColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ DraftMsgColume._ACCOUNT +" TEXT, "
					+ DraftMsgColume._TARGETID +" TEXT, "
					+ DraftMsgColume._DRAFTMSG +" TEXT"
					+")";
			CustomLog.d("execute createDraftMsgTable sql = "+sql);
			db.execSQL(sql);
		}
		private void createConversationBgTable(SQLiteDatabase db) {
			String sql = "CREATE TABLE IF NOT EXISTS " +
					TABLE_CONVERSATION_BG
					+ " ("
					+ ConversationBgColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ConversationBgColume._ACCOUNT +" TEXT, "
					+ ConversationBgColume._TARGETID +" TEXT, "
					+ ConversationBgColume._BGPATH +" TEXT"
					+")";
			CustomLog.d("execute createConversationBgTable sql = "+sql);
			db.execSQL(sql);
		}
		private void createUserSettingsTable(SQLiteDatabase db) {
			String sql = "CREATE TABLE IF NOT EXISTS " +
					TABLE_USER_SETTINGS
					+ " ("
					+ UserSettingColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ UserSettingColume._ACCOUNT +" TEXT, "
					+ UserSettingColume._ASADDRESSANDPORT +" TEXT, "
					+ UserSettingColume._TCPADDRESSANDPORT +" TEXT, "
					+ UserSettingColume._TOKEN +" TEXT, "
					+ UserSettingColume._MSGNOTIFY +" INTEGER DEFAULT 1, "
					+ UserSettingColume._MSGVITOR +" INTEGER DEFAULT 1, "
					+ UserSettingColume._MSGVOICE +" INTEGER DEFAULT 1"
					+")";
			CustomLog.d("execute createUserSettingsTable sql = "+sql);
			db.execSQL(sql);
		}
		private void createUserTable(SQLiteDatabase db) {
			String sql = "CREATE TABLE IF NOT EXISTS " +
					TABLE_USER
					+ " ("
					+ UserInfoColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ UserInfoColumn._ACCOUNT +" TEXT, "
					+ UserInfoColumn._NAME+" TEXT, "
					+ UserInfoColumn._LOGINSTATUS +" INTEGER DEFAULT 1, "
					+ UserInfoColumn._DEFAULTLOGIN +" INTEGER DEFAULT 1"
					+")";
			CustomLog.d("execute createUserTable sql = "+sql);
			db.execSQL(sql);
		}
	}
	
	public void addObserver(OnDbChangeListener observer){
		dbObserver.addObserver(observer);
	}

	public void removeObserver(OnDbChangeListener observer){
		dbObserver.removeObserver(observer);
	}
	
	protected void notify(String notifyId){
		dbObserver.notify(notifyId);
	}
	
	protected void clear(){
		dbObserver.clear();
	}
	
	/**
	* @Title BaseColumn 
	* @Description �����ı����ֶ�
	* @Company yunzhixun
	* @author zhuqian
	* @date 2015-12-2 ����2:32:00
	 */
	public static class BaseColumn{
		public static final String _ID = "_id";
	}
	/**
	* @Title UserInfoColumn 
	* @Description �û������ֶ�
	* @Company yunzhixun
	* @author zhuqian
	* @date 2015-12-2 ����2:33:28
	 */
	public static class UserInfoColumn extends BaseColumn{
		public static final String _ACCOUNT = "_account";
		public static final String _NAME = "_name";
		public static final String _LOGINSTATUS = "_loginStatus";
		public static final String _DEFAULTLOGIN = "_defaultLogin";
	}
	/**
	* @Title UserSettingCloume 
	* @Description �û����ñ����ֶ�
	* @Company yunzhixun
	* @author zhuqian
	* @date 2015-12-2 ����2:51:37
	 */
	public static class UserSettingColume extends BaseColumn{
		public static final String _ACCOUNT = "_account";
		public static final String _MSGNOTIFY = "_msgNotify";
		public static final String _MSGVOICE = "_msgVoice";
		public static final String _MSGVITOR = "_msgVitor";
		public static final String _TOKEN = "_token";
		public static final String _ASADDRESSANDPORT = "_asAddressAndPort";
		public static final String _TCPADDRESSANDPORT = "_tcpAddressAndPort";
	}
	/**
	* @Title ConversationBgCloume 
	* @Description �Ự���������ֶ�
	* @Company yunzhixun
	* @author zhuqian
	* @date 2015-12-2 ����2:53:20
	 */
	public static class ConversationBgColume extends BaseColumn{
		public static final String _ACCOUNT = "_account";
		public static final String _TARGETID = "_targetId";
		public static final String _BGPATH = "_bgPath";
	}
	/**
	* @Title DraftMsgColume 
	* @Description �ݸ�����ֶ�
	* @Company yunzhixun
	* @author zhuqian
	* @date 2015-12-2 ����2:54:35
	 */
	public static class DraftMsgColume extends BaseColumn{
		public static final String _ACCOUNT = "_account";
		public static final String _TARGETID = "_targetId";
		public static final String _DRAFTMSG= "_draftMsg";
	}
}
