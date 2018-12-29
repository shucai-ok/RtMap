package com.yzy.map3d.base;

import java.lang.ref.WeakReference;

/**
 * @author 志尧
 * @date on 2018-12-20 14:36
 * @email yuzhiyao0912@gmail.com
 * @describe presenter的基类 绑定View.释放资源
 * @ideas
 */
public class BasePresenter {

    protected WeakReference<IBaseView> mViewRef;    //view接口类型的弱引用

    /**
     * 关联
     */
    public void attachView(IBaseView view) {
        mViewRef = new WeakReference<>(view);
    }

    /**
     * @return view对象
     */
    protected IBaseView getView() {
        return mViewRef.get();
    }

    /**
     * 取消关联,释放资源
     */
    public void detachView() {
        if (null != mViewRef) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    /**
     * @return 判断是否关联
     */
    public boolean isViewAttached() {
        return null != mViewRef && null != mViewRef.get();
    }
}
