package co.cheez.cheez.event;

import org.json.JSONObject;

/**
 * Created by jiho on 5/28/15.
 */
public class PostReadEvent {
    public JSONObject data;
    public PostReadEvent(JSONObject jsonObject) {
        data = jsonObject;
    }
}
