package quran.model;

public class SurahModel {
    private int bookMarkId = -1;
    private String arabicAyah = "";
    private String translation = "";
    private String transliteration = "";


    private int juzzIndex = -1;


    private int paraIndex = 0;

    public SurahModel(int bookMarkId, String arabicAyah, String translation, String transliteration) {
        this.bookMarkId = bookMarkId;
        this.arabicAyah = arabicAyah;
        this.translation = translation;
        this.transliteration = transliteration;
    }

    public int getBookMarkId() {
        return bookMarkId;
    }

    public void setBookMarkId(int bookMarkId) {
        this.bookMarkId = bookMarkId;
    }

    public String getArabicAyah() {
        return arabicAyah;
    }

    public void setArabicAyah(String arabicAyah) {
        this.arabicAyah = arabicAyah;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getTransliteration() {
        return transliteration;
    }

    public void setTransliteration(String transliteration) {
        this.transliteration = transliteration;
    }

    public int getJuzzIndex() {
        return juzzIndex;
    }

    public void setJuzzIndex(int juzzIndex) {
        this.juzzIndex = juzzIndex;
    }

    public int getParaIndex() {
        return paraIndex;
    }

    public void setParaIndex(int paraIndex) {
        this.paraIndex = paraIndex;
    }
}
