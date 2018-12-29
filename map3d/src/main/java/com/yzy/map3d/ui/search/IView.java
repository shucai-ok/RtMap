package com.yzy.map3d.ui.search;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.yzy.map3d.base.IBaseView;

import java.util.List;


/**
 * @author 志尧
 * @date on 2018-12-28 10:55
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */

public interface IView extends IBaseView {

    /**
     * 更新历史列表
     *
     * @param poiList
     */
    void updateHistoryList(List<POI> poiList);

    /**
     * 更新列表
     *
     * @param poiList
     */
    void updateList(List<POI> poiList);
}
