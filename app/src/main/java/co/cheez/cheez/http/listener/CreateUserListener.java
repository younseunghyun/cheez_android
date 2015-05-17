package co.cheez.cheez.http.listener;

import com.android.volley.Response;

import org.json.JSONObject;

import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.model.User;

/**
 * Created by jiho on 5/17/15.
 */
public class CreateUserListener implements Response.Listener<JSONObject> {
    @Override
    public void onResponse(JSONObject response) {
        User user = User.fromJsonObject(response);
        Auth.getInstance().setUser(user);
    }
}
