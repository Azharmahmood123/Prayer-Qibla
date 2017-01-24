
package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Prayer {

    @SerializedName("prayer_id")
    @Expose
    private String prayerId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("prayed_counter")
    @Expose
    private String prayedCounter;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("date_posted")
    @Expose
    private String datePosted;
    @SerializedName("prayer_type")
    @Expose
    private String prayerType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("inappropriate_counter")
    @Expose
    private String inappropriate_counter;
    @SerializedName("location_status")
    @Expose
    private String location_status;


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
     *     The content
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param content
     *     The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 
     * @return
     *     The datePosted
     */
    public String getDatePosted() {
        return datePosted;
    }

    /**
     * 
     * @param datePosted
     *     The date_posted
     */
    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    /**
     * 
     * @return
     *     The prayerType
     */
    public String getPrayerType() {
        return prayerType;
    }

    /**
     * 
     * @param prayerType
     *     The prayer_type
     */
    public void setPrayerType(String prayerType) {
        this.prayerType = prayerType;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInappropriate_counter() {
        return inappropriate_counter;
    }

    public void setInappropriate_counter(String inappropriate_counter) {
        this.inappropriate_counter = inappropriate_counter;
    }

    public String getLocation_status() {
        return location_status;
    }

    public void setLocation_status(String location_status) {
        this.location_status = location_status;
    }
}
