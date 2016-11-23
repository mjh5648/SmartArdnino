package com.mjh.smartardnino.fragment;

import android.view.View;
import android.widget.TextView;

import com.mjh.smartardnino.HomeActivity;
import com.mjh.smartardnino.R;
import com.mjh.smartardnino.utils.ConstantValue;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by MJH on 2016/11/18.
 */
@ContentView(R.layout.fragment_left_menu)
public class LeftMenuFragment extends BaseFragment implements View.OnClickListener{

    @ViewInject(R.id.tv_local)
    private TextView tv_local;
    @ViewInject(R.id.tv_remote)
    private TextView tv_remote;

    private HomeActivity mHome;

    @Override
    public void initView() {


    }

    @Override
    public void initData() {
        tv_local.setOnClickListener(this);
        tv_remote.setOnClickListener(this);
        mHome = (HomeActivity)mActivity;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_local:
                mHome.switchView(ConstantValue.SHOW_LOCAL_VIEW);
                break;
            case R.id.tv_remote:
                mHome.switchView(ConstantValue.SHOW_REMOTE_VIEW);
                break;
        }
    }
}
