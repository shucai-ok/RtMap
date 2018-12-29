package com.yzy.map3d.util;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 志尧
 * @date on 2018-02-02 16:18
 * @email 1417337180@qq.com
 * @describe
 * @ideas
 */

public class JsonUtils {

    /**
     * list to json
     *
     * @param list
     * @return
     */
    public static String list2Json(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        for (String str : list) {
            jsonArray.put(str);
        }

        return jsonArray.toString();
    }

    public static String poiList2Json(List<POI> list) {
        JSONArray jsonArray = new JSONArray();
        JSONObject tmpObj;

        try {
            for (POI poi : list) {
                tmpObj = new JSONObject();
                tmpObj.put("name", poi.getName());
                tmpObj.put("x", (int) poi.getX());
                tmpObj.put("y", (int) poi.getY());
                tmpObj.put("buildId", poi.getBuildId());
                tmpObj.put("floor", poi.getFloor());
                tmpObj.put("poiNO", poi.getPoiNO());
                jsonArray.put(tmpObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }


    /**
     * json to list
     *
     * @param json
     * @return
     */
    public static List<String> json2List(String json) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isEmpty(json)) {
            return list;
        }

        try {
            JSONArray myJsonArray = new JSONArray(json);
            for (int i = 0; i < myJsonArray.length(); i++) {
                list.add(myJsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<POI> json2PoiList(String json) {
        List<POI> list = new ArrayList<>();
        if (StringUtils.isEmpty(json)) {
            return list;
        }

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                POIExt poi = new POIExt();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                poi.setName(jsonObject.getString("name"));
                poi.setX(jsonObject.getInt("x"));
                poi.setY(jsonObject.getInt("y"));
                poi.setBuildId(jsonObject.getString("buildId"));
                poi.setFloor(jsonObject.getString("floor"));
                poi.setPoiNO(jsonObject.getInt("poiNO"));
                list.add(poi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
