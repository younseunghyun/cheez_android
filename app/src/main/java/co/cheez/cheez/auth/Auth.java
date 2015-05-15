package co.cheez.cheez.auth;

import android.content.Context;
import android.content.SharedPreferences;

import co.cheez.cheez.App;
import co.cheez.cheez.model.User;

/**
 * Created by jiho on 5/15/15.
 */
public class Auth {
    private static Auth mInstance = new Auth();
    private static final String PREFERENCE_NAME = "auth";

    private String mAuthToken;
    private User mUser;

    public static Auth getInstance() {
        return mInstance;
    }

    private Auth() {
        loadDataFromPreference();
    }

    private void loadDataFromPreference() {
        SharedPreferences sharedPreferences = App.getContext()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
}
