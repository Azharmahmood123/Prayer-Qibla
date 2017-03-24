
package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("no_of_prayers")
    @Expose
    private Integer noOfPrayers;
    @SerializedName("user_id")
    @Expose
    private int user_id;

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The noOfPrayers
     */
    public Integer getNoOfPrayers() {
        return noOfPrayers;
    }

    /**
     * @param noOfPrayers The no_of_prayers
     */
    public void setNoOfPrayers(Integer noOfPrayers) {
        this.noOfPrayers = noOfPrayers;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
