package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 11/22/2016.
 */

public class GetAllPrayerRequest {
    @SerializedName("max_counter_order")
    @Expose
    private String max_counter_order;
    @SerializedName("action")
    @Expose
    private String action="getAllPrayers";

    public String getUserId() {
        return max_counter_order;
    }

    public void setUserId(String max_counter_order) {
        this.max_counter_order = max_counter_order;
    }


}
