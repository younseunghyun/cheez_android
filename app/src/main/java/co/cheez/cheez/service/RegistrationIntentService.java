package co.cheez.cheez.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.DeviceUtil;

/**
 * Created by jiho on 6/4/15.
 */
public class RegistrationIntentService extends IntentService {
    private static final String PREFERENCE_NAME = "gcm_token";
    private static final String SENT_TOKEN_TO_SERVER = "token_sent";
    private static final String NAME = "gcm_reg_service";


    public RegistrationIntentService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Initially this call goes out to the network to retrieve the token, subsequent calls
        // are local.
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // Subscribe to topic channels
            // subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            SharedPreferences sharedPreferences
                    = getTokenPreferences();
            boolean sent = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
            if (!sent) {
                sendRegistrationToServer(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences getTokenPreferences() {
        return getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private void sendRegistrationToServer(String token) {
        try {
            JSONObject deviceData = DeviceUtil.getDeviceInfo();
            deviceData.put(Constants.Keys.PUSH_TOKEN, token);
            Request request = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.URLs.PUSH_TOKEN,
                    deviceData,
                    new DefaultListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            super.onResponse(response);
                            getTokenPreferences()
                                    .edit()
                                    .putBoolean(SENT_TOKEN_TO_SERVER, true)
                                    .apply();
                        }
                    },
                    new DefaultErrorListener()
                    );
            App.addRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
