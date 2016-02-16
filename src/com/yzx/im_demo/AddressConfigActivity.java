package com.yzx.im_demo;

import com.yzx.mydefineview.YzxTopBar;
import com.yzx.tools.AddressTools;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 配置服务器地址界面
 * @author zhuqian
 *
 */
public class AddressConfigActivity extends Activity implements OnClickListener {
	
	private YzxTopBar topBar;
	private EditText as_address;
	private EditText as_port;
	private EditText tcp_address;
	private EditText tcp_port;
	private Button address_ok;
	private Button address_reset;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_config);
		
		initViews();
	}
	private void initViews() {
		topBar = (YzxTopBar) findViewById(R.id.actionBar_addressConfig);
		topBar.setTitle("服务器地址配置");
		topBar.setBackBtnOnclickListener(this);
		as_address = (EditText) findViewById(R.id.as_address);
		as_port = (EditText) findViewById(R.id.as_port);
		tcp_address = (EditText) findViewById(R.id.tcp_address);
		tcp_port = (EditText) findViewById(R.id.tcp_port);
		address_ok = (Button) findViewById(R.id.address_ok);
		address_reset = (Button) findViewById(R.id.address_reset);
		
		String asAddressAndPort = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_ASADDRESS", "");
		String tcpAddressAndPort = getSharedPreferences("YZX_DEMO_DEFAULT", 0).getString("YZX_CSADDRESS", "");
		
		if(!TextUtils.isEmpty(asAddressAndPort)){
			if(asAddressAndPort.split(":").length == 2){
				as_address.setText(asAddressAndPort.split(":")[0]);
				as_port.setText(asAddressAndPort.split(":")[1]);
			}
		}else{
			as_port.setText("");
			as_address.setText("");
		}
		if(!TextUtils.isEmpty(tcpAddressAndPort)){
			if(tcpAddressAndPort.split(":").length == 2){
				tcp_address.setText(tcpAddressAndPort.split(":")[0]);
				tcp_port.setText(tcpAddressAndPort.split(":")[1]);
			}
		}else{
			tcp_address.setText("");
			tcp_port.setText("");
		}
		address_ok.setOnClickListener(this);
		address_reset.setOnClickListener(this);
		
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imbtn_back:
			//返回
			finish();
			break;
		case R.id.address_ok:
			//save
			String asAddress = as_address.getText().toString();
			String asPort = as_port.getText().toString();
			String tcpAddress = tcp_address.getText().toString();
			String tcpPort = tcp_port.getText().toString();
			if(TextUtils.isEmpty(asAddress) || TextUtils.isEmpty(asPort) ||
					TextUtils.isEmpty(tcpAddress) || TextUtils.isEmpty(tcpPort)){
				Toast.makeText(this, "地址或者端口不能为空", 0).show();
				return;
			}
			if(!AddressTools.isPort(asPort) || !AddressTools.isPort(tcpPort)){
				Toast.makeText(this, "您输入的端口不合法", 0).show();
				return;
			}
			if(!AddressTools.isNetAddress(asAddress) || !AddressTools.isNetAddress(tcpAddress)){
				Toast.makeText(this, "您输入的地址不合法", 0).show();
				return;
			}
			getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().putString("YZX_ASADDRESS", asAddress+":"+asPort).commit();
			getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().putString("YZX_CSADDRESS", tcpAddress+":"+tcpPort).commit();
			Toast.makeText(this, "保存成功", 0).show();
			break;
		case R.id.address_reset:
			//reset
			getSharedPreferences("YZX_DEMO_DEFAULT", 0).edit().clear().commit();
			Toast.makeText(this, "清除成功", 0).show();
			as_address.setText("");
			as_port.setText("");
			tcp_port.setText("");
			tcp_address.setText("");
			break;
		default:
			break;
		}
	}
}
