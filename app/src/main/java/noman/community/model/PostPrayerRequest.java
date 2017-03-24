package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 11/21/2016.
 */

public class PostPrayerRequest {

    @SerializedName("action")
    @Expose
    private String action="addUserPrayer";

    @SerializedName("user_id")
    @Expose
    private int user_id;
    @SerializedName("post_content")
    @Expose
    private String prayer;

    @SerializedName("location_status")
    @Expose
    private String location_status;


    @SerializedName("device_date_time")
    @Expose
    private String device_date_time;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPrayer() {
        return prayer;
    }

    public void setPrayer(String prayer) {
        this.prayer = prayer;
    }

    public String getLocation_status() {
        return location_status;
    }

    public void setLocation_status(String location_status) {
        this.location_status = location_status;
    }

    public String getDevice_date_time() {
        return device_date_time;
    }

    public void setDevice_date_time(String device_date_time) {
        this.device_date_time = device_date_time;
    }
}
