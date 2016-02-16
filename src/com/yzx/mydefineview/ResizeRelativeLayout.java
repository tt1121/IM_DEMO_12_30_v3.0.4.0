package com.yzx.mydefineview;

import com.yzxtcp.tools.CustomLog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/**
 * ��������̱仯RelativeLayout����
 * @author zhuqian
 *
 */
public class ResizeRelativeLayout extends RelativeLayout {
	
	private OnSizeChangeListener mOnSizeChangeListener;
	

	public void setmOnSizeChangeListener(OnSizeChangeListener mOnSizeChangeListener) {
		this.mOnSizeChangeListener = mOnSizeChangeListener;
	}

	public ResizeRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ResizeRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//��һ�α�����ʱ��ԭ�߶�Ϊ0
		if(oldh != 0){
			if(mOnSizeChangeListener != null){
				mOnSizeChangeListener.onSizeChange(h, oldh);
			}
		}
		CustomLog.v("new h = "+h+",oldh="+oldh);
	}
	//�߶ȱ仯����
	public interface OnSizeChangeListener{
		void onSizeChange(int h,int oldh);
	}
}
