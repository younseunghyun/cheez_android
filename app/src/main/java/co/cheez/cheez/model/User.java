package co.cheez.cheez.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import co.cheez.cheez.R;

/**
 * Created by jiho on 5/15/15.
 */
public class User extends Model {
    private long id;
    private long uploadCount;
    private long followerCount;
    private long followeeCount;
    private boolean following;
    private String name;
    private String profileImage;
    private long[] followers;

    public static User fromJsonObject(JSONObject object) {
        return fromJsonString(object.toString());
    }

    public static User fromJsonString(String jsonString) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();


        User user = gson.fromJson(jsonString, User.class);

        // followers에는 본인의 id만 들어가도록 서버에서 처리되어있기 때문에..
        user.setFollowing(user.getFollowers().length > 0);
        return user;
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

    public String getProfileImage() {
        return profileImage;
    }

    public String getDisplayImageUrl() {
        if (profileImage == null) {
            return "drawable://" + R.drawable.ic_launcher;
        }
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(long uploadCount) {
        this.uploadCount = uploadCount;
    }

    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    public long getFolloweeCount() {
        return followeeCount;
    }

    public void setFolloweeCount(long followeeCount) {
        this.followeeCount = followeeCount;
    }

    public long[] getFollowers() {
        return followers;
    }

    public void setFollowers(long[] followers) {
        this.followers = followers;
    }


}
