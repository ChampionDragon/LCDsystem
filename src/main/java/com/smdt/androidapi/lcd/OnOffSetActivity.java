package com.smdt.androidapi.lcd;

import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.smdt.androidapi.R;
import com.smdt.androidapi.base.BaseActivity;
import com.smdt.androidapi.base.BaseApplication;
import com.smdt.androidapi.pickerview.OptionsPickerView;
import com.smdt.androidapi.pickerview.TimePickerView;
import com.smdt.androidapi.pickerview.other.pickerViewUtil;
import com.smdt.androidapi.utils.Constant;
import com.smdt.androidapi.utils.Logs;
import com.smdt.androidapi.utils.TimeUtil;
import com.smdt.androidapi.utils.ToastUtil;

import java.util.ArrayList;

public class OnOffSetActivity extends BaseActivity implements View.OnClickListener {
    private TextView onSet, offSet, onInterval, offInterval;
    private Switch onOffSet, onOffinterval;
    private SmdtManager smdt;
    String tag = "OnOffSetActivity";
    private ArrayList<String> hours = new ArrayList<>();
    private ArrayList<ArrayList<String>> minutes = new ArrayList<>();
    int onHour, onMinutes, offHour, offMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_off_set);
        initView();
        smdt = SmdtManager.create(getApplicationContext());
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onOffinterval.setChecked(
                BaseApplication.sp.getBoolean(Constant.onOffinterval));
        onOffSet.setChecked(
                BaseApplication.sp.getBoolean(Constant.onOffSet));

    }

    /*初始化时间数据,初步设定时间间隔可选的范围为:0小时0分~~~~59小时59分*/
    private void initData() {
        for (int i = 0; i < 60; i++) {
            hours.add(i + "");
        }
        minutes.add(hours);
        String time = TimeUtil.getSystem("HH:mm");
        String[] split = time.split(":");
        onHour = Integer.valueOf(split[0]);
        offHour = Integer.valueOf(split[0]);
        onMinutes = Integer.valueOf(split[1]);
        offMinute = Integer.valueOf(split[1]);
    }

    private void initView() {
        onSet = (TextView) findViewById(R.id.tv_onset);
        onSet.setOnClickListener(this);
        offSet = (TextView) findViewById(R.id.tv_offset);
        offSet.setOnClickListener(this);
        onInterval = (TextView) findViewById(R.id.tv_oninterval);
        offInterval = (TextView) findViewById(R.id.tv_offinterval);
        onInterval.setOnClickListener(this);
        offInterval.setOnClickListener(this);
        findViewById(R.id.onoff_save).setOnClickListener(this);
        onOffSet = (Switch) findViewById(R.id.switch_onoffset);
        onOffSet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onOffinterval.setChecked(false);
                }
            }
        });
        onOffinterval = (Switch) findViewById(R.id.switch_onoffinterval);
        onOffinterval.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onOffSet.setChecked(false);
                }
            }
        });
        onSet.setText(TimeUtil.getSystem("HH:mm"));
        offSet.setText(TimeUtil.getSystem("HH:mm"));
        onInterval.setText(TimeUtil.getSystem("HH:mm"));
        offInterval.setText(TimeUtil.getSystem("HH:mm"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_onset:
                OnSet();
                break;
            case R.id.tv_offset:
                OffSet();
                break;
            case R.id.tv_oninterval:
                onInterval();
                break;
            case R.id.tv_offinterval:
                offInterval();
                break;
            case R.id.onoff_save:
                SaveOnoff();
                break;
        }

    }

    private void SaveOnoff() {
        if (onOffinterval.isChecked()) {
            onOffinterval(3);
        } else if (onOffSet.isChecked()) {
            onOffSet(1);
        } else {
            onOffinterval(0);
            onOffSet(0);
        }
        ToastUtil.showLong("设置成功");

        BaseApplication.sp.putBoolean(Constant.onOffSet, onOffSet.isChecked());
        BaseApplication.sp.putBoolean(Constant.onOffinterval, onOffinterval.isChecked());

        finish();
    }

    private void onOffSet(int i) {
        smdt.smdtSetTimingSwitchMachine(offSet.getText().toString(),
                onSet.getText().toString(), i + "");
        Logs.d(tag + 114 + "  " + offSet.getText().toString() + "  " + onSet.getText().toString() + "  " + i);

    }

    private void onOffinterval(int i) {
        int ii = smdt.smdtSetPowerOnOff((char) offHour, (char) offMinute, (char) onHour, (char) onMinutes, (char) i);
//        int ii = smdt.smdtSetPowerOnOff((char)0, (char)2, (char)0, (char)2, (char)3);
        Logs.e(tag + 120 + "  " + offHour + "  " + offMinute + "  " + onHour + "  " + onMinutes + "  " + i);
        Logs.v("设置结果： " + ii);

    }

    private void offInterval() {
        String[] labels = {"小时", "分钟"};
        new pickerViewUtil().twoPicker(this, "设置关机的时间间隔", new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                offHour = Integer.parseInt(hours.get(options1));
                offMinute = Integer.parseInt(minutes.get(0).get(option2));
                offInterval.setText(offHour + ":" + offMinute);
            }
        }, hours, minutes, labels, false);


    }

    private void onInterval() {
        String[] labels = {"小时", "分钟"};
        new pickerViewUtil().twoPicker(this, "设置开机的时间间隔", new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                onHour = Integer.parseInt(hours.get(options1));
                onMinutes = Integer.parseInt(minutes.get(0).get(option2));
                onInterval.setText(onHour + ":" + onMinutes);
            }
        }, hours, minutes, labels, false);
    }

    private void OffSet() {
        pickerViewUtil.alertTimerPicker(OnOffSetActivity.this, TimePickerView.Type.HOURS_MINS, "HH:mm",
                "设置关机时间", 33, new pickerViewUtil.TimerPickerCallBack() {
                    @Override
                    public void onTimeSelect(String date) {
                        offSet.setText(date);
                    }
                });
    }

    private void OnSet() {
        pickerViewUtil.alertTimerPicker(OnOffSetActivity.this, TimePickerView.Type.HOURS_MINS, "HH:mm",
                "设置开机时间", 33, new pickerViewUtil.TimerPickerCallBack() {
                    @Override
                    public void onTimeSelect(String date) {
                        onSet.setText(date);
                    }
                });
    }


}
