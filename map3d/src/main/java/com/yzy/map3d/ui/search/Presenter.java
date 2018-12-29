package com.yzy.map3d.ui.search;

import android.content.Context;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.rtm.net.RMSearchPoiUtil;
import com.yzy.map3d.base.BasePresenter;
import com.yzy.map3d.base.IBaseView;
import com.yzy.map3d.model.MapModel;
import com.yzy.map3d.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 志尧
 * @date on 2018-12-28 11:00
 * @email yuzhiyao0912@gmail.com
 * @describe 地图搜索
 * @ideas
 */

public class Presenter extends BasePresenter {

    public List<POI> mCollectList = new ArrayList<>();
    private String superNames[] = new String[]{"Hiboom", "海盛", "铜锣湾", "波拉猴", "OMG",
            "蜡笔", "MVP", "第九元素", "跃无限"};
    private int superIndex;

    private String mSearchKey;
    private String mBuildId;

    public void setmSearchKey(String mSearchKey) {
        this.mSearchKey = mSearchKey;
    }

    public void setmBuildId(String mBuildId) {
        this.mBuildId = mBuildId;
    }

    public String getmSearchKey() {
        return mSearchKey;
    }

    IView view;
    RMSearchPoiUtil rmSearchPoiUtil;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Presenter(IBaseView view) {
        if (view instanceof IView) {
            this.view = (IView) view;
            this.rmSearchPoiUtil = new RMSearchPoiUtil();
            getCollectList();
        } else {
            throw new RuntimeException(this.getClass().getName());
        }
    }

    public List<POI> getHistoryList() {
        List<POI> historyList = MapModel.getHistoryJson2List(context);
        if (historyList.size() > 3) {
            POI clearPoi = new POI();
            clearPoi.setPoiNO(-2);
            clearPoi.setName("清空历史记录");
            historyList.add(clearPoi);
        }
        return historyList;
    }


    public void getSearchList() {
        if (StringUtils.isEmpty(mSearchKey)) {
            view.updateHistoryList(getHistoryList());
            return;
        }

        rmSearchPoiUtil.setBuildid(mBuildId)
                .setKeywords(mSearchKey)
                .setOnSearchPOIExtListener(rmpoiExts -> {
                    if (rmpoiExts == null) {
                        return;
                    }

                    if (rmpoiExts.getError_code() != 0) {
                        return;
                    }

                    List<POIExt> poilist = rmpoiExts.getPoilist();
                    if (poilist == null || poilist.isEmpty()) {
                        return;
                    }

                    List<POI> newPoiList = new ArrayList<>();
                    for (POIExt poiExt : poilist) {
                        POI poi = new POI();
                        poi.setName(poiExt.getName());
                        poi.setX(poiExt.getX());
                        poi.setY(poiExt.getY());
                        poi.setBuildId(poiExt.getBuildId());
                        poi.setFloor(poiExt.getFloor());
                        poi.setPoiNO(poiExt.getPoiNO());
                        newPoiList.add(poi);
                    }
                    view.updateList(newPoiList);
                }).searchPoi();
    }

    void getCollectList() {
        List<POI> collectJson2List = MapModel.getCollectJson2List(context);
        if (collectJson2List != null && !collectJson2List.isEmpty()) {
            mCollectList.addAll(collectJson2List);
            return;
        }

        searchSuperList();
    }

    void searchSuperList() {
        if (superIndex >= superNames.length) {
            MapModel.saveCollectList(context, mCollectList);
            return;
        }

        rmSearchPoiUtil
                .setBuildid(mBuildId)
                .setKeywords(superNames[superIndex])
                .setOnSearchPOIExtListener(rmpoiExts -> {
                    if (rmpoiExts.getError_code() == 0) {
                        List<POIExt> poilist = rmpoiExts.getPoilist();
                        if (poilist != null && !poilist.isEmpty()) {
                            mCollectList.addAll(poilist);
                        }
                    }
                    superIndex++;
                    searchSuperList();
                }).searchPoi();
    }
}
