package co.cheez.cheez.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiho on 6/4/15.
 */
public class Model {
    private static Gson mGson;

    public static Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
        }
        return mGson;
    }

    public String toJsonString() {
        return getGson().toJson(this);
    }

    public JSONObject toJsonObject() throws JSONException {
        return new JSONObject(toJsonString());
    }

}
