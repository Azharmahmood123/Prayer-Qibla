package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 11/22/2016.
 */

public class DeletePrayerRequest {


    @SerializedName("action")
    @Expose
    private String action="deleteUserPrayer";

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("prayer_id")
    @Expose
    private String prayer_id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrayer_id() {
        return prayer_id;
    }

    public void setPrayer_id(String prayer_id) {
        this.prayer_id = prayer_id;
    }
}
