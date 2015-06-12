package co.cheez.cheez.http.listener;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import co.cheez.cheez.util.MessageUtil;

/**
 * Created by jiho on 5/16/15.
 */
public class DefaultErrorListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO : default error handling
        try {
            JSONObject response = new JSONObject(new String(error.networkResponse.data));
            MessageUtil.showMessage(response.getString("message"));
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtil.showDefaultErrorMessage();
        }
    }
}
