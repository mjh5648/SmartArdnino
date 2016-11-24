package com.mjh.smartardnino.fragment.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mjh.smartardnino.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by MJH on 2016/11/18.
 */

public class RemoteMonitor extends BaseView implements View.OnClickListener {

    @ViewInject(R.id.tv_remote_humidity)
    TextView tv_remote_humidity;
    @ViewInject(R.id.tv_remote_temperature)
    TextView tv_remote_temperature;
    @ViewInject(R.id.btn_remote_monitor_on)
    Button btn_remote_monitor_on;
    @ViewInject(R.id.btn_remote_monitor_off)
    Button btn_remote_monitor_off;
    @ViewInject(R.id.tv_remote_monitor_devices)
    TextView tv_remote_monitor_devices;

    private View mMonitorView;
    private boolean isMoniting = false;
    private String url = "http://1587w8o804.iask.in/";
    private InputStream inputStream;
    private String mStrT;
    private String mStrH;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    tv_remote_temperature.setText(mStrT);
                    tv_remote_humidity.setText(mStrH);
                    break;
            }
        }
    };



    public RemoteMonitor(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        if (mMonitorView == null) {
            mMonitorView = View.inflate(mActivity, R.layout.view_remote_monitor, null);
            x.view().inject(this, mMonitorView);
            btn_remote_monitor_on.setOnClickListener(this);
            btn_remote_monitor_off.setOnClickListener(this);
            btn_remote_monitor_off.setEnabled(false);

        }

        return mMonitorView;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_remote_monitor_on:
                isMoniting = true;
                btn_remote_monitor_on.setEnabled(false);
                btn_remote_monitor_off.setEnabled(true);
                tv_remote_monitor_devices.setText("当前连接：" + url);
                new Thread() {
                    @Override
                    public void run() {
                        startMoniror();
                    }
                }.start();


                break;
            case R.id.btn_remote_monitor_off:
                stopMoniror();
                btn_remote_monitor_on.setEnabled(true);
                btn_remote_monitor_off.setEnabled(false);
                tv_remote_monitor_devices.setText("当前连接：");
                break;
        }


    }

    private void startMoniror() {

        while (isMoniting) {


            ByteArrayOutputStream byArrayOutputStream = new ByteArrayOutputStream();

            try {

                URL uri = new URL("http://1587w8o804.iask.in/");


                HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                System.out.println(connection.getResponseCode());
                if (connection.getResponseCode() == (200)) {
                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != (-1)) {
                        byArrayOutputStream.write(buffer, 0, len);
                    }
                    String result = byArrayOutputStream.toString();
                    System.out.println(result);
                    int indexT = result.indexOf("℃");
                    int indexH = result.indexOf('%');
                    mStrT = result.substring(indexT - 2, indexT+1);
                    mStrH = result.substring(indexH - 2, indexH+1);
                    System.out.println(mStrH);
                    System.out.println(mStrT);
                    mHandler.sendEmptyMessage(0);
                 }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        byArrayOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void stopMoniror() {

        isMoniting = false;
    }
}
