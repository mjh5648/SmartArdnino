package com.mjh.smartardnino.fragment.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mjh.smartardnino.R;
import com.mjh.smartardnino.utils.BT;
import com.mjh.smartardnino.utils.ConstantValue;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MJH on 2016/11/18.
 */

public class LocalDevices extends BaseView implements View.OnClickListener {

    @ViewInject(R.id.tv_current_connected_device)
    private TextView tv_cur_conn_dev;
    @ViewInject(R.id.lv_local_dev)
    private ListView lv_local_dev;
    @ViewInject(R.id.btn_scan)
    private Button btn_scan;
    @ViewInject(R.id.btn_shut_bt)
    private Button btn_shut_bt;
    @ViewInject(R.id.btn_stop_scan)
    private Button btn_stop_scan;
    //    @ViewInject(R.id.tb_scan)
//    private ToggleButton tb_scan;
    private View mDevicesView;
    private List<BluetoothDevice> mBTDevicesList;
    private MyAdapter myAdapter;
    private BT mBT;

    public LocalDevices(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        if (mDevicesView == null) {
            mDevicesView = View.inflate(mActivity, R.layout.view_loacl_devices, null);
            x.view().inject(this, mDevicesView);
            btn_scan.setOnClickListener(this);
            btn_shut_bt.setOnClickListener(this);
            btn_stop_scan.setOnClickListener(this);
            mBTDevicesList = new ArrayList<BluetoothDevice>();
            myAdapter = new MyAdapter();
            lv_local_dev.setAdapter(myAdapter);
            initBT();
            initListView();
        }
        return mDevicesView;
    }


    private void initListView() {
        lv_local_dev.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBT.getBTState() == BluetoothAdapter.STATE_ON) {
                    showtDialog(mBTDevicesList.get(position));
                } else {
                    ToastMaker("蓝牙未打开");
                }
            }
        });

    }

    private void showtDialog(final BluetoothDevice device) {
        final String name = device.getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("提示");
        builder.setMessage("确认绑定设备" + name + "么？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tv_cur_conn_dev.setText(name);
                mBT.connection(device);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void initBT() {
        mBT = BT.getBT(mActivity);

        if (mBT.getBTState() == BluetoothAdapter.STATE_ON) {
            if (!TextUtils.isEmpty(mBT.getRemoteDeviceName())) {
                tv_cur_conn_dev.setText(mBT.getRemoteDeviceName());
            }
        }

        mBT.setDevListChangeListener(new BT.DevListChangeListener() {
            @Override
            public void findBT(BluetoothDevice BTDevice) {
                mBTDevicesList.add(BTDevice);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void connBT(String BTDevice) {
                tv_cur_conn_dev.setText(BTDevice);
                if(!TextUtils.isEmpty(BTDevice)){
                    mActivity.sendBroadcast(new Intent(ConstantValue.SHOW_LOCAL_MONITOR_VIEW));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                ToastMaker("开始扫描");
                mBT.stopScan();
                mBTDevicesList.clear();
                mBT.scan();

                break;
            case R.id.btn_stop_scan:
                ToastMaker("关闭扫描");
                mBT.stopScan();
                break;
            case R.id.btn_shut_bt:
                mBT.shutDownBT();
                mBTDevicesList.clear();
                myAdapter.notifyDataSetChanged();
                ToastMaker("正在关闭蓝牙设备");
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBTDevicesList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return mBTDevicesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_local_devices, null);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_devices_name.setText(getItem(position).getName());
            return convertView;
        }
    }

    class ViewHolder {
        @ViewInject(R.id.tv_devices_name)
        TextView tv_devices_name;
    }

}
