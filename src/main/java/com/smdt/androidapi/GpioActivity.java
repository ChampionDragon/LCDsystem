package com.smdt.androidapi;

import android.app.Activity;
import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GpioActivity extends Activity implements OnClickListener{

	private Button btn_readGpio,btn_writeGpio;
	private EditText tv_gpioNum;
	private EditText tv_gpioValue;
	private SmdtManager smdtManager;
	private int gpioNum = 0;
	private boolean bGpioValue;
	private int ret;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.gpio);
		
		tv_gpioNum = (EditText)findViewById(R.id.tv_gpioNum);
		btn_readGpio = (Button)findViewById(R.id.btn_readGpio);
		btn_writeGpio = (Button)findViewById(R.id.btn_writeGpio);
		tv_gpioValue = (EditText)findViewById(R.id.tv_gpioValue);
				
		btn_readGpio.setOnClickListener(this);
		btn_writeGpio.setOnClickListener(this);
	
		smdtManager = SmdtManager.create(this);
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){		
		case R.id.btn_readGpio:
			if (tv_gpioNum.getText().toString().equals(""))
			{
				Toast.makeText(getApplicationContext(), "gpio number is null", Toast.LENGTH_SHORT).show();
				return;
			}
			
			gpioNum = Integer.parseInt(tv_gpioNum.getText().toString());			
			if ((gpioNum < 1) || (gpioNum > 10))
			{
				Toast.makeText(getApplicationContext(), "gpio number is not 1~10", Toast.LENGTH_SHORT).show();
				return;
			}
					
			//smdtManager.smdtReadExtrnalGpioValue(gpioNum);
			Toast.makeText(getApplicationContext(), smdtManager.smdtReadExtrnalGpioValue(gpioNum) + "", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_writeGpio:
			if (tv_gpioNum.getText().toString().equals("") || tv_gpioValue.getText().toString().equals(""))
			{
				Toast.makeText(getApplicationContext(), "gpio number or value is null", Toast.LENGTH_SHORT).show();
				return;
			}
			
			gpioNum = Integer.parseInt(tv_gpioNum.getText().toString());			
			if (gpioNum < 1 || gpioNum > 10)
			{
				Toast.makeText(getApplicationContext(), "gpio number is not 1~10", Toast.LENGTH_SHORT).show();
				return;
			}
			
			bGpioValue = (Integer.parseInt(tv_gpioValue.getText().toString())==1)?true:false;
			
			ret = smdtManager.smdtSetExtrnalGpioValue(gpioNum, bGpioValue);
			Log.d("lzl", "=========smdtSetExtrnalGpioValue is return " + ret + "=============");
			//Toast.makeText(getApplicationContext(), smdtManager.smdtSetExtrnalGpioValue(gpioNum, bGpioValue) + "", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

}
