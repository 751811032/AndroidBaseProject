package com.updatelib;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by TC855 on 2016/12/7.
 */
public class VersionInfo {

    private int codeId;    //错误码
    private int appVersion;    //应用的版本
    private String appVersionName;    //应用的版本
    private String apkUrl;
    private String updateContent;   //更新内容

    public int getCodeId() {
        return codeId;
    }

    public void setCodeId(int codeId) {
        this.codeId = codeId;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public static VersionInfo parse(JSONObject o) {
        VersionInfo info = new VersionInfo();
        if (o == null) {
            return info;
        }

        info.codeId = o.optInt("codeId", 0);
        if (info.codeId!=0) {
            return info;
        }
        info.appVersion = o.optInt("appVersion", 0);
        info.appVersionName = o.optString("appVersionName", "");
        info.apkUrl = o.optString("apkUrl", "");
        info.updateContent = o.optString("updateContent", "");
        return info;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "codeId=" + codeId +
                ", appVersion=" + appVersion +
                ", appVersionName='" + appVersionName + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", updateContent='" + updateContent + '\'' +
                '}';
    }
}
