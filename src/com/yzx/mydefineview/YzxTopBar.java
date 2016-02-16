package com.yzx.mydefineview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yzx.im_demo.R;

public class YzxTopBar extends RelativeLayout {

	private RelativeLayout imgBackBtn;
	private Button imgInfoBtn;
	private TextView  tvTitle;
	private TextView tvNumber;
	private LayoutInflater layoutInflater;
	private View myView;
	
	public YzxTopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		layoutInflater = LayoutInflater.from(context);
		myView = layoutInflater.inflate(R.layout.view_topbar,null);
		imgBackBtn = (RelativeLayout) myView.findViewById(R.id.imbtn_back);
		imgInfoBtn = (Button) myView.findViewById(R.id.imbtn_info);
		tvTitle = (TextView)myView.findViewById(R.id.tv_title);
		tvNumber = (TextView) myView.findViewById(R.id.tv_number);
		addView(myView);
	}
	//设置未读消息数
	public void setUnReadCount(int unreadcount){
		if(unreadcount <= 0){
			tvNumber.setVisibility(View.GONE);
		}else{
			tvNumber.setVisibility(View.VISIBLE);
			tvNumber.setText("消息("+unreadcount+")");
		}
	}
	
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	public void setTitleColor(String color){
		tvTitle.setBackgroundColor(Color.parseColor(color));
	}
	
	public void setBackBtnBackgroudResource(int resId) {  
		imgBackBtn.setBackgroundResource(resId);  
    }  
	
	public void setInfoBtnBackgroudResource(int resId) {  
		imgInfoBtn.setBackgroundResource(resId);  
    }  
	
	public void setBackVisibility(int visibility){
		imgBackBtn.setVisibility(visibility);
	}
	
	public void setInfoVisibility(int visibility) {  
		imgInfoBtn.setVisibility(visibility);  
    }  
	
	public void setBackBtnOnclickListener(OnClickListener clickListener){
		imgBackBtn.setOnClickListener(clickListener);
	}
	
	public void setInfoBtnOnclickListener(OnClickListener clickListener){
		imgInfoBtn.setOnClickListener(clickListener);
	}
	
	public void setLayoutBackgroudColor(String color){
		myView.setBackgroundColor(Color.parseColor(color));
	}
}
