package co.cheez.cheez.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Created by jiho on 5/15/15.
 */
public class Post extends Model {
    private long id;
    private float rating;
    private float averageRating;
    private boolean saved;
    private String body;
    private String imageUrl;
    private String sourceUrl;
    private String subtitle;
    private String title;

    private User user;
    private ReadPostRel[] readPostRels;

    public static Post fromJsonObject(JSONObject object) {
        return fromJsonString(object.toString());
    }

    public static Post fromJsonString(String jsonString) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Post post = gson.fromJson(jsonString, Post.class);
        ReadPostRel[] readPostRels = post.getReadPostRels();
        if (readPostRels.length > 0) {
            post.setRating(readPostRels[0].getRating());
            post.setSaved(readPostRels[0].isSaved());
        } else {
            post.setRating(0f);
            post.setSaved(false);
        }


        return post;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public boolean isRated() {
        return rating > 0;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReadPostRel[] getReadPostRels() {
        return readPostRels;
    }

    public void setReadPostRels(ReadPostRel[] readPostRels) {
        this.readPostRels = readPostRels;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
