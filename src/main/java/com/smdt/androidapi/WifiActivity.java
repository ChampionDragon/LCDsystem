package com.smdt.androidapi;

import android.app.Activity;
import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WifiActivity extends Activity implements OnClickListener{

	private Button open,close;
	private SmdtManager smdtManager;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.wifi);
		
		open = (Button)findViewById(R.id.btn_openwifi);
		close = (Button)findViewById(R.id.btn_closewifi);
		
		open.setOnClickListener(this);
		close.setOnClickListener(this);

		smdtManager = SmdtManager.create(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_openwifi:
			smdtManager.getWifiInterface(getApplicationContext()).wifiOpen();
			break;
		case R.id.btn_closewifi:
			smdtManager.getWifiInterface(getApplicationContext()).wifiClose();
			break;
		default:
			break;
		}
	}
	
}
