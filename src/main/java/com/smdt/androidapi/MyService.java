package com.smdt.androidapi;

import android.app.Service;
import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service{
	private SmdtManager smdt;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		smdt= SmdtManager.create(this);
		smdt.smdtSetExtrnalGpioValue(1, true);
	}
}
