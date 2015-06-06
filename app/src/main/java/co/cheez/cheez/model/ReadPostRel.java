package co.cheez.cheez.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Created by jiho on 5/22/15.
 */
public class ReadPostRel extends Model{
    private long post; // post id
    private boolean liked;
    private boolean linkClicked;
    private boolean saved;
    private long viewStartedTime;
    private long viewEndedTime;
    private long linkOpenedTime;
    private long linkClosedTime;


    public static ReadPostRel fromJsonObject(JSONObject object) {
        return fromJsonString(object.toString());
    }

    public static ReadPostRel fromJsonString(String jsonString) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.fromJson(jsonString, ReadPostRel.class);
    }

    public boolean isLinkClicked() {
        return linkClicked;
    }

    public void setLinkClicked(boolean linkClicked) {
        this.linkClicked = linkClicked;
    }


    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public long getViewStartedTime() {
        return viewStartedTime;
    }

    public void setViewStartedTime(long viewStartedTime) {
        this.viewStartedTime = viewStartedTime;
    }

    public long getViewEndedTime() {
        return viewEndedTime;
    }

    public void setViewEndedTime(long viewEndedTime) {
        this.viewEndedTime = viewEndedTime;
    }

    public long getLinkOpenedTime() {
        return linkOpenedTime;
    }

    public void setLinkOpenedTime(long linkOpenedTime) {
        this.linkOpenedTime = linkOpenedTime;
    }

    public long getLinkClosedTime() {
        return linkClosedTime;
    }

    public void setLinkClosedTime(long linkClosedTime) {
        this.linkClosedTime = linkClosedTime;
    }

    public long getPost() {
        return post;
    }

    public void setPost(long post) {
        this.post = post;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
