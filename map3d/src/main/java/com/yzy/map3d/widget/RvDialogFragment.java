package com.yzy.map3d.widget;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.yzy.map3d.R;
import com.yzy.map3d.ui.search.SearchAdapter;
import com.yzy.map3d.util.JsonUtils;

import java.util.List;

/**
 * @author 志尧
 * @date on 2018-01-25 14:29
 * @email 1417337180@qq.com
 * @describe 收藏菜单
 * @ideas
 */

public class RvDialogFragment extends DialogFragment implements SearchAdapter.ISearchItemClick {

    private ICollectClick iCollectClick;

    public void setiCollectClick(ICollectClick iCollectClick) {
        this.iCollectClick = iCollectClick;
    }

    public static final String RV_DIALOGFRAGMENT_TAG = "rv_dialogfragment_tag";
    public static final String RV_DIALOGFRAGMENT_DATA = "rv_dialogfragment_data";

    public static final RvDialogFragment newInstance(String json) {
        RvDialogFragment rvDialogFragment = new RvDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RV_DIALOGFRAGMENT_DATA, json);
        rvDialogFragment.setArguments(bundle);
        return rvDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //测试
        String json = getArguments().getString(RV_DIALOGFRAGMENT_DATA);
        List<POI> list = JsonUtils.json2PoiList(json);

        View collectView = inflater.inflate(R.layout.dialog_map_collect, null);
        RecyclerView rvCollectList = collectView.findViewById(R.id.rv_collect_list);
        TextView tvCollectCancel = collectView.findViewById(R.id.tv_collect_cancel);
        tvCollectCancel.setOnClickListener(view -> dismiss());

        SearchAdapter searchAdapter = new SearchAdapter(list);
        searchAdapter.setMode(1);
        rvCollectList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCollectList.setAdapter(searchAdapter);
        searchAdapter.setiSearchItemClick(this);

        return collectView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    void initDialog() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.BOTTOM;
        attributes.dimAmount = 0.4f;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.windowAnimations = R.style.BottomDialogFragment;
        window.setAttributes(attributes);
    }

    @Override
    public void itemClick(POI poi, int position) {
        if (iCollectClick != null) {
            iCollectClick.collectItemClick(poi, position);
        }

        dismiss();
    }

    public interface ICollectClick {
        void collectItemClick(POI poi, int position);
    }
}