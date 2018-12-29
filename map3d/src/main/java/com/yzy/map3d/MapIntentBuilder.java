package com.yzy.map3d;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.rtm.location.LocationApp;
import com.yzy.map3d.bean.MapBuilder;
import com.yzy.map3d.ui.main.MapActivity;


/**
 * @author 志尧
 * @date on 2018-12-19 14:04
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public class MapIntentBuilder {

    private Context mContext;
    private Intent mIntent;

    public MapIntentBuilder(Context mContext) {
        this.mContext = mContext;
        mIntent = getIntent(mContext, MapActivity.class);
    }

    private Intent getIntent(Context context, Class<? extends Activity> clazz) {
        return mIntent = new Intent(context, clazz);
    }

    public static class Builder {
        private Context context;
        private Boolean debug;
        private String buildId;
        private String floor;
        private Float scaleLevel;
        private String searchName;
        private Boolean loading;
        private Boolean showCompass;
        private Boolean showLogo;
        private Boolean hasCheck;
        private String checkMsg;
        private String checkFloor;
        private float[] checkPosition;
        private Boolean navigationFollow;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDebug(Boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setBuildId(final String buildId) {
            this.buildId = buildId;
            return this;
        }

        public Builder setFloor(final String floor) {
            this.floor = floor;
            return this;
        }

        public Builder setScaleLevel(Float scaleLevel) {
            this.scaleLevel = scaleLevel;
            return this;
        }

        public Builder setSearchName(String searchName) {
            this.searchName = searchName;
            return this;
        }

        public Builder setLoading(Boolean loading) {
            this.loading = loading;
            return this;
        }

        public Builder setShowCompass(Boolean showCompass) {
            this.showCompass = showCompass;
            return this;
        }

        public Builder setShowLogo(Boolean showLogo) {
            this.showLogo = showLogo;
            return this;
        }

        public Builder setHasCheck(Boolean hasCheck) {
            this.hasCheck = hasCheck;
            return this;
        }

        public Builder setCheckMsg(String checkMsg) {
            this.checkMsg = checkMsg;
            return this;
        }

        public Builder setCheckFloor(String checkFloor) {
            this.checkFloor = checkFloor;
            return this;
        }

        public Builder setCheckPosition(float[] checkPosition) {
            this.checkPosition = checkPosition;
            return this;
        }

        public Builder setNavigationFollow(Boolean navigationFollow) {
            this.navigationFollow = navigationFollow;
            return this;
        }

        public Intent build() {
            return new MapIntentBuilder(context)
                    .setBuilder(this)
                    .build();
        }

        public Boolean getDebug() {
            return debug;
        }

        public String getBuildId() {
            return buildId;
        }

        public String getFloor() {
            return floor;
        }

        public Float getScaleLevel() {
            return scaleLevel;
        }

        public String getSearchName() {
            return searchName;
        }

        public Boolean getLoading() {
            return loading;
        }

        public Boolean getShowCompass() {
            return showCompass;
        }

        public Boolean getShowLogo() {
            return showLogo;
        }

        public Boolean getHasCheck() {
            return hasCheck;
        }

        public String getCheckMsg() {
            return checkMsg;
        }

        public String getCheckFloor() {
            return checkFloor;
        }

        public float[] getCheckPosition() {
            return checkPosition;
        }

        public Boolean getNavigationFollow() {
            return navigationFollow;
        }
    }

    MapIntentBuilder setBuilder(Builder builder) {
        MapBuilder mapBuilder = new MapBuilder(builder);
        mIntent.putExtra(MapActivity.MAP_BUILDER, mapBuilder);
        return this;
    }

    Intent build() {
        if (!(mContext instanceof Activity)) {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return mIntent;
    }

    public static void cleanMap() {
        if (!LocationApp.getInstance().isStartLocate()) {
            return;
        }
        LocationApp.getInstance().stop();
    }
}