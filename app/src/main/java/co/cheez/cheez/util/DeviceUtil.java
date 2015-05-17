package co.cheez.cheez.util;

import android.os.Build;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.App;

/**
 * Created by jiho on 5/16/15.
 */
public class DeviceUtil {
    public static String getDeviceId() {
        return Settings.Secure.getString(
                App.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static JSONObject getDeviceInfo() throws JSONException {
        /*
        '/user/',
            {
                "devices": [{
                    "device_id": "unique_device_id",
                    "os_type": 1,
                }]
            },
         */
        return new JSONObject()
                .put(Constants.Keys.DEVICE_ID, getDeviceId())
                .put(Constants.Keys.OS_TYPE, Constants.Integers.OS_TYPE_ANDROID)
                .put(Constants.Keys.OS_VERSION, getOSVersion());


    }
}
