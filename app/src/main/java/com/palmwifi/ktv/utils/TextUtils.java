package com.palmwifi.ktv.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by David on 2017/3/3.
 */

public class TextUtils {

    public static void setNumTypeFace(Context context,TextView textView){
        //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"OpenSans-Light.ttf");
        //使用字体
        textView.setTypeface(typeFace);
    }
}
