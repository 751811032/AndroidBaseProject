package base.com.baseproject.requestPermission;

import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * Created by TC855 on 2016/12/7.
 */

public interface PermissionsCallbacks extends
        ActivityCompat.OnRequestPermissionsResultCallback {

    void onPermissionsGranted(int requestCode, List<String> perms);
    void onPermissionsDenied(int requestCode, List<String> perms);
}