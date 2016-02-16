package com.yzx.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import com.yzx.im_demo.contact.CharacterParser;
import com.yzx.im_demo.contact.PinyinComparator;
import com.yzx.model.SortModel;
import com.yzxtcp.tools.CustomLog;

public class ContactTools {
	
	private static List<SortModel> SourceDateList=new ArrayList<SortModel>();
	private static CharacterParser characterParser = CharacterParser.getInstance();
	private static PinyinComparator pinyinComparator = new PinyinComparator();;
	
	/**��ϵ����ʾ����**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    /**�绰����**/  
    private static final int PHONES_NUMBER_INDEX = 1;  
      
    /**ͷ��ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;
     
    /**��ϵ�˵�ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3;  
    
	private static final String[] PHONES_PROJECTION = new String[] {  
	       Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID, Phone.CONTACT_ID};
	
	public static List<SortModel> getSourceDateList(){
		return SourceDateList;
	}
	
	public static void initContacts(final Context mContext) {
		CustomLog.d("initContacts start");
		List<SortModel> list = getPhoneContacts(mContext);
		SourceDateList.clear();
		SourceDateList.addAll(list);
		Collections.sort(SourceDateList, pinyinComparator);
		CustomLog.d("initContacts finish");
	}
	
	  /**�õ��ֻ�ͨѶ¼��ϵ����Ϣ**/  
    private static List<SortModel> getPhoneContacts(Context mContext) {
    	 List<SortModel> mSortList = new ArrayList<SortModel>();
    	 //���3����Ĭ���˺ź�
    	 contactAddDefaultAccount(mSortList);
		 ContentResolver resolver = mContext.getContentResolver();  
		   
		 // ��ȡ�ֻ���ϵ��  
		 Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		   
		 if (phoneCursor != null) {  
		     while (phoneCursor.moveToNext()) {  
		   
		    	SortModel sortModel = new SortModel();

				//�õ��ֻ�����  
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				//���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��  
				if (TextUtils.isEmpty(phoneNumber))  
					continue;
		       
				//�õ���ϵ������  
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
		       
				//�õ���ϵ��ID  
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);  
		   
				//�õ���ϵ��ͷ��ID  
				//Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);  
		       
				//�õ���ϵ��ͷ��Bitamp  
				Bitmap contactPhoto = null;  
		   
				//photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�  
//				if(photoid > 0 ) {  
//					Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);  
//					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);  
//					contactPhoto = BitmapFactory.decodeStream(input);  
//				}

				sortModel.setName(contactName);
				sortModel.setId(phoneNumber);
				String pinyin = characterParser.getSelling(contactName);
				String sortString = pinyin.substring(0, 1).toUpperCase();

				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
				sortModel.setBitmap(contactPhoto);
				mSortList.add(sortModel);
		     }  
		     phoneCursor.close();  
		 }  
		 return mSortList;
    }  
    
    private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);

			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * ListView
	 * 
	 * @param filterStr
	 */
	private List<SortModel> filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		Collections.sort(filterDateList, pinyinComparator);
		return filterDateList;
	}
	
	
	private static void contactAddDefaultAccount(List<SortModel> mSortList){
		String[] contactName = new String[]{"�����˺�1","�����˺�2","�����˺�3"};
		String[] phoneNumber = new String[]{"18687654321", "18687654322", "18687654323"};
		for(int i = 0; i < contactName.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(contactName[i]);
			sortModel.setId(phoneNumber[i]);
			String pinyin = characterParser.getSelling(contactName[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			sortModel.setSortLetters("*");
			/*if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}*/
			mSortList.add(sortModel);
		}
	}
	
	public static String getConTitle(String id){
		if(SourceDateList == null || SourceDateList.size() == 0){
			return "";
		}
		String result = "";
		for(SortModel item : SourceDateList){
			if(item.getId().equals(id)){
				result = item.getName();
			}
		}
		return result;
	}
}
