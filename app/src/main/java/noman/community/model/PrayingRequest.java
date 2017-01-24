
package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PrayingRequest {



    @SerializedName("action")
    @Expose
    private String action="updateUserPrayerCounters";

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("prayer_id")
    @Expose
    private String prayerId;
    @SerializedName("prayed_counter")
    @Expose
    private String prayedCounter;
    @SerializedName("inappropriate_counter")
    @Expose
    private String inappropriateCounter;

    /**
     * 
     * @return
     *     The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * @return
     *     The prayerId
     */
    public String getPrayerId() {
        return prayerId;
    }

    /**
     * 
     * @param prayerId
     *     The prayer_id
     */
    public void setPrayerId(String prayerId) {
        this.prayerId = prayerId;
    }

    /**
     * 
     * @return
     *     The prayedCounter
     */
    public String getPrayedCounter() {
        return prayedCounter;
    }

    /**
     * 
     * @param prayedCounter
     *     The prayed_counter
     */
    public void setPrayedCounter(String prayedCounter) {
        this.prayedCounter = prayedCounter;
    }

    /**
     * 
     * @return
     *     The inappropriateCounter
     */
    public String getInappropriateCounter() {
        return inappropriateCounter;
    }

    /**
     * 
     * @param inappropriateCounter
     *     The inappropriate_counter
     */
    public void setInappropriateCounter(String inappropriateCounter) {
        this.inappropriateCounter = inappropriateCounter;
    }

}
