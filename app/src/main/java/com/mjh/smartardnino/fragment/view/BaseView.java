package com.mjh.smartardnino.fragment.view;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by MJH on 2016/11/18.
 */

public abstract class BaseView {

    Activity mActivity;

    public BaseView(Activity activity) {
        mActivity=activity;
    }

    abstract public View initView();

     public void initData(){};

    public void ToastMaker(String mes){
        Toast.makeText(mActivity,mes,Toast.LENGTH_SHORT).show();
    }

}
