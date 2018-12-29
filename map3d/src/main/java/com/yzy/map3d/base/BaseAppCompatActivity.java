package com.yzy.map3d.base;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * @author 志尧
 * @date on 2018-12-20 14:36
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity implements IBaseView {

    public static final int RC_MAP = 0x01;

    protected BasePresenter mBasePresenter;
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mBasePresenter = createPresenter();
            if (null == mBasePresenter) {
                handleErrorMsg("缺少 Presenter");
                exitActivity();
                throw new Exception("缺少 Presenter");
            }

            mBasePresenter.attachView(this);
            setContentView(this.getLayoutId());
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initialize();

    protected abstract BasePresenter createPresenter();


    @Override
    public void showLoading() {
        showLoading("正在加载...");
    }

    @Override
    public void showLoading(String msg) {
        hideLoading();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void loadProgress(String msg) {
        if (mProgressDialog != null && !isFinishing()) {
            mProgressDialog.setMessage(msg);
        }
    }

    @Override
    public void hideLoading() {
        try {
            if (null != mProgressDialog && !isFinishing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleErrorMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == -1 || grantResults == null || grantResults.length == 0) {
            return;
        }

        switch (requestCode) {
            case RC_MAP:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    mapInitialize();
                } else {
                    handleErrorMsg("地理位置已被拒绝");
                    exitActivity();
                }
                break;
        }
    }

    public void mapInitialize() {

    }

    @Override
    public void exitActivity() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mBasePresenter != null) {
            if (mBasePresenter.isViewAttached()) {
                mBasePresenter.detachView();
            }
        }
        super.onDestroy();
    }
}