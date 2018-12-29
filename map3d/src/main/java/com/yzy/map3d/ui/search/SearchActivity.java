package com.yzy.map3d.ui.search;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.yzy.map3d.R;
import com.yzy.map3d.base.BaseAppCompatActivity;
import com.yzy.map3d.base.BasePresenter;
import com.yzy.map3d.model.MapModel;
import com.yzy.map3d.ui.main.MapActivity;
import com.yzy.map3d.util.UiUtils;
import com.yzy.map3d.widget.RvDialogFragment;
import com.yzy.map3d.widget.SearchTitleView;

import java.util.List;


/**
 * @author 志尧
 * @date on 2018-02-26 14:25
 * @email 1417337180@qq.com
 * @describe
 * @ideas
 */

public class SearchActivity extends BaseAppCompatActivity implements IView,
        SearchTitleView.ISearchTitleClick, SearchAdapter.ISearchItemClick, RvDialogFragment.ICollectClick {

    public static final String SEARCH_NAME = "searchName";
    public static final String SEARCH_BUILDID = "searchBuildid";

    SearchTitleView searchView;
    RelativeLayout rlSearchMapSelect;
    RelativeLayout rlSearchMapCollect;
    RecyclerView rvSearchMapList;

    SearchAdapter mSearchAdapter;
    RvDialogFragment mRvDialogFragment;

    Presenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_search;
    }

    @Override
    protected void initialize() {
        presenter.setContext(SearchActivity.this);

        findViewById();
        initData();
        initView();
        initClick();
    }

    @Override
    protected BasePresenter createPresenter() {
        return presenter = new Presenter(this);
    }

    void findViewById() {
        searchView = findViewById(R.id.searchView);
        rlSearchMapSelect = findViewById(R.id.rlSearchMapSelect);
        rlSearchMapCollect = findViewById(R.id.rlSearchMapCollect);
        rvSearchMapList = findViewById(R.id.rvSearchMapList);
    }

    void initData() {
        presenter.setmSearchKey(getIntent().getStringExtra(SEARCH_NAME));
        presenter.setmBuildId(getIntent().getStringExtra(SEARCH_BUILDID));
    }

    void initView() {
        searchView.setiSearchTitleClick(this);
        searchView.setEditFocusable(true);
        searchView.setEditKey(presenter.getmSearchKey());

        mSearchAdapter = new SearchAdapter(presenter.getHistoryList());
        mSearchAdapter.setiSearchItemClick(this);
        rvSearchMapList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        rvSearchMapList.setAdapter(mSearchAdapter);
    }

    void initClick() {
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        rlSearchMapSelect.setOnClickListener(myOnClickListener);
        rlSearchMapCollect.setOnClickListener(myOnClickListener);
    }

    @Override
    public void updateHistoryList(List<POI> poiList) {
        if (poiList == null || poiList.isEmpty()) {
            return;
        }

        mSearchAdapter.setMode(0);
        mSearchAdapter.setDatas(poiList);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateList(List<POI> poiList) {
        if (poiList == null || poiList.isEmpty()) {
            return;
        }

        mSearchAdapter.setMode(1);
        mSearchAdapter.setDatas(poiList);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void headBack() {
        UiUtils.closeKey(SearchActivity.this, searchView);
        exitActivity();
    }

    @Override
    public void headSearch(String key) {
        presenter.setmSearchKey(key);
        presenter.getSearchList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            headBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void itemClick(POI poi, int position) {
        if (poi == null || poi.getPoiNO() < 0) {
            MapModel.clearHistoryList(SearchActivity.this);
            mSearchAdapter.setDatas(null);
            mSearchAdapter.notifyDataSetChanged();
            return;
        }

        MapModel.savePoi2HistoryList(SearchActivity.this, poi);
        Intent intent = new Intent();
        intent.putExtra(MapActivity.SEARCH_POI, poi);
        setResult(100, intent);
        headBack();
    }

    @Override
    public void collectItemClick(POI poi, int position) {
        itemClick(poi, position);
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.rlSearchMapSelect) {
                headBack();
                return;
            }

            if (v.getId() == R.id.rlSearchMapCollect) {
                if (mRvDialogFragment == null) {
                    mRvDialogFragment = RvDialogFragment.newInstance(MapModel.getCollectJson(SearchActivity.this));
                    mRvDialogFragment.setiCollectClick(SearchActivity.this);
                }
                mRvDialogFragment.show(getFragmentManager(), RvDialogFragment.RV_DIALOGFRAGMENT_TAG);
                return;
            }
        }
    }
}
