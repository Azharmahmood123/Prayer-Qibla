
package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PrayingResponse {

    @SerializedName("State")
    @Expose
    private Boolean state;
    @SerializedName("Response")
    @Expose
    private PostPrayerResponse response;


    /**
     * @return The state
     */
    public Boolean getState() {
        return state;
    }

    /**
     * @param state The State
     */
    public void setState(Boolean state) {
        this.state = state;
    }

    /**
     * @return The response
     */
    public PostPrayerResponse getResponse() {
        return response;
    }

    /**
     * @param response The Response
     */
    public void setResponse(PostPrayerResponse response) {
        this.response = response;
    }


}
