package com.yzy.map3d.model;

import android.content.Context;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.yzy.map3d.util.JsonUtils;
import com.yzy.map3d.util.SPUtils;
import com.yzy.map3d.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 志尧
 * @date on 2018-12-28 16:41
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public class MapModel {

    private static final String HISTORY_LIST = "historyList";
    private static final String COLLECT_LIST = "collectList";


    /**
     * 获取历史搜索列表
     *
     * @return
     */
    public static List<POI> getHistoryJson2List(Context context) {
        String json = SPUtils.getInstance(context).getString(HISTORY_LIST, null);
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }

        return JsonUtils.json2PoiList(json);
    }

    /**
     * 获取收藏
     *
     * @return
     */
    public static List<POI> getCollectJson2List(Context context) {
        String collectJson = getCollectJson(context);
        if (StringUtils.isEmpty(collectJson)) {
            return new ArrayList<>();
        }

        return JsonUtils.json2PoiList(collectJson);
    }

    public static String getCollectJson(Context context) {
        return SPUtils.getInstance(context).getString(COLLECT_LIST, null);
    }

    /**
     * 存储单个POI到历史搜索列表
     *
     * @param poi
     */
    public static void savePoi2HistoryList(Context context, POI poi) {
        List<POI> historyList = getHistoryJson2List(context);
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        boolean isContains = false;
        for (POI history : historyList) {
            if (history.getPoiNO() == poi.getPoiNO()) {
                isContains = true;
            }
        }

        if (!isContains) {
            historyList.add(0, poi);
            saveHistoryList(context, historyList);
        }
    }

    /**
     * 存储集合到SH
     *
     * @param list
     */
    public static void saveHistoryList(Context context, List<POI> list) {
        SPUtils.getInstance(context)
                .put(HISTORY_LIST, JsonUtils.poiList2Json(list))
                .commit();
    }

    /**
     * 存储 收藏
     *
     * @param list
     */
    public static void saveCollectList(Context context, List<POI> list) {
        SPUtils.getInstance(context)
                .put(COLLECT_LIST, JsonUtils.poiList2Json(list))
                .commit();
    }

    /**
     * 清空历史搜索
     */
    public static void clearHistoryList(Context context) {
        SPUtils.getInstance(context)
                .put(HISTORY_LIST, "")
                .commit();
    }
}
