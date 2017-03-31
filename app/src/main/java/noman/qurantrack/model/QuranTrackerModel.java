package noman.qurantrack.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 3/17/2017.
 */

public class QuranTrackerModel {

    //Note following value used for
    // 0: Missed
    // 1: late
    // 2: Prayed


   /* @SerializedName("action")
    @Expose
    private String action="addUserSalat";*/


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


    private int surahNo;
    private int ayahNo;
    private int verses;

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

    public int getSurahNo() {
        return surahNo;
    }

    public void setSurahNo(int surahNo) {
        this.surahNo = surahNo;
    }

    public int getAyahNo() {
        return ayahNo;
    }

    public void setAyahNo(int ayahNo) {
        this.ayahNo = ayahNo;
    }

    public int getVerses() {
        return verses;
    }

    public void setVerses(int verses) {
        this.verses = verses;
    }
}
