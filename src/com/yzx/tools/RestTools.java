package com.yzx.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.yzx.im_demo.userdata.GroupBean;
import com.yzx.im_demo.userdata.RestBean;
import com.yzxtcp.tools.CustomLog;

public class RestTools {
	public static final int LOGIN_STATUS_FAIL = -1;
	public static final int LOGIN_STATUS_SUCCESS = 0;
	public static final int LOGIN_STATUS_INPROCESS = 1;
	public static final int LOGIN_STATUS_TIMEOUT = 2;
	public static final int LOGIN_REST_TOKEN_OK = 3;
	public static final int LOGIN_REST_REGISTER_OK = 4;
	public static final int LOGIN_REST_HAS_REGISTER = 5;
	public static final int LOGIN_REST_UNREGISTER = 6;
	public static final int LOGIN_REST_REGISTER_FAIL = 7;
	public static final int LOGIN_REST_STARTGETTOKEN = 8;
	public static final int LOGIN_REST_STARTREGISTER = 9;
	public static final int LOGIN_REST_FINISH = 10;
	public static final int LOGIN_REST_GETTOKENFAIL = 11;
	public static final int LOGIN_REST_TOKEN_FAIL = 12;

	private static final String REST_REGISTER_SUCCESS = "0";
	private static final String REST_TOKEN_SUCCESS = "0";
	private static final String REST_HAS_REGISTER = "-1";
	private static final String REST_UNREGISTER = "-2";
	private static final String REST_REGISTER_FAIL = "-3";
	private static final String REST_NOTFINDGROUP_FAIL = "-4";
	private static final String REST_JOINGROUP_FAIL = "-5";
	private static final String TAG = RestTools.class.getSimpleName();
	private static String LOGIN_URL_PRE = "http://imactivity.ucpaas.com/"
			+ "user/login.do?phone=";
	private static String REG_URL_PRE = "http://imactivity.ucpaas.com/"
			+ "user/reg.do?";
	private static String GROUP_URL_PRE = "http://imactivity.ucpaas.com/user"
			+ "/queryGroup.do?userid=";
	public static String mPhoneNum;
	public static String mNickName;
	public static List<GroupBean> groupBeans;
	
	private static void initUrl(Context context){
		String asAddressAndPort = context.getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_ASADDRESS", "");
		Log.i(TAG, "asAddressAndPort : "+asAddressAndPort);
		if(!TextUtils.isEmpty(asAddressAndPort)){
			LOGIN_URL_PRE = "http://"+asAddressAndPort + "/user/login.do?phone=";
			REG_URL_PRE = "http://"+asAddressAndPort+ "/user/reg.do?";
			GROUP_URL_PRE = "http://"+asAddressAndPort + "/user/queryGroup.do?userid=";
		}else{
			LOGIN_URL_PRE = "http://imactivity.ucpaas.com/"
					+ "user/login.do?phone=";
			REG_URL_PRE = "http://imactivity.ucpaas.com/"
					+ "user/reg.do?";
			GROUP_URL_PRE = "http://imactivity.ucpaas.com/user"
					+ "/queryGroup.do?userid=";
		}
	}

	public static void accountRegister(final Context context,final String phoneNum,
			final String nickName, final Handler handler) {
		if (StringUtils.isEmpty(phoneNum) || StringUtils.isEmpty(nickName)
				|| handler == null) {
			CustomLog.e("accountRegister 参数错误");
		}
		mPhoneNum = phoneNum;
		mNickName = nickName;
		
		new Thread(new Runnable() {
			RestBean restBean = null;
			Message msg = null;

			@Override
			public void run() {
				String nickNameUTF8 = null;
				try {
					nickNameUTF8 = URLEncoder.encode(nickName, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				initUrl(context);
				String urlStr = REG_URL_PRE + "phone=" + phoneNum
						+ "&nickname=" + nickNameUTF8;
				String result = HttpUtils.doGet(urlStr);
				if (result != null) {
					restBean = parseGetTokenJson(result);
					if (restBean.getResult().equals(REST_REGISTER_SUCCESS)
							|| restBean.getResult().equals(REST_HAS_REGISTER)) {
						msg = handler.obtainMessage(LOGIN_REST_TOKEN_OK);
						Bundle data = new Bundle();
						data.putString("imtoken", restBean.getImtoken());
						msg.setData(data);
						handler.sendMessage(msg);
					} else if (restBean.getResult().equals(REST_REGISTER_FAIL)) {
						handler.sendEmptyMessage(LOGIN_REST_REGISTER_FAIL);
					} else {
						handler.sendEmptyMessage(LOGIN_STATUS_FAIL);
						CustomLog
								.e("parseGetTokenJson unknow result:" + result);
					}
				}
			}
		}).start();
	}

	public static void getToken(final Context context,final String phoneNum, final Handler handler) {
		if (StringUtils.isEmpty(phoneNum) || handler == null) {
			CustomLog.e("getToken 参数错误");
			return ;
		}
		mPhoneNum = phoneNum;
		new Thread(new Runnable() {
			@Override
			public void run() {
				RestBean restBean = null;
				Message msg = null;

				initUrl(context);
				String urlStr = LOGIN_URL_PRE + phoneNum;
				Log.i(TAG, "getToken url = "+urlStr);
				String result = HttpUtils.doGet(urlStr);
				if (result != null) {
					restBean = parseGetTokenJson(result);
					if (restBean.getResult().equals(REST_TOKEN_SUCCESS)) {
						mPhoneNum = restBean.getPhone();
						mNickName = restBean.getNickname();
						msg = handler.obtainMessage(LOGIN_REST_TOKEN_OK);
						Bundle data = new Bundle();
						data.putString("imtoken", restBean.getImtoken());
						msg.setData(data);
						handler.sendMessage(msg);
					} else if (restBean.getResult().equals(REST_UNREGISTER)) {
						msg = handler.obtainMessage(LOGIN_REST_UNREGISTER);
						Bundle data = new Bundle();
						data.putString("phoneNum", phoneNum);
						msg.setData(data);
						handler.sendMessage(msg);
					} else {
						handler.sendEmptyMessage(RestTools.LOGIN_REST_TOKEN_FAIL);
						CustomLog
								.e("parseGetTokenJson unknow result:" + result);
					}
				}else{
					handler.sendEmptyMessage(RestTools.LOGIN_REST_TOKEN_FAIL);
				}
			}
		}).start();
	}

	public static void queryGroupInfo(final Context context,final String phoneNum, 
								final IGroupInfoCallBack callBack) {
		if (StringUtils.isEmpty(phoneNum)) {
			CustomLog.e("getGroupInfo 参数错误");
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initUrl(context);
				String urlStr = GROUP_URL_PRE + phoneNum;
				String result = HttpUtils.doGet(urlStr);
				if (result != null) {
					groupBeans = parseGroupInfo(result);
					if(callBack != null){
						callBack.onGroupInfo(groupBeans);
					}
				} else {
					CustomLog.e("获取群组信息失败");
				}
			}
		}).start();

	}

