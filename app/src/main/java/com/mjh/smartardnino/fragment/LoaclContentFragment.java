package com.mjh.smartardnino.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mjh.smartardnino.R;
import com.mjh.smartardnino.fragment.view.LocalDevices;
import com.mjh.smartardnino.fragment.view.LocalMonitor;
import com.mjh.smartardnino.utils.ConstantValue;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import static android.R.attr.name;

/**
 * Created by MJH on 2016/11/18.
 */
@ContentView(R.layout.fragment_local_content)
public class LoaclContentFragment extends BaseFragment implements View.OnClickListener{
    @ViewInject(R.id.btn_local_monitor)
    Button btn_local_monitor;
    @ViewInject(R.id.btn_local_device)
    Button btn_local_device;
    @ViewInject(R.id.fl_loacl_detail)
    FrameLayout fl_loacl_detail;

    private View mLocalMonitorView;
    private View mLocalDevicesView;


    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        btn_local_monitor.setOnClickListener(this);
        btn_local_device.setOnClickListener(this);
        mLocalMonitorView = new LocalMonitor(mActivity).initView();
        mLocalDevicesView = new LocalDevices(mActivity).initView();
        fl_loacl_detail.removeAllViews();
        fl_loacl_detail.addView(mLocalMonitorView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_local_monitor:
                switchView(ConstantValue.SHOW_LOCAL_MONITOR_VIEW);
                break;
            case R.id.btn_local_device:
                switchView(ConstantValue.SHOW_LOCAL_DEVICES_VIEW);
                break;
        }
    }
    public void switchView(String action){
        fl_loacl_detail.removeAllViews();
        if(action.equals(ConstantValue.SHOW_LOCAL_MONITOR_VIEW)){
            fl_loacl_detail.removeAllViews();
            fl_loacl_detail.addView(mLocalMonitorView);
            return;
        }
        if(action.equals(ConstantValue.SHOW_LOCAL_DEVICES_VIEW)){
            fl_loacl_detail.removeAllViews();
            fl_loacl_detail.addView(mLocalDevicesView);
            return;
        }


    }

}
