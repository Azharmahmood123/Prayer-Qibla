package noman.Tasbeeh.model;

/**
 * Created by Administrator on 3/2/2017.
 */

public class TasbeehModel {
    int id;
    int count;
    int total;
    int totalCounterUpto;
    String tasbeehArabic;
    String tasbeehEng;
    String translation;
    String reference;
    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalCounterUpto() {
        return totalCounterUpto;
    }

    public void setTotalCounterUpto(int totalCounterUpto) {
        this.totalCounterUpto = totalCounterUpto;
    }

    public String getTasbeehArabic() {
        return tasbeehArabic;
    }

    public void setTasbeehArabic(String tasbeehArabic) {
        this.tasbeehArabic = tasbeehArabic;
    }

    public String getTasbeehEng() {
        return tasbeehEng;
    }

    public void setTasbeehEng(String tasbeehEng) {
        this.tasbeehEng = tasbeehEng;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
