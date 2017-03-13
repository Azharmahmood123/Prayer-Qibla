package noman.searchquran.model;

/**
 * Created by Administrator on 2/22/2017.
 */

public class TopicModel {

    private int surahNo;
    private int versesNo;
    private int id;
    private int topicId;
    private int paraNo;
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getSurahNo() {
        return surahNo;
    }

    public void setSurahNo(int surahNo) {
        this.surahNo = surahNo;
    }

    public int getVersesNo() {
        return versesNo;
    }

    public void setVersesNo(int versesNo) {
        this.versesNo = versesNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getParaNo() {
        return paraNo;
    }

    public void setParaNo(int paraNo) {
        this.paraNo = paraNo;
    }
}
