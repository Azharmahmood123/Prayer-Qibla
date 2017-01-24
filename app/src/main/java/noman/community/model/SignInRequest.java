package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 11/18/2016.
 */

public class SignInRequest {

    private String user_id;
    @SerializedName("action")
    @Expose
    private String action="userLogIn";


    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_login_id")
    @Expose
    private String user_login_id;

    @SerializedName("mode")
    @Expose
    private String mode;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("name")
    @Expose
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_login_id() {
        return user_login_id;
    }

    public void setUser_login_id(String user_login_id) {
        this.user_login_id = user_login_id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
