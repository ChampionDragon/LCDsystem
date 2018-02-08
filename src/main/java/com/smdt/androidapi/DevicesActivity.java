package com.smdt.androidapi;

import android.app.Activity;
import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.widget.TextView;

public class DevicesActivity extends Activity {

    private TextView model, Android_version, running, inner, firware, kernel, version;
    private SmdtManager smdt = null;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.devices);

        model = (TextView) findViewById(R.id.model);
        Android_version = (TextView) findViewById(R.id.Android_version);
        running = (TextView) findViewById(R.id.running);
        inner = (TextView) findViewById(R.id.inner);
        firware = (TextView) findViewById(R.id.firware);
        kernel = (TextView) findViewById(R.id.kernel);
        version = (TextView) findViewById(R.id.version);
        smdt = SmdtManager.create(this);

        model.setText("型号\n" + smdt.getAndroidModel());
        Android_version.setText("Android版本\n" + smdt.getAndroidVersion());
        running.setText("运行内存\n" + smdt.getRunningMemory());
        inner.setText("内部存储\n" + smdt.getInternalStorageMemory());
        firware.setText("固件版本\n" + smdt.getFirmwareVersion());
        kernel.setText("内核版本\n" + smdt.getFormattedKernelVersion());
        version.setText("版本号\n" + smdt.getAndroidDisplay());

    }

}
