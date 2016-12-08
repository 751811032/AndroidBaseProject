package com.updatelib;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TC855 on 2016/12/7.
 */
public class UpdateInfo {
    // 是否有新版本
    private boolean hasUpdate = false;
    // 是否静默下载：有新版本时不提示 直接下载，下次启动时安装
    private boolean isSilent = false;
    // 是否强制安装：不安装无法使用app
    private boolean isForce = false;
    // 是否可忽略该版本
    private boolean isIgnorable = true;
    // 是否是增量补丁包
    private boolean isPatch = false;

    private String md5;
    private long size;

    public UpdateInfo(boolean isSilent, boolean isForce ) {
        this.isSilent = isSilent;
        this.isForce = isForce;
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public boolean isSilent() {
        return isSilent;
    }

    public void setIsSilent(boolean isSilent) {
        this.isSilent = isSilent;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setIsForce(boolean isForce) {
        this.isForce = isForce;
    }

    public boolean isIgnorable() {
        return isIgnorable;
    }

    public void setIsIgnorable(boolean isIgnorable) {
        this.isIgnorable = isIgnorable;
    }

    public boolean isPatch() {
        return isPatch;
    }

    public void setIsPatch(boolean isPatch) {
        this.isPatch = isPatch;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}