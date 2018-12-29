package com.yzy.map3d.ui.main;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.rtm.common.model.BuildingInfo;
import com.rtm.common.model.Floor;
import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.rtm.common.model.RMLocation;
import com.rtm.core.model.NavigatePoint;
import com.rtm.frm.nmap.entry.RouteNode;
import com.rtm.net.RMBuildDetailUtil;
import com.rtm.net.RMSearchPoiUtil;
import com.yzy.map3d.base.BasePresenter;
import com.yzy.map3d.base.IBaseView;
import com.yzy.map3d.model.MapModel;
import com.yzy.map3d.util.JsonUtils;
import com.yzy.map3d.util.SPUtils;
import com.yzy.map3d.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 志尧
 * @date on 2018-12-20 14:53
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public class Presenter extends BasePresenter {

    private static final String FLOOR_JSON = "floor_json";

    IView view;

    private Vibrator vibrate;

    public Presenter(IBaseView view) {
        if (view instanceof IView) {
            this.view = (IView) view;
        } else {
            throw new RuntimeException(this.getClass().getName());
        }
    }

    public void startVibrator(Context context) {
        if (vibrate == null) {
            vibrate = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        }

        vibrate.vibrate(new long[]{0, 200, 200, 200}, -1);
    }

    public void cancelVibrator() {
        if (vibrate != null) {
            vibrate.cancel();
            vibrate = null;
        }
    }

    public void getFloorList(Context context, String buildId) {
        List<String> floorList = JsonUtils.json2List(
                SPUtils.getInstance(context).getString(FLOOR_JSON, null));
        if (floorList != null && !floorList.isEmpty()) {
            view.setFloorList(floorList);
            return;
        }

        RMBuildDetailUtil.requestBuildDetail(buildId, rmBuildDetail -> {
            if (rmBuildDetail.getError_code() != 0) {
                return;
            }

            BuildingInfo build = rmBuildDetail.getBuild();
            if (build == null) {
                return;
            }

            floorList.clear();
            for (Floor floor : build.getFloorlist()) {
                floorList.add(floor.getFloor());
            }

            view.setFloorList(floorList);
            SPUtils.getInstance(context)
                    .put(FLOOR_JSON, JsonUtils.list2Json(floorList))
                    .commit();
        });
    }

    public boolean checkInshow(List<NavigatePoint> path, String checkFloor, float[] checkPosition) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        if (checkPosition == null || checkPosition.length != 4) {
            return false;
        }

        for (NavigatePoint point : path) {
            if (!StringUtils.isEquals(point.getFloor(), checkFloor)) {
                continue;
            }

            float x = point.getX();
            float y = point.getY();

            if ((x >= checkPosition[0] && x <= checkPosition[2]) && (y >= checkPosition[1] && y <= checkPosition[3])) {
                return true;
            }
        }

        return false;
    }


    public void routeBookChanged(RouteNode routeNode) {
        NavigatePoint newNavigatePoint = getNewNavigatePoint(routeNode);
        view.updateKeyRouteNodeView(newNavigatePoint.getDesc(), newNavigatePoint.getAroundPoiName());
    }

    NavigatePoint getNewNavigatePoint(RouteNode routeNode) {

        String poiName = "";
        String nearPoiName = routeNode.getNearPoiName();
        switch (routeNode.getAction()) {
            case 1:
                poiName = "在" + nearPoiName + "处直行";
                break;
            case 2:
                poiName = "在" + nearPoiName + "处右转";
                break;
            case 3:
                poiName = "在" + nearPoiName + "处左转";
                break;
            case 4:
                poiName = "在" + nearPoiName + "处乘坐直梯上行";
                break;
            case 5:
                poiName = "在" + nearPoiName + "处乘坐直梯下行";
                break;
            case 6:
                poiName = "在" + nearPoiName + "处乘坐扶梯上行";
                break;
            case 7:
                poiName = "在" + nearPoiName + "处乘坐扶梯下行";
                break;
            case 8:
                poiName = "到达终点";
                break;
        }

        String desc = "步行约 " + (int) routeNode.getDistance() + " 米后";

        NavigatePoint navigatePoint = new NavigatePoint();
        navigatePoint.setAction(routeNode.getAction());
        navigatePoint.setAroundPoiName(poiName);
        navigatePoint.setDesc(desc);
        navigatePoint.setDistance((int) routeNode.getDistance());
        navigatePoint.setImportant(true);
        navigatePoint.setBuildId(routeNode.getBuildingId());
        navigatePoint.setFloor(routeNode.getFloor());
        navigatePoint.setX(routeNode.getX());
        navigatePoint.setY(routeNode.getY());

        return navigatePoint;
    }

    public void searchPoi(Context context, String buildId, String searchName, String searchFloor) {
        if (StringUtils.isEmpty(searchName) || StringUtils.isEmpty(searchFloor)) {
            return;
        }

        new RMSearchPoiUtil()
                .setBuildid(buildId)
                .setFloor(searchFloor)
                .setKeywords(searchName)
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

                    POIExt poiExt = poilist.get(0);
                    poiExt.setStyle(5);
                    view.selectedPoi(poiExt);
                }).searchPoi();
    }

    /**
     * 获取最新历史
     *
     * @param context
     * @return
     */
    public POI getNewHistory(Context context) {
        List<POI> historyJson2List = MapModel.getHistoryJson2List(context);
        if (historyJson2List == null || historyJson2List.isEmpty()) {
            return null;
        }

        return historyJson2List.get(0);
    }


    /***************************************DEBUG**************************************/
    int index = 0;
    List<RMLocation> rmLocations = new ArrayList<>();

    public void setRmLocations(String buildId) {
        RMLocation rmLocation0 = new RMLocation();
        rmLocation0.setError(0);
        rmLocation0.setBuildID(buildId);
        rmLocation0.setFloor("F3");
        rmLocation0.setX(153.6667f);
        rmLocation0.setY(55.638f);
        rmLocations.add(rmLocation0);
    }

    public void startDebugNavigate(List<NavigatePoint> path) {
        for (NavigatePoint point : path) {
            RMLocation rmLocation = new RMLocation();
            rmLocation.setError(0);
            rmLocation.setBuildID(point.getBuildId());
            rmLocation.setFloor(point.getFloor());
            rmLocation.setX(point.getX());
            rmLocation.setY(point.getY());
            rmLocations.add(rmLocation);
        }
    }

    public void clearDebugNavigate(RMLocation rmLocation) {
        index = 0;
        rmLocations.clear();
        rmLocations.add(rmLocation);
    }

    public RMLocation nextDebug() {
        if (index >= rmLocations.size()) {
            return null;
        }

        return rmLocations.get(index);
    }
}
