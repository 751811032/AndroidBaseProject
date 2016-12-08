package com.updatelib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.updatelib.util.UpdateUtil;

import org.json.JSONObject;

import java.io.File;


///  https://github.com/czy1121/update/blob/master/library/src/main/java/ezy/boost/update/UpdateAgent.java

//https://github.com/AlexLiuSheng/CheckVersionLib
//        https://github.com/hugeterry/UpdateDemo/blob/master/updatefun/src/main/java/cn/hugeterry/updatefun/view/DownLoadDialog.java
//        https://github.com/chiclaim/android-app-update
//        https://github.com/zilianliuxue/AndroidStudy/blob/master/Android%20%E5%AE%9E%E6%88%98%E5%BC%80%E5%8F%91/%E5%AE%89%E5%8D%93%E5%BC%80%E5%8F%91%E5%AE%9E%E6%88%98%E4%B9%8Bapp%E4%B9%8B%E7%89%88%E6%9C%AC%E6%9B%B4%E6%96%B0(DownloadManager%E5%92%8Chttp%E4%B8%8B%E8%BD%BD)%E5%AE%8C%E6%95%B4%E5%AE%9E%E7%8E%B0.md

/**
 * Created by TC855 on 2016/12/7.
 */
public class UpdateParams {

    private Context mContext;

    //dialog 的视图
    private View dialogView;
    public View getDialogView() {
        return dialogView;
    }
    public void setDialogView(View dialogView) {
        this.dialogView = dialogView;
    }

    //更新失败或者取消更新的回调
    private OnFailureListener mOnFailureListener;
    public void setFailureListener(OnFailureListener failure) {
        if (failure != null) {
            mOnFailureListener = failure;
        }
    }

    //确定更新的回调函数
    private OnPromptListener mOnPromptListener;
    public void setPromptListener(OnPromptListener prompt) {
        if (prompt != null) {
            mOnPromptListener = prompt;
        }
    }

    //下载中的回调
    public interface OnProgressListener {
        void onStart();
        void onProgress(int progress);
        void onFinish();
    }

    //设置 静默安装的 监听器
    private OnProgressListener mOnProgressListener;
    public void setProgressListener(OnProgressListener listener) {
        if (listener != null) {
            mOnProgressListener = listener;
        }
    }

    //设置 通知栏更新的 监听器
    private OnProgressListener mOnNotificationListener;
    public void setNotifyListener(OnProgressListener listener) {
        if (listener != null) {
            mOnNotificationListener = listener;
        }
    }

    /**
     * @param context  上下文
     * @param checkurl  检查版本的地址
     * @param isSilent  是否静默下载安装
     * @param isForce   是否强制安装
     */
    public UpdateParams(Context context, String checkurl, boolean isSilent, boolean isForce) {
        mContext = context;
        checkUrl = checkurl;
        updateInfo = new UpdateInfo(isSilent, isForce);
        mOnPromptListener = new OnPrompt(context);
        mOnFailureListener = new OnFailure(context);
    }

    public interface OnFailureListener {
        void onFailure(UpdateError error);
    }

    public interface OnPromptListener {
        void onPrompt(UpdateParams agent);
    }

    //请求版本的地址   必须从外部设置
    private String checkUrl = "";

