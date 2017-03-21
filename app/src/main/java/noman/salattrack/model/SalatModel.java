package noman.salattrack.model;

/**
 * Created by Administrator on 3/17/2017.
 */

public class SalatModel {

    //Note following value used for
    // 0: Missed
    // 1: late
    // 2: Prayed

    private int id;
    private int date;
    private int month;
    private int year;
    private int user_id;
    private int fajar = 0;
    private int zuhar = 0;
    private int asar = 0;
    private int magrib = 0;
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
