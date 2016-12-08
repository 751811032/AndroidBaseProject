package com.updatelib;

import android.os.AsyncTask;
import android.util.Log;

import com.updatelib.util.UpdateUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by TC855 on 2016/12/7.
 */
class UpdateCheck extends AsyncTask<Void, Integer, Void> {

    final UpdateParams updateParams;
    public UpdateCheck(UpdateParams agent) {
        updateParams = agent;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(updateParams.getCheckUrl()).openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                Log.i("我的信息","返回结果:"+UpdateUtil.readString(connection.getInputStream()));
                String a = "{\n" +
                        "    \"appVersion\":2,\n" +
                        "    \"apkUrl\":\"https://ct855.com/855asia.apk\",\n" +
                        "    \"updateContent\":\"update\"\n" +
                        "}";
                updateParams.parse(a);
            }
        } catch (IOException e) {
            e.printStackTrace();
            updateParams.setError(new UpdateError(UpdateError.CHECK_NETWORK_IO));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        updateParams.checkFinish();
    }
}