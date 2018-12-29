package com.yzy.map3d.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yzy.map3d.R;

public class NetworkDialogFragment extends DialogFragment {

    public static final String NETWORK_DIALOG_TAG = "network_dialog_tag";
    public static final String NETWORK_DIALOG_DATA = "network_dialog_data";

    private IDialogSettingClick iDialogSettingClick;

    public void setiDialogSettingClick(IDialogSettingClick iDialogSettingClick) {
        this.iDialogSettingClick = iDialogSettingClick;
    }

    public static final NetworkDialogFragment newInstance() {
        NetworkDialogFragment dialogFragment = new NetworkDialogFragment();
        return dialogFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.dialog_img, null);
        ImageView dialogImg = dialogView.findViewById(R.id.dialogImg);
        View dialogDismiss = dialogView.findViewById(R.id.dialogDismiss);
        View dialogClick = dialogView.findViewById(R.id.dialogClick);

        dialogImg.setImageResource(R.drawable.bg_network);

        dialogDismiss.setOnClickListener(v -> {
            dismiss();
        });

        dialogClick.setOnClickListener(v -> {
            if (iDialogSettingClick != null) {
                iDialogSettingClick.onDialogClick();
            }
        });
        return dialogView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        });


        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.BOTTOM;
        attributes.dimAmount = 0.4f;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.windowAnimations = R.style.BottomDialogFragment;
        window.setAttributes(attributes);
    }

    public interface IDialogSettingClick {
        void onDialogClick();
    }
}
