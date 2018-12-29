package com.yzy.map3d.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzy.map3d.R;


/**
 * Toast的工具类
 */
public class ToastUtil {

    public static final long DEFAULT_DURATION = 1000L;

    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;

    private static android.widget.Toast normalToast;
    private static android.widget.Toast gravityToast;
    private static Handler handler;
    private static View toastView;
    private static TextView toastMsg;
    private static ImageView toastIcon;

    static {
        if (!(Looper.myLooper() == Looper.getMainLooper())) {
            Looper.prepare();
        }
        handler = new Handler();
    }

    private static Runnable run = new Runnable() {
        public void run() {
            if (normalToast != null) normalToast.cancel();
            if (gravityToast != null) gravityToast.cancel();
        }
    };

    private static void toast(Context context, CharSequence text, int duration) {
        if (context == null) return;
        handler.removeCallbacks(run);
        long delayMillis;
        switch (duration) {
            case LENGTH_LONG:
                delayMillis = 3000L;
                break;
            case LENGTH_SHORT:
            default:
                delayMillis = DEFAULT_DURATION;
                break;
        }
        if (normalToast == null) {
            normalToast = android.widget.Toast.makeText(context, text, duration);
        } else {
            normalToast.setText(text);
        }
        handler.postDelayed(run, delayMillis);
        normalToast.show();
    }

    private static void toast(Context context, CharSequence text, int duration, int mipmapImg, int gravity, int xOffset, int yOffset) {
        if (context == null) return;
        handler.removeCallbacks(run);
        long delayMillis;
        switch (duration) {
            case LENGTH_LONG:
                delayMillis = 3000L;
                break;
            case LENGTH_SHORT:
            default:
                delayMillis = DEFAULT_DURATION;
                break;
        }
        if (gravityToast == null) {
            gravityToast = android.widget.Toast.makeText(context, text, duration);
            toastView = LayoutInflater.from(context).inflate(R.layout.view_tips, null, false);
            toastMsg = toastView.findViewById(R.id.tips_msg);
            toastIcon = toastView.findViewById(R.id.tips_icon);
        }
        if (mipmapImg > 0) {
            toastIcon.setVisibility(View.VISIBLE);
            toastIcon.setImageResource(mipmapImg);
        }
        toastMsg.setText(text);
        gravityToast.setView(toastView);
        gravityToast.setGravity(gravity, xOffset, yOffset);
        handler.postDelayed(run, delayMillis);
        gravityToast.show();
    }

    /**
     * 弹出Toast
     *
     * @param context  弹出Toast的上下文
     * @param text     弹出Toast的内容
     * @param duration 弹出Toast的持续时间
     */
    public static void show(Context context, CharSequence text, int duration) {
        if (duration > 0) {
            duration = LENGTH_SHORT;
        }
        toast(context, text, duration);
    }


    /**
     * 中间弹出Toast
     *
     * @param text 弹出Toast的内容
     */
    public static void showCenter(Context context, CharSequence text) {
        toast(context, text, LENGTH_SHORT, -1, Gravity.CENTER, 0, 0);
    }

    /**
     * 中间弹出Toast 长时间
     *
     * @param text
     */
    public static void showCenterLong(Context context, CharSequence text) {
        toast(context, text, LENGTH_LONG, -1, Gravity.CENTER, 0, 0);
    }

    /**
     * 中间弹出Toast
     *
     * @param context 弹出Toast的上下文
     * @param text    弹出Toast的内容
     */
    public static void showCenter(Context context, CharSequence text, int mipmapImg) {
        toast(context, text, LENGTH_SHORT, mipmapImg, Gravity.CENTER, 0, 0);
    }


    /**
     * 中弹出Toast
     *
     * @param context 弹出Toast的上下文
     * @param text    弹出Toast的内容
     * @param gravity 弹出Toast的gravity
     * @param xOffset 弹出Toast的x间距
     * @param yOffset 弹出Toast的y间距
     */
    public static void showGravity(Context context, CharSequence text, int gravity, int xOffset, int yOffset) {
        toast(context, text, LENGTH_SHORT, -1, gravity, xOffset, yOffset);
    }

    /**
     * 弹出Toast
     *
     * @param context  弹出Toast的上下文
     * @param text     弹出Toast的内容
     * @param duration 弹出Toast的持续时间
     * @param gravity  弹出Toast的gravity
     * @param xOffset  弹出Toast的x间距
     * @param yOffset  弹出Toast的y间距
     */
    public static void showGravity(Context context, CharSequence text, int duration, int gravity, int xOffset, int yOffset) {
        toast(context, text, duration, -1, gravity, xOffset, yOffset);
    }

    /**
     * 弹出Toast
     *
     * @param context  弹出Toast的上下文
     * @param resId    弹出Toast的内容的资源ID
     * @param duration 弹出Toast的持续时间
     */
    public static void show(Context context, int resId, int duration)
            throws NullPointerException {
        if (null == context)
            throw new NullPointerException("The context is null!");
        duration = duration > 0 ? LENGTH_LONG : LENGTH_SHORT;
        toast(context, context.getResources().getString(resId), duration);
    }
}