	public static List<GroupBean> getGroupInfo() {
		return groupBeans;
	}

	private static List<GroupBean> parseGroupInfo(String jsonData) {
		List<GroupBean> groupBeans = new ArrayList<GroupBean>();
//		List<UserList> userLists = new ArrayList<UserList>();
		try {
//			jsonData = URLDecoder.decode(jsonData, "UTF-8");
//			jsonData = new String(jsonData.getBytes("UTF-8"));
			JSONArray arr = new JSONArray(jsonData);
			for (int i = 0; i < arr.length(); i++) {
				GroupBean groupBean = new GroupBean();
				JSONObject jsonGroup = (JSONObject) arr.get(i);
				if(jsonGroup.has("groupicon")){
					groupBean.setGroupicon(jsonGroup.getString("groupicon"));
				}else{
					groupBean.setGroupicon("");
				}
				if(!jsonGroup.has("groupid") || !jsonGroup.has("groupname")){
					CustomLog.e("groupid == null || groupname 返回null!");
					return null;
				}
				groupBean.setGroupID(jsonGroup.getString("groupid"));
				groupBean.setGroupName(jsonGroup.getString("groupname"));
				
				Log.i(TAG, "get Group "+groupBean.getGroupName());

//				JSONArray userListArray = jsonGroup.getJSONArray("userlist");
//				for (int j = 0; j < userListArray.length(); j++) {
//					UserList user = new UserList();
//					JSONObject jsonUser = (JSONObject) userListArray.get(i);
//					user.setNickName(jsonUser.getString("nickname"));
//					user.setPhone(jsonUser.getString("phone"));
//					user.setPortraituri(jsonUser.getString("portraituri"));
//					user.setUserID(jsonUser.getString("userid"));
//					userLists.add(user);
//				}
//				groupBean.setUserLists(userLists);

				groupBeans.add(groupBean);
				Log.i(TAG, "groupBean = "+groupBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			CustomLog.e("解析群组信息JSON失败!!!!");
		}
		return groupBeans;
	}

	private static RestBean parseGetTokenJson(String json) {
		RestBean restBean = new RestBean();
		try {
			JSONObject tokens = new JSONObject(json);
			String result = "";
			if(tokens.has("result")){
				result = tokens.getString("result");
			}
			restBean.setResult(result);
			if (result.equals(REST_REGISTER_SUCCESS)) {
				if(tokens.has("imtoken")){
					restBean.setImtoken(tokens.getString("imtoken"));
				}
				if(tokens.has("nickname")){
					restBean.setNickname(tokens.getString("nickname"));
				}
				if(tokens.has("portraituri")){
					restBean.setPortraituri(tokens.getString("portraituri"));
				}
				if(tokens.has("phone")){
					restBean.setPhone(tokens.getString("phone"));
				}
			} else if (result.equals(REST_UNREGISTER)) {
				CustomLog.e("parseGetTokenJson REST_UNREGISTER result:"
						+ result);
			} else if (result.equals(REST_HAS_REGISTER)) {
				CustomLog.e("parseGetTokenJson REST_HAS_REGISTER result:"
						+ result);
				restBean.setImtoken(tokens.getString("imtoken"));
				restBean.setNickname(tokens.getString("nickname"));
			} else if (result.equals(REST_REGISTER_FAIL)) {
				CustomLog.e("parseGetTokenJson REST_REGISTER_FAIL result:"
						+ result);
			} else {
				CustomLog.e("parseGetTokenJson unknow result:" + result);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return restBean;
	}
}

interface IGroupInfoCallBack{
	void onGroupInfo(List<GroupBean> groupBeans);
}
