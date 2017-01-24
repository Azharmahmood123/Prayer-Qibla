package com.quranreading.model;

/**
 * Created by cyber on 12/15/2016.
 */

public class PrayerTimeModel {

    private int hijri;
    private int dst;
    private String juristic;
    private int juristicIndex;
    private String convention;
    private int conventionNumber;

    public int getConventionPosition() {
        return conventionPosition;
    }

    public void setConventionPosition(int conventionPosition) {
        this.conventionPosition = conventionPosition;
    }

    private int conventionPosition;
    private int[] corrections = {-2, -1, 0, 0, 0, 4};
    private Double angleFajr;
    private Double angleIsha;

    public PrayerTimeModel() {
        hijri = 0;
        dst = 0;
        juristic = "Standard";
        juristicIndex = 1;
        convention = "MWL";
        conventionNumber = 3;
    }

    public int getHijri() {
        return hijri;
    }

    public void setHijri(int hijri) {
        this.hijri = hijri;
    }

    public int getDst() {
        return dst;
    }

    public void setDst(int dst) {
        this.dst = dst;
    }

    public String getJuristic() {
        return juristic;
    }

    public void setJuristic(String juristic) {
        this.juristic = juristic;
    }

    public int getJuristicIndex() {
        return juristicIndex;
    }

    public void setJuristicIndex(int juristicIndex) {
        this.juristicIndex = juristicIndex;
    }

    public String getConvention() {
        return convention;
    }

    public void setConvention(String convention) {
        this.convention = convention;
    }

    public int getConventionNumber() {
        return conventionNumber;
    }

    public void setConventionNumber(int conventionIndex) {
        this.conventionNumber = conventionIndex;
    }

    public int[] getCorrections() {
        return corrections;
    }

    public void setCorrections(int[] corrections) {
        this.corrections = corrections;
    }

    public Double getAngleFajr() {
        return angleFajr;
    }

    public void setAngleFajr(Double angleFajr) {
        this.angleFajr = angleFajr;
    }

    public Double getAngleIsha() {
        return angleIsha;
    }

    public void setAngleIsha(Double angleIsha) {
        this.angleIsha = angleIsha;
    }
}
