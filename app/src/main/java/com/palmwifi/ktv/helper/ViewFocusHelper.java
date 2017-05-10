package com.palmwifi.ktv.helper;

import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.view.KeyEvent;
import android.view.View;

import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.view.MainUpView;
import com.palmwifi.ktv.R;

/**
 * Created by David on 2017/3/29.
 */

public class ViewFocusHelper {

    private View mOldFocus;
    private OnSharkListener mListener;
    private   float defaultScaleSize = 1.15f;

    public ViewFocusHelper(OnSharkListener listener){
        this.mListener = listener;
    }

    public ViewFocusHelper(){
    }

    public void onFocusView(View newFocus,float scale){
        if(mOldFocus !=null && mOldFocus.getId() != R.id.rl_item_recycler){
            onUnFocusAnim(mOldFocus);
        }
        if(newFocus != null && newFocus.getId() != R.id.rl_item_recycler) {
            newFocus.bringToFront();
            onFocusAnim(newFocus,scale);
        }

        mOldFocus = newFocus;
    }



    private  void onFocusAnim(View view,float scale){

        SpringForce spring = new SpringForce(scale)
                .setDampingRatio(0.6f)
                .setStiffness(SpringForce.STIFFNESS_LOW);
        final SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, scale).setSpring(spring);
        final SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, scale).setSpring(spring);
        animX.start();
        animY.start();
    }

    private void onUnFocusAnim(View view){

        SpringForce spring = new SpringForce(1f)
                .setDampingRatio(0.6f)
                .setStiffness(SpringForce.STIFFNESS_LOW);
        final SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1).setSpring(spring);
        final SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1).setSpring(spring);
        animX.start();
        animY.start();
    }


    public void onFocusView(View newFocus){

        onFocusView(newFocus,defaultScaleSize);
    }


    public void setFocusView(View newFocus){

        mOldFocus = newFocus;
    }


    public float getScaleSize() {
        return defaultScaleSize;
    }

    public void setDefaultScaleSize(float defaultScaleSize) {
        this.defaultScaleSize = defaultScaleSize;
    }

    /**
     * 处理DOWN事件.
     */
    public boolean onSoftKeyDown(int keyCode, KeyEvent event) {
        if(mListener == null || mOldFocus == null || !mOldFocus.isFocused()) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: // 左
                return mListener.toLeft(mOldFocus);
            case KeyEvent.KEYCODE_DPAD_RIGHT: // 右
                return mListener.toRight(mOldFocus);
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return mListener.toDown(mOldFocus);
            case KeyEvent.KEYCODE_DPAD_UP:
                return mListener.toTop(mOldFocus);
            case KeyEvent.KEYCODE_BACK:
                return mListener.toBack(mOldFocus);
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return mListener.toMenu(mOldFocus);

        }
        return false;
    }


    /**
     * 处理DOWN事件.
     */
    public boolean onSoftKeyDown1(int keyCode, KeyEvent event) {
        if(mListener == null ) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: // 左
                return mListener.toLeft(mOldFocus);
            case KeyEvent.KEYCODE_DPAD_RIGHT: // 右
                return mListener.toRight(mOldFocus);
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return mListener.toDown(mOldFocus);
            case KeyEvent.KEYCODE_DPAD_UP:
                return mListener.toTop(mOldFocus);
            case KeyEvent.KEYCODE_BACK:
                return mListener.toBack(mOldFocus);
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return mListener.toMenu(mOldFocus);

        }
        return false;
    }
/*
    *//**
     * 处理UP的事件.
     *//*
    public boolean onSoftKeyUp(int keyCode, KeyEvent event) {
        if(mOldFocus == null || !mOldFocus.isFocused()) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: // 左
                if(itemMovePosition  == 0){
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT: // 右
                if(itemMovePosition  == 1){
                    return true;
                }
                return false;
        }
        return false;
    }*/




    public  interface OnSharkListener {

         boolean toLeft(View view);

        boolean toRight(View view);

        boolean toTop(View view);

        boolean toDown(View view);

        boolean toBack(View view);

        boolean toMenu(View view);


    }


}
