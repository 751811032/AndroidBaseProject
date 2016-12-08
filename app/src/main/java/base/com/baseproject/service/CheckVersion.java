package base.com.baseproject.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import base.com.baseproject.R;
import base.com.baseproject.activity.BaseActivity;
import base.com.baseproject.util.HttpClient;

/**
 * Created by TC855 on 2016/6/3.
 */
public class CheckVersion extends AsyncTask<Object, Integer, Integer> {
    private BaseActivity activity;
    private ProgressBar mProgressBar;
    private int progress;
    private final int DOWNLOAD = 1;
    private final int DOWNLOADFINISH = 2;
    private static final String savePath = "/sdcard/GA/";
    private static final String saveFileName = savePath + "GA.apk";
    private static final String WEBSITE = "GA-Android";
    private int lastRate = 0;
    private boolean canceled = false;

    private int serviceCode;
    private String apkUrl;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DOWNLOAD:
                    mProgressBar.setProgress(progress);
                    break;
                case DOWNLOADFINISH:
                    installApk();
            }
        }
    };
    private Dialog mDownloadDialog;
    public CheckVersion(BaseActivity activity) {
        this.activity = activity;
    }

    /**
     * 更新版本号的入口
     */
    private void checkUpdate() {
        boolean isUpdate = isUpdata();
        Log.i("我的信息", "isUpdate:" + isUpdate);
        if (isUpdate) {
            showNoticeDialog();
        }
    }

    private boolean isUpdata() {
        int versionCode = getLocalVersion(activity);
        Log.i("我的信息", "isUpdate:" + versionCode + ";" + serviceCode);
        if (serviceCode > versionCode) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取本地版本号
     * @param context
     * @return
     */
    private int getLocalVersion(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取本地版本名称
     * @param context
     * @return
     */
    public String getLocalVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 更新对话框
     */
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.app_update);
        builder.setMessage(R.string.new_version);
        builder.setPositiveButton(R.string.update,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
//                        startDownloadView();
                        showDownloadDialog();
                    }
                });
//                .setNegativeButton(R.string.cancel,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//        builder.setCancelable(false);
        builder.show();
    }

//    private void startDownloadView() {
//        Intent it = new Intent(activity,
//                NotificationUpdateActivity.class);
//        activity.startActivity(it);
//    }

    private void showDownloadDialog(){
        //构造下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.app_update);
        //给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.update,null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.update_progressbar);
        builder.setView(v);
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        canceled = true;
                    }
                });
        mDownloadDialog = builder.create();
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
        downLoadApk();
    }
    private void downLoadApk(){
        new downloadApkThread().start();
    }

    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                //sd存在并有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    //获取文件大小
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    Log.i("我的信息", "下载完成" + length);
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String apkFile = saveFileName;
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;
                        Log.i("我的信息", "下载完成" + numread);
                        progress = (int) (((float) count / length) * 100);
                        if (progress >= lastRate + 1) {
                            mHandler.sendEmptyMessage(DOWNLOAD);
                            lastRate = progress;
                        }
                        //下载完成
                        if (numread <= 0) {
                            Log.i("我的信息", "下载完成");
                            mHandler.sendEmptyMessage(DOWNLOADFINISH);
                            canceled = true;
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!canceled);
                    fos.close();
                    is.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            mDownloadDialog.dismiss();
        }
    };
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        activity.startActivity(i);
    }
    @Override
    protected Integer doInBackground(Object... params) {
        int versionCode = apiCheckVersion();
        return versionCode;
    }

    //该方法运行在Ui线程内，可以对UI线程内的控件设置和修改其属性
    @Override
    protected void onPreExecute() {
        activity.showProgressDialog(activity.getResources().getString(R.string.loading));
    }

    //在doInBackground方法当中，每次调用publishProgrogress()方法之后，都会触发该方法
    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    //在doInBackground方法执行结束后再运行，并且运行在UI线程当中
    //主要用于将异步操作任务执行的结果展示给用户
    @Override
    protected void onPostExecute(Integer bean) {
        activity.hideProgressDialog();
        checkUpdate();
    }

    public int apiCheckVersion(){
        String jsonString = HttpClient.sendGet("http://www.855play.com/ipad/getMobileVersion.jsp?website=jxdaohang", "");
        Log.i("我的信息","jsonstirng:"+jsonString);
        if ("netError".equals(jsonString)||"".equals(jsonString)){
        }else {
            try {
                String[] strRes = jsonString.split("#");
                serviceCode = Integer.parseInt(strRes[0]);
                apkUrl = strRes[1];
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return serviceCode;
    }
}
