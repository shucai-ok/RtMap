package com.yzy.map3d.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 志尧
 * @date on 2018-02-02 16:18
 * @email 1417337180@qq.com
 * @describe SharedPreferences的简单封装
 * @ideas
 */

public class SPUtils {

    private static final String TAG = SPUtils.class.getSimpleName();

    private static SPUtils instance;
    private static SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static Context mContext;
    // 默认的SharePreference文件名
    private static String mName = "Undefine_SharedPreferenceName";

    //单例
    private SPUtils(Context context, String name, int mode) {
        mSharedPreferences = context.getSharedPreferences(name, mode);
        mEditor = mSharedPreferences.edit();
    }

    //初始化
    public static void init(Context context) {
        mContext = context;
    }

    public static void init(Context context, String name) {
        mContext = context;
        mName = name;
    }

    /**
     * 使用默认配置
     *
     * @return
     */
    public static SPUtils getInstance() {
        return getInstance(mContext, mName, Context.MODE_PRIVATE);
    }


    /**
     * 上下文
     *
     * @param context
     * @return
     */
    public static SPUtils getInstance(Context context) {
        return getInstance(context, mName, Context.MODE_PRIVATE);
    }

    /**
     * 使用自定义配置
     *
     * @param context
     * @param name
     * @param mode
     * @return
     */
    public static SPUtils getInstance(Context context, String name, int mode) {
        if (instance == null) {
            synchronized (SPUtils.class) {
                if (instance == null) {
                    instance = new SPUtils(context, name, mode);
                }
            }
        }
        return instance;
    }

    /**
     * 获得字符串
     */
    public String getString(String key, String defaultValue) throws ClassCastException {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * 获取整形数据：默认为-1
     */
    public int getInt(String key) throws ClassCastException {
        return getInt(key, -1);
    }

    /**
     * 获取整形数据
     */
    public int getInt(String key, int defaultValue) throws ClassCastException {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 获取Long型数据：默认-1
     */
    public long getLong(String key) throws ClassCastException {
        return getLong(key, -1);
    }

    /**
     * 获取Long型数据
     */
    public long getLong(String key, long defaultValue) throws ClassCastException {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * 获取Float数据：默认-1
     */
    public float getFloat(String key) throws ClassCastException {
        return getFloat(key, -1);
    }

    /**
     * 获取Float数据
     */
    public float getFloat(String key, float defaultValue) throws ClassCastException {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * 获取Boolean数据：默认false
     */
    public boolean getBoolean(String key) throws ClassCastException {
        return getBoolean(key, false);
    }

    /**
     * @return 获取Boolean数据
     */
    public boolean getBoolean(String key, boolean defaultValue) throws ClassCastException {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * @return 获取所有键值对
     */
    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    /**
     * 注意：当保存的value不是boolean, byte(会被转换成int保存),int, long, float,
     * String等类型时将调用它的toString()方法进行值的保存。
     *
     * @param key   键名称。
     * @param value 值。
     * @return 引用PreferencesUtils的对象。
     */
    public SPUtils put(String key, Object value) {
        if (value == null) {
            value = "";
        }
        if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer || value instanceof Byte) {
            mEditor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else {
            mEditor.putString(key, value.toString());
        }
        return this;
    }


    /**
     * 移除键值对。
     *
     * @param key 要移除的键名称。
     * @return 引用的PreferencesUtils对象。
     */
    public SPUtils remove(String key) {
        mEditor.remove(key);
        return this;
    }

    /**
     * 清除所有键值对。
     *
     * @return 引用的PreferencesUtils对象。
     */
    public SPUtils clear() {
        mEditor.clear();
        return this;
    }

    /**
     * 是否包含某个键。
     *
     * @param key 查询的键名称。
     * @return 当且仅当包含该键时返回true, 否则返回false.
     */
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    /**
     * 返回是否提交成功。 同步方式
     *
     * @return 当且仅当提交成功时返回true, 否则返回false.
     */
    public boolean commit() {
//        Log.i(TAG, "SharedPreferences提交方式:commit(同步)");
        return mEditor.commit();
    }

    /**
     * 返回是否提交成功。 异步方式
     *
     * @return 当且仅当提交成功时返回true, 否则返回false.
     */
    public boolean commitAsyn() {
//        Log.i(TAG, "SharedPreferences提交方式:commitAsyn(异步)");
        return SharedPreferencesCompat.apply(mEditor);
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * commit方法是同步的，并且我们很多时候的commit操作都是UI线程中，毕竟是IO操作，尽可能异步；
     * 所以我们使用apply进行替代，apply异步的进行写入；
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static boolean apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
//                    Log.i(TAG, "异步提交调用成功");
                    return true;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
//            Log.i(TAG, "异步提交调用失败：调用同步提交方式");
            return editor.commit();
        }
    }
}
