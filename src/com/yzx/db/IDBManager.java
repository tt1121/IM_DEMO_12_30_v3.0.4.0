package com.yzx.db;

import java.util.List;
/**
* @Title IDBManager 
* @Description �������ݿ�����ӿڷ�װ
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 ����4:00:48 
*/
public interface IDBManager<T> {
	//��ȡ����
	List<T> getAll();
	
	//�����û�id��ȡ
	T getById(String id);
	
	//�����û�idɾ��
	int deleteById(String id);
	
	//��������
	int insert(T t);
	
	int update(T t);
}
