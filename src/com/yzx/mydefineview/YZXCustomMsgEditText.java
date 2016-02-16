package com.yzx.mydefineview;

import com.yzx.im_demo.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
/**
* @Title YZXCustomMsgEditText 
* @Description �Զ�����Ϣ�ı��༭�ؼ�
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-22 ����11:06:58 
*/
public class YZXCustomMsgEditText extends EditText {
	private static final String TAG = YZXCustomMsgEditText.class.getSimpleName();
	private TextPaint mTextPaint;
	private int textSize;
	private int textColor;
	private CharSequence text;//��ǰ�ı�����
	private int width;
	private int height;
	
	private OnTextChangeListener onTextChangeListener;
	
	public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
		this.onTextChangeListener = onTextChangeListener;
	}

	private int paddingBottom;//�ײ��߾�
	private int maxLines;//�����ʾ����
	public YZXCustomMsgEditText(Context context) {
		super(context);
	}

	public YZXCustomMsgEditText(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public YZXCustomMsgEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initAttrs(context,attrs,0);
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setFakeBoldText(false);
		mTextPaint.density = context.getResources().getDisplayMetrics().density;
		mTextPaint.setTextSize(textSize);
		mTextPaint.setColor(textColor);
		paddingBottom = getPaddingBottom();
	}
	
	private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray tArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YZXCustomMsgEditText, defStyleAttr, 0);
		
		for(int i=0;i<tArray.getIndexCount();i++){
			int attr = tArray.getIndex(i);
			switch (attr) {
			case R.styleable.YZXCustomMsgEditText_textSize:
				textSize = tArray.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP, 14, getResources()
										.getDisplayMetrics()));
				break;
			case R.styleable.YZXCustomMsgEditText_textColor:
				textColor = tArray.getColor(attr, 0);
				break;
			default:
				break;
			}
		}
		tArray.recycle();
	}
	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		//��Ϣ�ı������仯
		this.text = text;
		
		if(onTextChangeListener != null){
			onTextChangeListener.onTextChange(text, start, lengthBefore, lengthAfter);
		}
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.width = w;
		this.height = h;
		this.maxLines = (h - getPaddingTop() - getPaddingBottom()) / getLineHeight();
		Log.i(TAG, "�����ʾ = "+this.maxLines+"��");
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//����ʾ�ı�(10/200)
		drawText(canvas);
	}

	private void drawText(Canvas canvas) {
		int textSize = text.length();
		if(textSize > 200){
			textSize = 200;
		}
		String drawText = "("+textSize+"/200)";
		//�ı����
		int textWidth = (int) mTextPaint.measureText(drawText, 0, drawText.length());
		int textX = width - textWidth - getPaddingLeft();
		int textY = (int) (height - ((mTextPaint.descent() + mTextPaint.ascent()) / 2) - paddingBottom);
		if(getLineCount() > maxLines){
			//�����ǰ�ı�������������ı�������textYҪ+=����������*ÿ���ı��߶�
			textY += (getLineCount() - maxLines) * getLineHeight() + ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
		}
		canvas.drawText(drawText, textX, textY, mTextPaint);
	}
	
	public interface OnTextChangeListener{
		void onTextChange(CharSequence text, int start,
				int lengthBefore, int lengthAfter);
	}
}
