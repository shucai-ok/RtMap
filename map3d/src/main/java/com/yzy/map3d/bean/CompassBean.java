package com.yzy.map3d.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.yzy.map3d.enums.PositionEnum;

/**
 * @author 志尧
 * @date on 2018-12-19 16:51
 * @email yuzhiyao0912@gmail.com
 * @describe 使用 指南针
 * @ideas
 */
public class CompassBean implements Parcelable {

    /**
     * 显示指南针
     */
    private Boolean showCompass;
    /**
     * 指南针图片
     */
    private String compassIcon;
    /**
     * 指南针x，y
     */
    private float[] compassPosition;
    /**
     * 位置
     */
    private PositionEnum positionEnum;

    public Boolean getShowCompass() {
        return showCompass;
    }

    public void setShowCompass(Boolean showCompass) {
        this.showCompass = showCompass;
    }

    public String getCompassIcon() {
        return compassIcon;
    }

    public void setCompassIcon(String compassIcon) {
        this.compassIcon = compassIcon;
    }

    public float[] getCompassPosition() {
        return compassPosition;
    }

    public void setCompassPosition(float[] compassPosition) {
        this.compassPosition = compassPosition;
    }

    public PositionEnum getPositionEnum() {
        return positionEnum;
    }

    public void setPositionEnum(PositionEnum positionEnum) {
        this.positionEnum = positionEnum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.showCompass);
        dest.writeString(this.compassIcon);
        dest.writeFloatArray(this.compassPosition);
        dest.writeInt(this.positionEnum == null ? -1 : this.positionEnum.ordinal());
    }

    public CompassBean() {
    }

    protected CompassBean(Parcel in) {
        this.showCompass = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.compassIcon = in.readString();
        this.compassPosition = in.createFloatArray();
        int tmpPositionEnum = in.readInt();
        this.positionEnum = tmpPositionEnum == -1 ? null : PositionEnum.values()[tmpPositionEnum];
    }

    public static final Creator<CompassBean> CREATOR = new Creator<CompassBean>() {
        @Override
        public CompassBean createFromParcel(Parcel source) {
            return new CompassBean(source);
        }

        @Override
        public CompassBean[] newArray(int size) {
            return new CompassBean[size];
        }
    };
}