    public String getCheckUrl() {
        return checkUrl;
    }

    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }

    //版本信息
    private VersionInfo mInfo;

    public VersionInfo getmInfo() {
        return mInfo;
    }

    private void setInfo(VersionInfo info) {
        mInfo = info;
    }


    //更新信息详情
    private UpdateInfo updateInfo;

    protected UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    //解析接收的数据
    protected void parse(String source) {
        try {
            JSONObject myJsonObject = new JSONObject(source);
            setInfo(VersionInfo.parse(myJsonObject));
        } catch (Exception e) {
            e.printStackTrace();
            setError(new UpdateError(UpdateError.CHECK_PARSE));
        }
    }

    //请求或者下载出现异常
    private UpdateError mError = null;
    protected UpdateError getError() {
        Log.i("我的信息","checkFinish0000:"+mError);
        return mError;
    }
    protected void setError(UpdateError mError) {
        this.mError = mError;
    }

    //临时文件和apk文件
    private File mTmpFile;
    private File mApkFile;

    public void check() {
        if (UpdateUtil.checkNetwork(mContext)) {
            onCheck();
        } else {
            onFailure(new UpdateError(UpdateError.CHECK_NO_NETWORK));
        }
    }

    protected void onCheck() {
        new UpdateCheck(this).execute();
    }

    protected void checkFinish() {
        UpdateError error = null;
        if (mError==null){
        }else {
            error = getError();
        }
        if (error != null) {
            onFailure(error);
        } else {
            VersionInfo info = getmInfo();
            if (info == null) {
                onFailure(new UpdateError(UpdateError.CHECK_UNKNOWN));
            } else if (info.getCodeId() != 0) {
                onFailure(new UpdateError(UpdateError.CHECK_NETWORK_IO));
            } else {
                if (info.getAppVersion() > UpdateUtil.getVersionCode(mContext)) {
                    updateInfo.setHasUpdate(true);
                } else {
                    updateInfo.setHasUpdate(false);
                }
            }
            if (!updateInfo.isHasUpdate()) {
                onFailure(new UpdateError(UpdateError.UPDATE_NO_NEWER));
            } else {
                UpdateUtil.setUpdate(mContext);
                mTmpFile = new File(mContext.getExternalCacheDir(), UpdateUtil.getAppName(mContext));
                mApkFile = new File(mContext.getExternalCacheDir(), UpdateUtil.getAppName(mContext) + ".apk");
//                if (UpdateUtil.verify(mApkFile)) {
//                    onInstall();
//                } else
                if (updateInfo.isSilent()) {
                    onDownload();
                } else {
                    mOnPromptListener.onPrompt(this);
                }
            }
        }
    }

    private void onFailure(UpdateError error) {
        if (error.isError()) {
            mOnFailureListener.onFailure(error);
        }
    }

    private static class OnFailure implements OnFailureListener {
        private Context mContext;

        public OnFailure(Context context) {
            mContext = context;
        }

        @Override
        public void onFailure(UpdateError error) {
            UpdateUtil.log(error.toString());
            Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private static class OnPrompt implements UpdateParams.OnPromptListener {
        private Context mContext;

        public OnPrompt(Context context) {
            mContext = context;
        }

        @Override
        public void onPrompt(UpdateParams agent) {
            final VersionInfo info = agent.getmInfo();
            final UpdateInfo updateInfo = agent.getUpdateInfo();
//            String size = Formatter.formatShortFileSize(mContext, info.size);
            String content = String.format("最新版本："+info.getAppVersionName()+"更新内容:"+info.getUpdateContent());
            final AlertDialog dialog = new AlertDialog.Builder(mContext).create();

            if (agent.dialogView!=null){
                dialog.setView(agent.dialogView);

                DialogInterface.OnClickListener listener = new OnPromptClick(agent, true);
                if (updateInfo.isForce()) {
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
                } else {
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "立即更新", listener);
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "以后再说", listener);
                }
            }else {
                dialog.setTitle("应用更新");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                float density = mContext.getResources().getDisplayMetrics().density;
                TextView tv = new TextView(mContext);
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setVerticalScrollBarEnabled(true);
                tv.setTextSize(14);
                tv.setMaxHeight((int) (250 * density));
                dialog.setView(tv, (int) (25 * density), (int) (15 * density), (int) (25 * density), 0);

                DialogInterface.OnClickListener listener = new OnPromptClick(agent, true);
                if (updateInfo.isForce()) {
                    tv.setText("您需要更新应用才能继续使用\n\n" + content);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
                } else {
                    tv.setText(content);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "立即更新", listener);
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "以后再说", listener);
                }
            }
            dialog.show();
        }
    }

    public static class OnPromptClick implements DialogInterface.OnClickListener {
        private final UpdateParams updateParams;
        private final boolean mIsAutoDismiss;

        public OnPromptClick(UpdateParams agent, boolean isAutoDismiss) {
            updateParams = agent;
            mIsAutoDismiss = isAutoDismiss;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    updateParams.update();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    updateParams.ignore();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    // not now
                    break;
            }
            if (mIsAutoDismiss) {
                dialog.dismiss();
            }
        }
    }


    protected void update() {
        mApkFile = new File(mContext.getExternalCacheDir(), UpdateUtil.getAppName(mContext) + ".apk");
        if (UpdateUtil.verify(mApkFile)) {
            onInstall();
        } else {
            onDownload();
        }
    }

    protected void ignore() {
//        UpdateUtil.setIgnore(mContext, getInfo().md5);
    }

    protected void onDownload() {
        if (mOnNotificationListener == null) {
            mOnNotificationListener = new EmptyProgress();
        }
        if (mOnProgressListener == null) {
            mOnProgressListener = new DialogProgress(mContext);
        }
        Log.i("我的信息","onDownload");
        new UpdateDownloader(this, mContext, mInfo.getApkUrl(), mTmpFile).execute();
    }

    protected void onInstall() {
        UpdateUtil.install(mContext, mApkFile, updateInfo.isForce());
    }

    protected static class EmptyProgress implements UpdateParams.OnProgressListener {
        @Override
        public void onStart() {
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onProgress(int progress) {
        }
    }

    protected static class DialogProgress implements UpdateParams.OnProgressListener {
        private Context mContext;
        private ProgressDialog mDialog;

        public DialogProgress(Context context) {
            mContext = context;
        }

        @Override
        public void onStart() {
            mDialog = new ProgressDialog(mContext);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setMessage("下载中...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        public void onProgress(int i) {
            if (mDialog != null) {
                mDialog.setProgress(i);
            }
        }

        @Override
        public void onFinish() {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        }
    }

    public static class NotificationProgress implements UpdateParams.OnProgressListener {
        private Context mContext;
        private int mNotifyId;
        private NotificationCompat.Builder mBuilder;

        public NotificationProgress(Context context, int notifyId) {
            mContext = context;
            mNotifyId = notifyId;
        }

        @Override
        public void onStart() {
            if (mBuilder == null) {
                String title = "下载中 - " + mContext.getString(mContext.getApplicationInfo().labelRes);
                mBuilder = new NotificationCompat.Builder(mContext);
                mBuilder.setOngoing(true)
                        .setAutoCancel(false)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(mContext.getApplicationInfo().icon)
                        .setTicker(title)
                        .setContentTitle(title);
            }
            onProgress(0);
        }

        @Override
        public void onProgress(int progress) {
            if (mBuilder != null) {
                if (progress > 0) {
                    mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
                    mBuilder.setDefaults(0);
                }
                mBuilder.setProgress(100, progress, false);

                NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(mNotifyId, mBuilder.build());
            }
        }

        @Override
        public void onFinish() {
            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(mNotifyId);
        }
    }

    protected void downloadStart() {
        Log.i("我的信息","downloadStart");
        if (updateInfo.isSilent()) {
            mOnNotificationListener.onStart();
        } else {
            mOnProgressListener.onStart();
        }
    }

    protected void downloadProgress(int progress) {
        Log.i("我的信息","downloadFinish："+progress);
        if (updateInfo.isSilent()) {
            mOnNotificationListener.onProgress(progress);
        } else {
            mOnProgressListener.onProgress(progress);
        }
    }

    protected void downloadFinish() {
        Log.i("我的信息","downloadFinish");
        if (updateInfo.isSilent()) {
            mOnNotificationListener.onFinish();
        } else {
            mOnProgressListener.onFinish();
        }
        if (mError != null) {
            mOnFailureListener.onFailure(mError);
        } else {
            mTmpFile.renameTo(mApkFile);
            if (!updateInfo.isSilent()) {
                onInstall();
            }
        }
    }
}
