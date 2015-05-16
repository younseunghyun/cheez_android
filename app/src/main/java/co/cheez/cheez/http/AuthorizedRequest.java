package co.cheez.cheez.http;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.http.listener.DefaultErrorListener;

/**
 * Created by jiho on 5/16/15.
 */
public class AuthorizedRequest extends JsonObjectRequest {

    public AuthorizedRequest(int method,
                             String url,
                             JSONObject jsonRequest,
                             Response.Listener<JSONObject> listener,
                             DefaultErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        String authToken = Auth.getInstance().getAuthToken();
        if (authToken != null) {
            headers.put("Authorization", "Token "+authToken);
        }
        return super.getHeaders();
    }
}
