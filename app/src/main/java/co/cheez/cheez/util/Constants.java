package co.cheez.cheez.util;

/**
 * Created by jiho on 5/16/15.
 */
public interface Constants {
    interface Integers {
        int OS_TYPE_ANDROID = 1;
        int TIMEOUT_LONG = 20000;
        int SNS_TYPE_FACEBOOK = 1;
        int TIMEOUT_DEFAULT = 5000;
    }

    interface Keys {
        String DEVICE_ID = "device_id";
        String OS_TYPE = "os_type";
        String OS_VERSION = "os_version";

        String TOKEN = "token";
        String DEVICE = "device";
        String DEVICES = "devices";

        String URL = "url";
        String TITLE = "title";
        String SUBTITLE = "subtitle";
        String IMAGE_URL = "image_url";
        String TAGS = "tags";
        String SOURCE_URL = "source_url";
        String RESULTS = "results";
        String POST_ID = "post_id";
        String LINK_CLICKED = "link_clicked";
        String SNS_USER_ID = "sns_user_id";
        String SNS_TYPE = "sns_type";
        String SNS_PROFILE_URL = "sns_profile_url";
        String SNS_ACCOUNTS = "sns_accounts";
        String EMAIL = "email";
        String NAME = "name";
        String RATING = "rating";
        String PAGE = "page";
        String USER_ID = "user_id";
        String REASON = "reason";
        String SAVED = "saved";
        String COMMENT = "comment";
        String DATA = "data";
        String PUSH_TOKEN = "push_token";
        String DELETE = "delete";
        String STATE_MESSAGE = "state_message";
        String PROFILE_IMAGE = "profile_image";
    }

    interface URLs {
        String BASE = "https://api.cheez.co/";
        String POST = BASE + "post/";
        String USER = BASE + "user/";
        String AUTH_TOKEN = BASE + "api-auth-token/";
        String OG = BASE + "og/";
        String READ_POST = BASE + "read-post/";
        String REPORT = BASE + "report/";
        String SAVED_POST = BASE + "saved-post/";
        String COMMENT = BASE + "comment/";
        String SHARE = "http://bite.cheez.co/";
        String NO_IMAGE = "https://d5pd307bf8hwt.cloudfront.net/no_image.png";
        String PUSH_TOKEN = BASE + "push-token/";
        String FOLLOW = BASE + "follow/";
        String EDIT_PROFILE = BASE + "edit-profile/";
    }
}
