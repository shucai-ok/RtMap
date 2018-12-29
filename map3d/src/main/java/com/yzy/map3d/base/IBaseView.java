package com.yzy.map3d.base;


/**
 * @author 志尧
 * @date on 2018-12-20 14:36
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */

public interface IBaseView {


    /**
     * 显示loading
     */
    void showLoading();

    /**
     * 显示文字loading
     *
     * @param msg
     */
    void showLoading(String msg);

    /**
     * 加载进度
     *
     * @param msg
     */
    void loadProgress(String msg);

    /**
     * 关闭loading
     */
    void hideLoading();

    /**
     * Toast
     *
     * @param msg
     */
    void handleErrorMsg(String msg);

    /**
     * 关闭界面
     */
    void exitActivity();

}
