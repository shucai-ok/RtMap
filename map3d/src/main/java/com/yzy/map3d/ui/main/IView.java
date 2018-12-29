package com.yzy.map3d.ui.main;

import com.rtm.common.model.POI;
import com.yzy.map3d.base.IBaseView;

import java.util.List;

/**
 * @author 志尧
 * @date on 2018-12-20 14:56
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public interface IView extends IBaseView {


    /**
     * 设置楼层列表
     *
     * @param floorList
     */
    void setFloorList(List<String> floorList);


    /**
     * 更新View
     */
    void updateKeyRouteNodeView(String desc, String route);

    /**
     * 选点
     *
     * @param poi
     */
    void selectedPoi(POI poi);

}
