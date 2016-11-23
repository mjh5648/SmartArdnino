package com.mjh.smartardnino.fragment.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mjh.smartardnino.R;
import com.mjh.smartardnino.utils.BT;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by MJH on 2016/11/18.
 */

public class LocalMonitor extends BaseView implements View.OnClickListener {

    @ViewInject(R.id.btn_relay_on)
    private Button btn_relay_on;
    @ViewInject(R.id.btn_relay_off)
    private Button btn_relay_off;
    @ViewInject(R.id.tv_local_humidity)
    private TextView tv_local_humidity;
    @ViewInject(R.id.tv_local_temperature)
    private TextView tv_local_temperature;
    @ViewInject(R.id.btn_monitor_on)
    private Button btn_monitor_on;
    @ViewInject(R.id.btn_monitor_off)
    private Button btn_monitor_off;
    @ViewInject(R.id.tv_local_monitor_devices)
    private TextView tv_local_monitor_devices;

    private View mMonitorView;
    private BT mBT;
    private static boolean MonitorState=false;


    public LocalMonitor(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        if(mMonitorView ==null){
            mMonitorView = View.inflate(mActivity, R.layout.view_loacl_monitor, null);
            x.view().inject(this, mMonitorView);
            btn_relay_on.setOnClickListener(this);
            btn_relay_off.setOnClickListener(this);
            btn_monitor_on.setOnClickListener(this);
            btn_monitor_off.setOnClickListener(this);
            setBtnState(false);
            initBT();
        }
        return mMonitorView;
    }

    private void initBT() {
        System.out.println("initBT");
        mBT = BT.getBT(mActivity);

        if (mBT.getBTState() == BluetoothAdapter.STATE_ON) {
            if (!TextUtils.isEmpty(mBT.getRemoteDeviceName())) {
                tv_local_monitor_devices.setText("当前已连接设备："+mBT.getRemoteDeviceName());
                setBtnState(true);
            }
        }

        mBT.setDataListener(new BT.DataListener() {
            @Override
            public void processData(String strRecv) {
                if(strRecv.contains("T")&&strRecv.contains("H")){
                    tv_local_humidity.setText(strRecv.substring(1,3)+"%");
                    tv_local_temperature.setText(strRecv.substring(4,6)+"℃");
                }
            }
        });
       mBT.setDevConnListener(new BT.DevConnListener() {
           @Override
           public void connDev(String DevName) {
               if(!TextUtils.isEmpty(DevName)){
                   tv_local_monitor_devices.setText("当前已连接设备："+DevName);
                   setBtnState(true);
               }else{
                   tv_local_monitor_devices.setText("");
                   stopMonitor();
                   ToastMaker("和设备断开了，，，");
                   tv_local_temperature.setText("已断开");
                   tv_local_humidity.setText("已断开");
                   setBtnState(false);
               }
           }
       });
    }
    private void setBtnState(boolean state){
        btn_monitor_off.setEnabled(state);
        btn_monitor_on.setEnabled(state);
        btn_relay_on.setEnabled(state);
        btn_relay_off.setEnabled(state);
    }
    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_relay_on:
                mBT.writeData("1");
                break;
            case R.id.btn_relay_off:
                mBT.writeData("2");
                break;
            case R.id.btn_monitor_on:
                if(MonitorState){
                    ToastMaker("实时监控已开启");
                }else{
                    startMonitor();
                 }
                break;
            case R.id.btn_monitor_off:
                stopMonitor();
                break;


        }
    }

    public static void stopMonitor() {
        MonitorState=false;
    }

    private void startMonitor() {
        MonitorState=true;
        new Thread(){
            @Override
            public void run() {
                while(MonitorState){
                    mBT.writeData("3");
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_local_temperature.setText("已断开");
                        tv_local_humidity.setText("已断开");
                    }
                });
            }
        }.start();

    }
}
