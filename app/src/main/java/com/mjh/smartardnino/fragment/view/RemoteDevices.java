package com.mjh.smartardnino.fragment.view;

import android.app.Activity;
import android.view.View;

import com.mjh.smartardnino.R;

import org.xutils.x;

/**
 * Created by MJH on 2016/11/18.
 */

public class RemoteDevices extends BaseView{

    View mDevicesView;

    public RemoteDevices(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        if(mDevicesView==null){
            mDevicesView = View.inflate(mActivity, R.layout.view_remote_devices,null);
            x.view().inject(this,mDevicesView);
        }

        return mDevicesView;
    }

    @Override
    public void initData() {

    }
}
