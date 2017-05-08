package com.palmwifi.ktv.helper;

import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;

import com.palmwifi.ktv.R;

/**
 * Created by David on 2017/3/28.
 */

public class ItemFocusHelper {

    private View itemView;
    private int itemMovePosition;
    private long focusTime;


    public void onFocus(View itemView){
        onFocusAnim(itemView);
        this.itemView = itemView;
        itemMovePosition = 0;
        selectItemView(itemView,true,false);
        focusTime = System.currentTimeMillis();
    }

    public void onUnFocus(View itemView){
        selectItemView(itemView, false,false);
        onUnFocusAnim(itemView);
    }

    private void selectItemView(View itemView, boolean isSelectPlay,boolean isSelectFav){

        if(itemView != null) {
            View favView =  itemView.findViewById(R.id.img_hot_fav);
            View playView =  itemView.findViewById(R.id.img_hot_play);
            favView.setSelected(isSelectFav);
            playView.setSelected(isSelectPlay);

        }
    }


    public  void onFocusAnim(View view){
        view.clearAnimation();
        ViewCompat.setScaleX(view,1);
        ViewCompat.setScaleY(view,1);
        SpringForce spring = new SpringForce(1.05f)
                .setDampingRatio(0.7f)
                .setStiffness(SpringForce.STIFFNESS_LOW);
        final SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1.05f).setSpring(spring);
        final SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1.05f).setSpring(spring);
        animX.start();
        animY.start();
    }

    public  void onUnFocusAnim(View view){
        view.clearAnimation();
        SpringForce spring = new SpringForce(1f)
                .setDampingRatio(0.7f)
                .setStiffness(SpringForce.STIFFNESS_LOW);
        final SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1).setSpring(spring);
        final SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1).setSpring(spring);
        animX.start();
        animY.start();
    }
    /**
     * 处理DOWN事件.
     */
    public boolean onSoftKeyDown(int keyCode, KeyEvent event) {
        if(itemView == null || !itemView.isFocused()) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: // 左
                if(itemMovePosition  == 1){
                    itemMovePosition = 0;
                    selectItemView(itemView,true,false);
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT: // 右
                int dis = (int) (System.currentTimeMillis() - focusTime);
                if(dis >300) {
                    if (itemMovePosition == 0) {
                        itemMovePosition = 1;
                        selectItemView(itemView, false, true);
                        return true;
                    }
                }
                return false;
        }
        return false;
    }

    /**
     * 处理UP的事件.
     */
    public boolean onSoftKeyUp(int keyCode, KeyEvent event) {
        if(itemView == null || !itemView.isFocused()) {
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
    }


    public boolean isSelectedFav(){

        return  itemMovePosition == 1;
    }
}
