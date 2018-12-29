package com.yzy.map3d.enums;

/**
 * @author 志尧
 * @date on 2018-12-19 16:58
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public enum PositionEnum {

    LEFT_TOP(0, "左上角"),
    LEFT_BOTTOM(1, "左下角"),
    RIGHT_TOP(2, "右上角"),
    RIGHT_BOTTOM(3, "右下角"),
    CENTER(4, "居中");

    private int code;
    private String msg;

    PositionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static PositionEnum getByValue(int value) {
        for (PositionEnum quality : values()) {
            if (quality.getCode() == value) {
                return quality;
            }
        }
        return null;
    }
}
