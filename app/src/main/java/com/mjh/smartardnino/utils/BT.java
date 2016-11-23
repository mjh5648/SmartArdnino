package com.mjh.smartardnino.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 * Created by MJH on 2016/11/18.
 * 加两个回调接口：
 * 1.用来更新传感器的试试数据
 * 2.用来更新蓝牙设备的连接情况；这个需求，具体回到什么api还未知
 */

public class BT {


    private BluetoothAdapter mBluetoothAdapter;
    private MyBluetoothReceiver myBluetoothReceiver;
    private Activity mActivity;
    private DevListChangeListener mDevListChangeListener;
    private Handler mHandler;
    private BluetoothDevice mdevice;
    private static BT mBT;
    public BluetoothDevice pairedDev;
    private BluetoothSocket mBtsocket;
    private final int MES_FIND_DEV = 0;
    private final int MES_BT_NOT_IN_PHONE = 1;
    private final int MES_CONN_DEV = 2;
    private final int MES_CONN_ERROR = 3;
    private static final int MES_DATA_CONMING = 4;
    private int mMsgLength;
    private byte[] mBs;
    private boolean mState;
    private DataListener mDataListener;
    private DevConnListener mDevConnListener;


    private BT(Activity activity) {
        mActivity = activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myBluetoothReceiver = new MyBluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // mActivity.registerReceiver(myBluetoothReceiver, intentFilter);
        mBluetoothAdapter.disable();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MES_FIND_DEV:
                        if (mdevice != null) {
                            mDevListChangeListener.findBT(mdevice);
                        }
                        break;
                    case MES_BT_NOT_IN_PHONE:
                        ToastMaker("设备无蓝牙");
                        break;
                    case MES_CONN_DEV:
                        ToastMaker("当前设备已绑定");
                        mDevListChangeListener.connBT(mdevice.getName());
                        mDevConnListener.connDev(mdevice.getName());
                        break;

                    case MES_CONN_ERROR:
                        ToastMaker("无法绑定设备");
                        mDevListChangeListener.connBT("");
                        mDevConnListener.connDev("");
                        break;

                    case  MES_DATA_CONMING:
                        mDataListener.processData((String) msg.obj);
                        break;
                }
            }
        };
    }

    public static BT getBT(Activity activity) {
        if (mBT == null) {
            mBT = new BT(activity);
        }
        return mBT;
    }

    public void scan() {

        new Thread() {
            @Override
            public void run() {

                if (mBluetoothAdapter == null) {
                    mHandler.sendEmptyMessage(MES_BT_NOT_IN_PHONE);
                } else {

                    if (!mBluetoothAdapter.enable()) {
                        mBluetoothAdapter.enable();
                    }
                    while (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {

                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                            break;
                        }
                    }

                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    // If there are paired devices
//                    System.out.println("pairedDevices.size()" + pairedDevices.size());
                    if (pairedDevices.size() > 0) {
                        // Loop through paired devices
                        for (BluetoothDevice device : pairedDevices) {
                            // Add the name and address to an array adapter to show in a ListView
//                            System.out.println("getName" + device.getName());
//                            System.out.println("getAddress" + device.getAddress());
                            // TODO: 2016/11/19 回调
                            mdevice = device;
                            mHandler.sendEmptyMessage(MES_FIND_DEV);
                            try {
                                Thread.currentThread().sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    mBluetoothAdapter.startDiscovery();
                }

            }
        }.start();
    }

    public void stopScan() {
        // TODO: 2016/11/18 stop scan
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

    }

    public void shutDownBT() {
        // TODO: 2016/11/18 shut down devices
        mBluetoothAdapter.disable();
    }

    public void writeData(final String mes) {

        // TODO: 2016/11/18 write data
        new Thread() {
            @Override
            public void run() {
                OutputStream OS = null;
                try {
                    OS = mBtsocket.getOutputStream();
                    OS.write(mes.getBytes());
                } catch (IOException e) {
                    mState=false;
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void readData() {
        // TODO: 2016/11/18 read data
        if (mBtsocket.isConnected()) {

            new Thread() {
                @Override
                public void run() {
                    InputStream IS = null;
                    try {
                        IS = mBtsocket.getInputStream();
                        mState = mBtsocket.isConnected();
                        while (mState) {
                            mMsgLength = IS.available();

                            while (mMsgLength > 0) {
                                char[] tChars=new char[mMsgLength];
                                StringBuffer sb=new StringBuffer();
                                mBs = new byte[mMsgLength];
                                IS.read(mBs);
                                for (int i = 0; i <mMsgLength; i++) {
                                    tChars[i]=(char)mBs[i];
                                }
                                String mStrRecv = new String(tChars);
                                if(mStrRecv.contains("H")||mStrRecv.contains("T")){
                                    Message message = Message.obtain(mHandler);
                                    message.obj=mStrRecv;
                                    message.what=MES_DATA_CONMING;
                                    mHandler.sendMessage(message);
                                }
                                mMsgLength = IS.available();
                            }
                            Thread.currentThread().sleep(500);
                            writeData("0");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("链接断开。。。。。");
                        mHandler.sendEmptyMessage(MES_CONN_ERROR);
                        if (IS != null) {
                            try {
                                IS.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
        } else {
            mHandler.sendEmptyMessage(MES_CONN_ERROR);
        }
    }

    public void connection(BluetoothDevice device) {
        mdevice = device;
        new Thread() {
            @Override
            public void run() {
                if (mBtsocket != null) {

                    if (mBtsocket.isConnected()) {
//                        System.out.println("mBtsocket.getRemoteDevice()" + mBtsocket.getRemoteDevice());
//                        System.out.println("mdevice" + mdevice);
                        if (mBtsocket.getRemoteDevice() == mdevice) {
                            mHandler.sendEmptyMessage(MES_CONN_DEV);
                            return;
                        } else {
                            try {
                                mBtsocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                try {
                    mBtsocket = mdevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    mBtsocket.connect();
                    System.out.println("connected");
                    readData();
                    mHandler.sendEmptyMessage(MES_CONN_DEV);
                } catch (IOException e) {
                    System.out.println("链接错误");
                    mHandler.sendEmptyMessage(MES_CONN_ERROR);
                    e.printStackTrace();
                }

            }
        }.start();


    }

    class MyBluetoothReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(intent.getAction());
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mdevice = device;
                mHandler.sendEmptyMessage(MES_FIND_DEV);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                System.out.println("开始扫描");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                ToastMaker("扫描结束");
            }
        }
    }

    public void ToastMaker(String mes) {
        Toast.makeText(mActivity, mes, Toast.LENGTH_SHORT).show();
    }


    public void setDevListChangeListener(DevListChangeListener changeListener) {
        mDevListChangeListener = changeListener;
    }

    public interface DevListChangeListener {
        void findBT(BluetoothDevice BTDevice);
        void connBT(String BTDevice);
    }
    public interface DataListener {
        void processData(String strRecv);
    }

    public interface DevConnListener {
        void connDev(String DevName);
    }
   public void setDataListener( DataListener dataListener) {
       mDataListener = dataListener;
    }

    public void setDevConnListener( DevConnListener devConnListener) {
        mDevConnListener = devConnListener;
    }

    public int getBTState(){

        return mBluetoothAdapter.getState();
    }
    public String getRemoteDeviceName() {
        if(mBtsocket==null||mBtsocket.getRemoteDevice()==null){
            return "";
        }
        String name = mBtsocket.getRemoteDevice().getName();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        return null;
    }

}
