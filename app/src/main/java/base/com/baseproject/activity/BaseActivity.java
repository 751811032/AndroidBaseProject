package base.com.baseproject.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhy.autolayout.AutoLayoutActivity;

import base.com.baseproject.R;


public class BaseActivity extends AutoLayoutActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showProgressDialog(String title) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppTheme);
//            progressDialog.set
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setCancelable(true);
            progressDialog.dismiss();
        }
        Log.i("我的信息", "执行了hideProgressDialog");
        progressDialog = null;
    }

    /**
     * handler处理消息机制
     */
    protected Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Log.i("我的信息", "handleMessage");
                    break;
            }
        }
    };


}
