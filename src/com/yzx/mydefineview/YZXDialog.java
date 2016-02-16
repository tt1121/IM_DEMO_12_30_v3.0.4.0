package com.yzx.mydefineview;


import com.yzx.im_demo.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @author zhuqian
 *
 */
public class YZXDialog {

	/**
	 * ���м�ʱ���ĵ�ѡdialog
	 * @param mContext ������
	 * @param titleicon  ͷ��ͼ��
	 * @param title  ͷ���ı�
	 * @param message dialog�ı�
	 * @param positiveButton  ��ť����
	 * @param time  ����ʱʱ��ֵ
	 * @param positiveListener �ص��ӿ�
	 * @return
	 */
	public static Dialog showTimeAlertDialog(Context mContext,int titleicon,String title,final String message, String positiveButton,final int time,final OnTimeAlertViewClickListener positiveListener){
		dialog = new Dialog(mContext, R.style.alertView);
		timeCount=time;
		timemessage=message;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = (View) inflater.inflate(R.layout.alertview, null);
		// ���������
		TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
		messageTextView = (TextView) view.findViewById(R.id.messageTextView);
		ImageView icon=(ImageView)view.findViewById(R.id.titileicon);
		if(0!=titleicon){
			icon.setBackgroundResource(titleicon);
		}else{
			icon.setVisibility(View.GONE);
		}
		if (null != title && !title.equalsIgnoreCase("")) {
			titleTextView.setText(title);
		} else {
			titleTextView.setVisibility(View.GONE);
		}
		if (null != message && !message.equalsIgnoreCase("")) {
			messageTextView.setText(time+message);
		} else {
			messageTextView.setVisibility(View.GONE);
		}

		Button pButton = new Button(mContext);
		pButton.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, dip2px(mContext, 44), 1.0f));
		pButton.setTextColor(mContext.getResources().getColor(R.color.dialog_orange));
		pButton.setText(positiveButton);
		pButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != positiveListener) {
					positiveListener.confirm();
				}
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				handler.removeMessages(0);
				positiveListener.confirm();
			}
		});
		// ��̬��Ӱ�ť
		LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
		// һ����ť
		buttonLayout.setOrientation(LinearLayout.VERTICAL);
		pButton.setBackgroundResource(R.drawable.alert_time_bottom_button);
		buttonLayout.addView(pButton);
		final int viewWidth = dip2px(mContext, 250);
		view.setMinimumWidth(viewWidth);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.show();
		handler.sendEmptyMessage(-1);
		return dialog;
	}
	private static TextView messageTextView;
	private static Dialog dialog;
	private static int timeCount;//����ʱʱ��
	private static String timemessage;//����ʱ��Ϣ
	static Handler handler=new Handler(){@Override
		public void handleMessage(Message msg) {
		int what=msg.what;
		if(what==0){
			if(timeCount>0){
				timeCount--;
				messageTextView.setText(timeCount+timemessage);
				handler.sendEmptyMessageDelayed(0, 1000);
			}else{
				dialog.dismiss();
				handler.removeMessages(0);
			}
		}else{
			handler.sendEmptyMessageDelayed(0, 1000);
		}
		super.handleMessage(msg);
	}};
	
	
	/**
	 * ʵ��һ����ť��2����ť
	 * @param mContext ������
	 * @param titleicon  ͷ��ͼ��
	 * @param title   ͷ������
	 * @param message  ����
	 * @param positiveButton  һ��Ĭ�ϰ�ť
	 * @param otherButtons  ������ť
	 * @param positiveListener �ص�����
	 * @return
	 */
	public static Dialog showAlertView(Context mContext, int titleicon,String title,
			String message, String positiveButton,
			final String[] otherButtons,
			final OnAlertViewClickListener positiveListener) {
		final Dialog dialog = new Dialog(mContext, R.style.alertView);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = (View) inflater.inflate(R.layout.alertview, null);
		// ���������
		TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
		TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);
		ImageView icon=(ImageView)view.findViewById(R.id.titileicon);
		if(0!=titleicon){
			icon.setBackgroundResource(titleicon);
		}else{
			icon.setVisibility(View.GONE);
		}
		if (null != title && !title.equalsIgnoreCase("")) {
			titleTextView.setText(title);
		} else {
			titleTextView.setVisibility(View.GONE);
		}
		if (null != message && !message.equalsIgnoreCase("")) {
			messageTextView.setText(message);
		} else {
			messageTextView.setVisibility(View.GONE);
		}
		Button pButton = new Button(mContext);
		pButton.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, dip2px(mContext, 44), 1.0f));
		pButton.setTextColor(mContext.getResources().getColor(R.color.dialogTxtColor));
		// ��̬��Ӱ�ť
		LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
		if (null == otherButtons || otherButtons.length == 0) {
			// һ����ť
			buttonLayout.setOrientation(LinearLayout.VERTICAL);
			pButton.setBackgroundResource(R.drawable.alert_bottom_button);
		} else if (null != otherButtons && otherButtons.length == 1) {
			// ������ť
			buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
			pButton.setBackgroundResource(R.drawable.alert_left_button);
		} else {
			// �������������ϰ�ť
			buttonLayout.setOrientation(LinearLayout.VERTICAL);
			pButton.setBackgroundResource(R.drawable.alert_middle_button);
		}

		pButton.setText(positiveButton);
		pButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != positiveListener) {
					positiveListener.cancel();
				}
				dialog.dismiss();
			}
		});
		buttonLayout.addView(pButton);
		if (null != otherButtons && otherButtons.length > 0) {
			for (int i = 0; i < otherButtons.length; i++) {
				final int index = i;
				Button otherButton = new Button(mContext);
				otherButton.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, dip2px(mContext, 44), 1.0f));
				otherButton.setText(otherButtons[i]);
				otherButton.setTextColor(mContext.getResources().getColor(R.color.dialogTxtColor));
				if (1 == otherButtons.length) {
					otherButton
					.setBackgroundResource(R.drawable.alert_right_button);
				} else if (i < (otherButtons.length - 1)) {
					otherButton
					.setBackgroundResource(R.drawable.alert_middle_button);
				} else {
					otherButton
					.setBackgroundResource(R.drawable.alert_bottom_button);
				}
				otherButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != positiveListener) {
							positiveListener.confirm(otherButtons[index]);
						}
						dialog.dismiss();
					}
				});
				buttonLayout.addView(otherButton);
			}
		}
		final int viewWidth = dip2px(mContext, 250);
		view.setMinimumWidth(viewWidth);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.setContentView(view);
		dialog.show();
		return dialog;
	}
	/**
	 * �ײ�����dialog
	 * @param mContext
	 * @param title
	 * @param items
	 * @param dialogClickListener
	 * @return
	 */
	public static android.app.Dialog ShowDialog(Context mContext,String title,String[] items,final DialogItemClickListener dialogClickListener){ 
		final android.app.Dialog dialog=new android.app.Dialog(mContext, R.style.DialogStyle);
		dialog.setCancelable(false);
		View view=LayoutInflater.from(mContext).inflate(R.layout.dialog_radio, null);
		dialog.setContentView(view);
		if(title.equals("")||title==null){
			((TextView)view.findViewById(R.id.title)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.title_divider)).setVisibility(View.GONE);
		}else{
			((TextView)view.findViewById(R.id.title)).setText(title);
		}
		//����items��̬����
		LinearLayout parent=(LinearLayout) view.findViewById(R.id.dialogLayout);
		parent.removeAllViews();
		int length=items.length;
		for (int i = 0; i < items.length; i++) {
			LayoutParams params1=new LayoutParams(-1,-2);
			params1.rightMargin=1;
			final TextView tv=new TextView(mContext);
			tv.setLayoutParams(params1);
			tv.setTextSize(18);
			tv.setText(items[i]);
			tv.setTextColor(mContext.getResources().getColor(R.color.dialogTxtColor));
			int pad=mContext.getResources().getDimensionPixelSize(R.dimen.padding10);
			tv.setPadding(pad,pad,pad,pad);
			tv.setSingleLine(true);
			tv.setGravity(Gravity.CENTER);
			if(i!=length-1)
				if((title.equals("")||title==null)&&i==0)
					tv.setBackgroundResource(R.drawable.menudialog_top2_selector);
				else
					tv.setBackgroundResource(R.drawable.menudialog_center_selector);

			else
				if(length==1)
					tv.setBackgroundResource(R.drawable.menudialog_bottom_selector);
				else
					tv.setBackgroundResource(R.drawable.menudialog_bottom2_selector);

			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					dialogClickListener.confirm(tv.getText().toString());
				}
			});
			parent.addView(tv);
			if(i!=length-1){
				TextView divider=new TextView(mContext);
				LayoutParams params=new LayoutParams(-1,(int)1);
				divider.setLayoutParams(params);
				divider.setBackgroundResource(android.R.color.darker_gray);
				parent.addView(divider);
			}
		}
		view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		Window mWindow=dialog.getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.width= getScreenWidth(mContext);
		mWindow.setGravity(Gravity.BOTTOM);
		//��Ӷ���
		mWindow.setWindowAnimations(R.style.dialogAnim);
		mWindow.setAttributes(lp);
		dialog.show();
		return dialog;
	}
	/**
	 * �ص��ӿ�
	 * @author Administrator
	 *
	 */
	public interface OnAlertViewClickListener {
		public abstract void confirm(String result);
		public abstract void cancel();
	}
	/**
	 * ʱ��dialog�ص��ӿ�
	 * @author Administrator
	 *
	 */
	public interface OnTimeAlertViewClickListener {
		public abstract void confirm();
	}

	public interface DialogItemClickListener{
		public abstract void confirm(String result);
	}
	
	/**��ȡ��Ļ�ֱ��ʿ�*/
	public static int getScreenWidth(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
	}
	/**��ȡ��Ļ�ֱ��ʸ�*/
	public static int getScreenHeight(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
	}
	/**��ȡ��Ļ�ֱ��ʿ����dialog�Ŀ��*/
	private static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
