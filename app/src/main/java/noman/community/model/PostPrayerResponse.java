
package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PostPrayerResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("no_of_prayers")
    @Expose
    private Integer noOfPrayers;
    /**
     * 
     * @return
     *     The errorMessage
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param errorMessage
     *     The error_message
     */
    public void setMessage(String errorMessage) {
        this.message = errorMessage;
    }

    public Integer getNoOfPrayers() {
        return noOfPrayers;
    }

    public void setNoOfPrayers(Integer noOfPrayers) {
        this.noOfPrayers = noOfPrayers;
    }
}
