package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 11/22/2016.
 */

public class MoveToTopRequest {

    @SerializedName("action")
    @Expose
    private String action="updateUserPrayerDateTime";

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("prayer_id")
    @Expose
    private String prayer_id;

    @SerializedName("device_date_time")
    @Expose
    private String device_date_time;

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

    public String getDevice_date_time() {
        return device_date_time;
    }

    public void setDevice_date_time(String device_date_time) {
        this.device_date_time = device_date_time;
    }
}
