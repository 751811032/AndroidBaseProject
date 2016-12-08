package base.com.baseproject.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.updatelib.UpdateParams;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.List;

import base.com.baseproject.R;
import base.com.baseproject.requestPermission.PermissionsCallbacks;
import base.com.baseproject.requestPermission.PermissionsRequest;

/**
 * Created by TC855 on 2016/12/7.
 */
public class StartActivity extends AutoLayoutActivity implements PermissionsCallbacks {

    private UpdateParams updateParams;

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PackageManager pm = getPackageManager();

        boolean permission2 = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()));

        if (!permission2) {
            requestFilePermission();
        }

        updateParams = new UpdateParams(this,"http://www.855play.com/ipad/getMobileVersion.jsp?website=ct855",false,false);
//        updateParams.setNotifyListener(new UpdateParams.NotificationProgress(this, 998));
//        updateParams.setFailureListener(mOnFailureListener);
//        updateParams.setPromptListener(mOnPromptListener);
//        updateParams.setProgressListener(mOnProgressListener);
        updateParams.setNotifyListener(new UpdateParams.NotificationProgress(this, 998));
        View view = View.inflate(this,R.layout.dialogview,null);
        //不设置会有默认界面
        updateParams.setDialogView(view);
        updateParams.check();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsRequest.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestFilePermission() {
        PermissionsRequest.requestPermissions(this, getString(R.string.prompt_request_file), 1, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPhonePermission() {
        PermissionsRequest.requestPermissions(this, getString(R.string.prompt_request_phone), 1, Manifest.permission.CALL_PHONE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode){
            case 0:   //相机权限 g

                break;
            case 1:   //文件读写
            case 2:

                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode){
            case 1:   //文件读写
            case 2:
                Toast.makeText(this, getResources().getString(R.string.prompt_file_been_denied), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
