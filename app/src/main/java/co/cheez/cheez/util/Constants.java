package co.cheez.cheez.util;

/**
 * Created by jiho on 5/16/15.
 */
public interface Constants {
    interface Integers {
        int OS_TYPE_ANDROID = 1;
    }

    interface Keys {
        String DEVICE_ID = "device_id";
        String OS_TYPE = "os_type";
        String OS_VERSION = "os_version";

        String TOKEN = "token";
        String DEVICE = "device";
        String DEVICES = "devices";

        String URL = "url";
    }

    interface URLs {
        String BASE = "https://api.cheez.co/";
        String POST = BASE + "post/";
        String USER = BASE + "user/";
        String AUTH_TOKEN = BASE + "api-auth-token/";
        String OG = BASE + "og/";
    }
}
