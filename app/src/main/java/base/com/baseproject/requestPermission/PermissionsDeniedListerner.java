package base.com.baseproject.requestPermission;

import java.util.List;

/**
 * Created by TC855 on 2016/12/7.
 */


public interface PermissionsDeniedListerner {

    void onPermissionsGranted(int requestCode, List<String> perms);
}