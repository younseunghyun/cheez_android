package co.cheez.cheez.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.R;

/**
 * Created by jiho on 5/15/15.
 */
public class User {
    private long id;
    private long uploadCount;
    private boolean following;
    private String name;
    private String imageUrl;

    public static User fromJsonObject(JSONObject object) {
        return fromJsonString(object.toString());
    }

    public static User fromJsonString(String jsonString) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.fromJson(jsonString, User.class);
    }

    public JSONObject toJsonObject() throws JSONException {
        return new JSONObject(toJsonString());
    }

    public String toJsonString() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        if (name != null) {
            return name;
        } else {
            return "익명";
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDisplayImageUrl() {
        if (imageUrl == null) {
            return "drawable://" + R.drawable.ic_launcher;
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(long uploadCount) {
        this.uploadCount = uploadCount;
    }
}
