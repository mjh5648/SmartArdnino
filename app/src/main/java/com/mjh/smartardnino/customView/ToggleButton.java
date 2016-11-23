package com.mjh.smartardnino.customView;

/**
 * Created by MJH on 2016/11/21.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 老公 on 2016/11/15.
 */

public class ToggleButton extends View {
    Bitmap switch_background;
    Bitmap slide_buttonOn;
    Bitmap slide_buttonOff;
    private Paint mPaint;
    private float mStartX;
    private float mDisX;
    String TAG = "MJH";
    private boolean mSwitchState;
    private boolean mPreSwitchState;
    private float mSlidX;
    private float mDistance;
    private boolean mFirstDraw = true;
    private SwitchStateChangeListener mSwitchStateChangeListener=null;



    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
        initData();
    }

    private void initData() {

    }

    private void initUI() {

    }

    public void setBackgroundBitmap(int switch_background) {

        this.switch_background = BitmapFactory.decodeResource(getResources(), switch_background);
    }

    public void setSwitchOnBitmap(int slide_buttonOnID) {
        this.slide_buttonOn = BitmapFactory.decodeResource(getResources(), slide_buttonOnID);
    }

    public void setSwitchOffBitmap(int slide_buttonOffID) {
        this.slide_buttonOff = BitmapFactory.decodeResource(getResources(), slide_buttonOffID);
    }

    public void setSwitchState(boolean state) {
        mSwitchState = state;
        if (!mFirstDraw) {
            if (mSwitchState) {
                mSlidX = mDistance;
            } else {
                mSlidX = 0;
            }
            mDisX=0;
            invalidate();
        }
    }

    public boolean getSwitchState() {
        return mSwitchState;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(switch_background.getWidth(), switch_background.getHeight());
        mDistance = switch_background.getWidth() - slide_buttonOn.getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint();
        canvas.drawBitmap(switch_background, 0, 0, mPaint);
        if (mFirstDraw) {
            if (mSwitchState) {
                mSlidX = mDistance;

            } else {
                mSlidX = 0;
            }
            mFirstDraw = false;
        }

        mSlidX = mDisX + mSlidX;
        if(mSwitchState){
            canvas.drawBitmap(slide_buttonOn, mSlidX, 0, mPaint);
        }else{
            canvas.drawBitmap(slide_buttonOff, mSlidX, 0, mPaint);
        }
        if(!mSwitchState==mPreSwitchState){
            mSwitchStateChangeListener.onStateChange(mSwitchState);
        }
        mPreSwitchState=mSwitchState;
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                Log.d(TAG, "mStartX: "+mStartX);
                Log.d(TAG, "mSwitchState: "+mSwitchState);
                if(mSwitchState){
                    if(mStartX<mDistance) {
                        setSwitchState(false);
                        return false;
                    }
                }else{
                    if(mStartX> slide_buttonOn.getWidth()){
                        setSwitchState(true);
                        invalidate();
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = event.getX();
                mDisX = endX - mStartX;
                mStartX = endX;
                if (mSlidX < 0) {
                    mSlidX = 0;
                    mDisX=0;
                    break;
                }
                if (mSlidX > mDistance) {
                    mSlidX = mDistance;
                    mDisX=0;
                    break;
                }

                invalidate();
                break;

            case MotionEvent.ACTION_UP:

                if (mSlidX < mDistance / 2&&mStartX>0) {
                    mSlidX = 0;
                    mSwitchState=false;
                }
                if (mSlidX > mDistance / 2) {
                    mSlidX = mDistance;
                    mSwitchState=true;
                }

                invalidate();

                break;
        }
        return true;
    }

    public void setOnSwitchStateChangeListener(SwitchStateChangeListener listener){
        mSwitchStateChangeListener=listener;
    }

    public  interface SwitchStateChangeListener{
        public void onStateChange(boolean state);
    }
}
