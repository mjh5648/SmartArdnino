package com.mjh.smartardnino;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.mjh.smartardnino.fragment.LeftMenuFragment;
import com.mjh.smartardnino.fragment.LoaclContentFragment;
import com.mjh.smartardnino.fragment.RemoteContentFragment;
import com.mjh.smartardnino.fragment.view.LocalMonitor;
import com.mjh.smartardnino.utils.ConstantValue;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


@ContentView(R.layout.activity_home)
public class HomeActivity extends SlidingFragmentActivity {

    @ViewInject(R.id.iv_menu)
    ImageView iv_menu;
    @ViewInject(R.id.tv_title)
    TextView tv_title;
    private static final String TAG_CONTENT = "content_fragment";
    private static final String TAG_LEFTMENU_FRAGMENT = "content";
    private SlidingMenu mMenu;
    private FragmentManager mFM;
    private LoaclContentFragment mLoaclContentFragment;
    private RemoteContentFragment mRemoteContentFragment;
    private LeftMenuFragment mLeftMenuFragment;
    private MyReceiver myReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        intiSlidingMenu();
        initFragment();
        initTitle();
        initReciver();
    }

    private void initReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantValue.SHOW_LOCAL_MONITOR_VIEW);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, intentFilter);
    }

    private void initTitle() {
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.toggle();
            }
        });
    }

    private void initFragment() {
        mLoaclContentFragment = new LoaclContentFragment();
        mRemoteContentFragment = new RemoteContentFragment();
        mLeftMenuFragment = new LeftMenuFragment();
        mFM = getSupportFragmentManager();
        FragmentTransaction mFT = mFM.beginTransaction();
        mFT.replace(R.id.fl_content, mLoaclContentFragment, TAG_CONTENT);
        mFT.replace(R.id.fl_left_menu, mLeftMenuFragment, TAG_LEFTMENU_FRAGMENT);
        mFT.commit();
    }

    private void intiSlidingMenu() {
        setBehindContentView(R.layout.leftmenu);
        mMenu = getSlidingMenu();
        mMenu.setMode(SlidingMenu.RIGHT);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mMenu.setBehindOffset(screenWidth * 289 / 480);
        mMenu.setEnabled(true);
    }

    public void switchView(String showView) {
        FragmentTransaction mFT = mFM.beginTransaction();

        if (showView.equals(ConstantValue.SHOW_LOCAL_VIEW)) {

            mFT.replace(R.id.fl_content, mLoaclContentFragment, TAG_CONTENT);
            tv_title.setText(R.string.local_page_title);

        } else if (showView.equals(ConstantValue.SHOW_REMOTE_VIEW)) {

            mFT.replace(R.id.fl_content, mRemoteContentFragment, TAG_CONTENT);
            tv_title.setText(R.string.remote_page_title);

        }
        mFT.commit();
        mMenu.toggle();
    }

    @Override
    protected void onDestroy() {
        LocalMonitor.stopMonitor();
        if(myReceiver!=null){
            unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantValue.SHOW_LOCAL_MONITOR_VIEW)) {
                LoaclContentFragment temp = (LoaclContentFragment) mFM.findFragmentByTag(TAG_CONTENT);
                temp.switchView(action);
            }
        }
    }


}
