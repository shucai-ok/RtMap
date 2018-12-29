package com.yzy.map3d.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

/**
 * @author 志尧
 * @date on 2018-12-25 14:00
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public class BitmapUtils {

    public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
}
