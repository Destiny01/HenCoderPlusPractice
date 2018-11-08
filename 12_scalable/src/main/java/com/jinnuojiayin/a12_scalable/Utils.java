package com.jinnuojiayin.a12_scalable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

/**
 * Created by xyl on 2018/11/8.
 */
public class Utils {
    /**
     * dp转成px
     * @param dp
     * @return
     */
    public static float dp2px(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,Resources.getSystem().getDisplayMetrics());
    }

    public static Bitmap getAvatar(Resources res,int width){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,R.drawable.avatar_rengwuxian,options);
        options.inJustDecodeBounds = false;
        options.inDensity = options.outWidth;
        options.inTargetDensity = width;
        return BitmapFactory.decodeResource(res,R.drawable.avatar_rengwuxian,options);
    }
}
