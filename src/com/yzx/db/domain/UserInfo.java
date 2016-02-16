package com.yzx.db.domain;


/**
* @Title UserInfo 
* @Description �û���Ϣ��
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 ����12:27:42 
*/
public class UserInfo {
	
	public UserInfo(String account, String name, int loginStatus,
			int defaultLogin) {
		this.account = account;
		this.name = name;
		this.loginStatus = loginStatus;
		this.defaultLogin = defaultLogin;
	}
	public UserInfo(){
		
	}
	private String id;
	private String account;
	private String name;
	//1��ʾ��¼״̬��0��ʾΪ�ǵ�¼״̬
	private int loginStatus;
	//1��ʾ���˺���Ĭ�ϵ�¼�˺ţ�0��ʾ��Ĭ�ϵ�¼�˺�
	private int defaultLogin;
	public int getDefaultLogin() {
		return defaultLogin;
	}
	public void setDefaultLogin(int defaultLogin) {
		this.defaultLogin = defaultLogin;
	}
	public int getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
