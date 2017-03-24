package noman.salattrack.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import noman.community.model.PostPrayerResponse;

/**
 * Created by Administrator on 3/24/2017.
 */

public class SalatResponse {

        @SerializedName("State")
        @Expose
        private Boolean state;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
