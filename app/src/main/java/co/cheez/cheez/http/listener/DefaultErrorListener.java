package co.cheez.cheez.http.listener;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by jiho on 5/16/15.
 */
public class DefaultErrorListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO : default error handling
        Log.e("error", error.toString());
    }
}
