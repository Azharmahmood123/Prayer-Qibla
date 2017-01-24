package noman.community.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GraphApiResponse {

    @SerializedName ("last_name")
    @Expose
    private String lastName;
    @SerializedName ("id")
    @Expose
    private String id;
    @SerializedName ("image")
    @Expose
    private String image;
    @SerializedName ("first_name")
    @Expose
    private String firstName;
    @SerializedName ("email")
    @Expose
    private String email;
    @SerializedName ("name")
    @Expose
    private String name;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @SerializedName ("location")
    @Expose
    private String location;
    /**
     * @return The lastName
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * @param lastName The last_name
     */
    public void setLastName (String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The id
     */
    public String getId () {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * @return The image
     */
    public String getImage () {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage (String image) {
        this.image = image;
    }

    /**
     * @return The firstName
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * @param firstName The first_name
     */
    public void setFirstName (String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The email
     */
    public String getEmail () {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail (String email) {
        this.email = email;
    }

    /**
     * @return The name
     */
    public String getName () {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName (String name) {
        this.name = name;
    }

}
