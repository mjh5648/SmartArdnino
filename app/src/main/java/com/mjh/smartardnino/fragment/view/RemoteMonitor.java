package com.mjh.smartardnino.fragment.view;

import android.app.Activity;
import android.view.View;

import com.mjh.smartardnino.R;

import org.xutils.x;

/**
 * Created by MJH on 2016/11/18.
 */

public class RemoteMonitor extends BaseView {

    private View mMonitorView;

    public RemoteMonitor(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        if(mMonitorView ==null){
            mMonitorView = View.inflate(mActivity, R.layout.view_remote_monitor, null);
            x.view().inject(this, mMonitorView);
        }

        return mMonitorView;
    }

    @Override
    public void initData() {

    }
}
