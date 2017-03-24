package noman.salattrack.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 3/17/2017.
 */

public class SalatModel {

    //Note following value used for
    // 0: Missed
    // 1: late
    // 2: Prayed


    @SerializedName("action")
    @Expose
    private String action="addUserSalat";


    private int id;
    @SerializedName("day")
    @Expose
    private int date;
    @SerializedName("month")
    @Expose
    private int month;
    @SerializedName("year")
    @Expose
    private int year;
    @SerializedName("user_id")
    @Expose
    private int user_id;
    @SerializedName("fajar")
    @Expose
    private int fajar = 0;
    @SerializedName("zuhar")
    @Expose
    private int zuhar = 0;
    @SerializedName("asar")
    @Expose
    private int asar = 0;
    @SerializedName("magrib")
    @Expose
    private int magrib = 0;
    @SerializedName("isha")
    @Expose
    private int isha = 0;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFajar() {
        return fajar;
    }

    public void setFajar(int fajar) {
        this.fajar = fajar;
    }

    public int getZuhar() {
        return zuhar;
    }

    public void setZuhar(int zuhar) {
        this.zuhar = zuhar;
    }

    public int getAsar() {
        return asar;
    }

    public void setAsar(int asar) {
        this.asar = asar;
    }

    public int getMagrib() {
        return magrib;
    }

    public void setMagrib(int magrib) {
        this.magrib = magrib;
    }

    public int getIsha() {
        return isha;
    }

    public void setIsha(int isha) {
        this.isha = isha;
    }
}
