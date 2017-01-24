
package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class SignUpResponse {

    @SerializedName("State")
    @Expose
    private Boolean state;
    @SerializedName("Response")
    @Expose
    private Response response;
    @SerializedName("Prayers")
    @Expose
    private List<Prayer> prayers = new ArrayList<Prayer>();

    /**
     * 
     * @return
     *     The state
     */
    public Boolean getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The State
     */
    public void setState(Boolean state) {
        this.state = state;
    }

    /**
     * 
     * @return
     *     The response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * 
     * @param response
     *     The Response
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * 
     * @return
     *     The prayers
     */
    public List<Prayer> getPrayers() {
        return prayers;
    }

    /**
     * 
     * @param prayers
     *     The Prayers
     */
    public void setPrayers(List<Prayer> prayers) {
        this.prayers = prayers;
    }

}
