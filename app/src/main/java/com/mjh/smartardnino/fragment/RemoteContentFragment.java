package com.mjh.smartardnino.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.mjh.smartardnino.R;
import com.mjh.smartardnino.fragment.view.RemoteDevices;
import com.mjh.smartardnino.fragment.view.RemoteMonitor;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by MJH on 2016/11/18.
 */
@ContentView(R.layout.fragment_remote_content)
public class RemoteContentFragment extends BaseFragment implements View.OnClickListener{

    @ViewInject(R.id.btn_remote_monitor)
    Button btn_remote_monitor;
    @ViewInject(R.id.btn_remote_device)
    Button btn_remote_device;
    @ViewInject(R.id.fl_remote_detail)
    FrameLayout fl_remote_detail;

    private View mRemoteMonitorView;
    private View mRemoteDevicesView;


    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        btn_remote_monitor.setOnClickListener(this);
        btn_remote_device.setOnClickListener(this);
        mRemoteMonitorView = new RemoteMonitor(mActivity).initView();
        mRemoteDevicesView = new RemoteDevices(mActivity).initView();
        fl_remote_detail.removeAllViews();
        fl_remote_detail.addView(mRemoteMonitorView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_remote_monitor:
                fl_remote_detail.removeAllViews();
                fl_remote_detail.addView(mRemoteMonitorView);
                break;
            case R.id.btn_remote_device:
                fl_remote_detail.removeAllViews();
                fl_remote_detail.addView(mRemoteDevicesView);
                break;
        }
    }
}
