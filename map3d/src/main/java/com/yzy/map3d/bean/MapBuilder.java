package com.yzy.map3d.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rtm.common.utils.RMStringUtils;
import com.yzy.map3d.MapIntentBuilder;
import com.yzy.map3d.util.StringUtils;

/**
 * @author 志尧
 * @date on 2018-12-19 15:17
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public class MapBuilder implements Parcelable {

    private Boolean debug;
    private String buildId;
    private String floor = "F1";
    private Float scaleLevel = Float.valueOf(4);
    private String searchName;
    private Boolean loading = true;
    private CompassBean compassBean;
    private Boolean showLogo = false;
    private Boolean hasCheck;
    private String checkMsg;
    private String checkFloor;
    private float[] checkPosition;
    private Boolean navigationFollow;

    public MapBuilder() {
    }

    public MapBuilder(MapIntentBuilder.Builder builder) {
        resetData(builder);
    }

    public void resetData() {
        resetData(null);
    }

    void resetData(MapIntentBuilder.Builder builder) {
        if (builder == null) {
            this.setFloor("F1");
            this.setScaleLevel(Float.valueOf(4));
            this.setLoading(true);
            this.setShowLogo(false);
            return;
        }

        if (builder.getDebug() != null) {
            this.setDebug(builder.getDebug());
        }

        if (!RMStringUtils.isEmpty(builder.getBuildId())) {
            this.setBuildId(builder.getBuildId());
        }

        if (!RMStringUtils.isEmpty(builder.getFloor())) {
            this.setFloor(builder.getFloor());
        }

        if (builder.getScaleLevel() != null) {
            this.setScaleLevel(builder.getScaleLevel());
        }

        if (!RMStringUtils.isEmpty(builder.getSearchName())) {
            this.setSearchName(builder.getSearchName());
        }

        if (builder.getLoading() != null) {
            this.setLoading(builder.getLoading());
        }

        if (builder.getShowCompass() != null) {
            CompassBean compassBean = new CompassBean();
            compassBean.setShowCompass(builder.getShowCompass());
            this.setCompassBean(compassBean);
        }

        if (builder.getShowLogo() != null) {
            this.setShowLogo(builder.getShowLogo());
        }

        if (builder.getHasCheck() != null) {
            if (!builder.getHasCheck()
                    || StringUtils.isEmpty(builder.getCheckMsg())
                    || StringUtils.isEmpty(builder.getCheckFloor())
                    || builder.getCheckPosition() == null
                    || builder.getCheckPosition().length != 4) {
                return;
            }

            this.setHasCheck(builder.getHasCheck());
            this.setCheckMsg(builder.getCheckMsg());
            this.setCheckFloor(builder.getCheckFloor());
            this.setCheckPosition(builder.getCheckPosition());
        }

        if (builder.getNavigationFollow() != null) {
            this.setNavigationFollow(builder.getNavigationFollow());
        }
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Float getScaleLevel() {
        return scaleLevel;
    }

    public void setScaleLevel(Float scaleLevel) {
        this.scaleLevel = scaleLevel;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public Boolean getLoading() {
        return loading;
    }

    public void setLoading(Boolean loading) {
        this.loading = loading;
    }

    public CompassBean getCompassBean() {
        return compassBean;
    }

    public void setCompassBean(CompassBean compassBean) {
        this.compassBean = compassBean;
    }

    public Boolean getShowLogo() {
        return showLogo;
    }

    public void setShowLogo(Boolean showLogo) {
        this.showLogo = showLogo;
    }

    public Boolean getHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(Boolean hasCheck) {
        this.hasCheck = hasCheck;
    }

    public String getCheckMsg() {
        return checkMsg;
    }

    public void setCheckMsg(String checkMsg) {
        this.checkMsg = checkMsg;
    }

    public String getCheckFloor() {
        return checkFloor;
    }

    public void setCheckFloor(String checkFloor) {
        this.checkFloor = checkFloor;
    }

    public float[] getCheckPosition() {
        return checkPosition;
    }

    public void setCheckPosition(float[] checkPosition) {
        this.checkPosition = checkPosition;
    }

    public Boolean getNavigationFollow() {
        return navigationFollow;
    }

    public void setNavigationFollow(Boolean navigationFollow) {
        this.navigationFollow = navigationFollow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.debug);
        dest.writeString(this.buildId);
        dest.writeString(this.floor);
        dest.writeValue(this.scaleLevel);
        dest.writeString(this.searchName);
        dest.writeValue(this.loading);
        dest.writeParcelable(this.compassBean, flags);
        dest.writeValue(this.showLogo);
        dest.writeValue(this.hasCheck);
        dest.writeString(this.checkMsg);
        dest.writeString(this.checkFloor);
        dest.writeFloatArray(this.checkPosition);
        dest.writeValue(this.navigationFollow);
    }

    protected MapBuilder(Parcel in) {
        this.debug = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.buildId = in.readString();
        this.floor = in.readString();
        this.scaleLevel = (Float) in.readValue(Float.class.getClassLoader());
        this.searchName = in.readString();
        this.loading = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.compassBean = in.readParcelable(CompassBean.class.getClassLoader());
        this.showLogo = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.hasCheck = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.checkMsg = in.readString();
        this.checkFloor = in.readString();
        this.checkPosition = in.createFloatArray();
        this.navigationFollow = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<MapBuilder> CREATOR = new Creator<MapBuilder>() {
        @Override
        public MapBuilder createFromParcel(Parcel source) {
            return new MapBuilder(source);
        }

        @Override
        public MapBuilder[] newArray(int size) {
            return new MapBuilder[size];
        }
    };
}
