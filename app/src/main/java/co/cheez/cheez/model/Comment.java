package co.cheez.cheez.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jiho on 5/15/15.
 */
public class Comment extends Model {
    private long id;
    private String comment;
    private String created;


    private User user;


    public static Comment fromJsonObject(JSONObject object) {
        return fromJsonString(object.toString());
    }

    public static Comment fromJsonString(String jsonString) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(jsonString, Comment.class);

    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDisplayTime() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            Date date = format.parse(created);
            Date now = new Date();
            long secondsDiff = (now.getTime() - date.getTime()) / 1000;

            if (secondsDiff < 60) {
                return "지금";
            } else if (secondsDiff < 3600) {
                return (secondsDiff / 60) + "분 전";
            } else if (secondsDiff < 86400) {
                return (secondsDiff / 3600) + "시간 전";
            } else {
                return (secondsDiff / 86400) + "일 전";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return created;
        }
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

}
