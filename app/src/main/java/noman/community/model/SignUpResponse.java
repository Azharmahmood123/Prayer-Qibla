
package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import noman.salattrack.model.SalatModel;


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


    @SerializedName("Salats")
    @Expose
    private List<SalatModel> Salats = new ArrayList<SalatModel>();


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

    public List<SalatModel> getSalats() {
        return Salats;
    }

    public void setSalats(List<SalatModel> salats) {
        Salats = salats;
    }
}
