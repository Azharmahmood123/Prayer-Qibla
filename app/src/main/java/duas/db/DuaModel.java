package duas.db;

import java.io.Serializable;

public class DuaModel implements Serializable {

	private static final long serialVersionUID = 1L;

	// Table Columns names
	private String id;
	private String duaId;
	private String duaTitle;
	private String duaEnglish;
	private String duaUrdu;
	private String duaTransliteration;
	private String audioName;
	private String duaArabic;
	private String favourite;
	private String category;
	private String additionalInfo;
	private String reference;
	private String counter;

	public DuaModel(String id, String duaId, String duaTitle, String duaEnglish, String duaUrdu, String duaTransliteration, String audioName, String duaArabic, String favourite, String category, String additionalInfo, String reference, String counter) {
		super();
		this.id = id;
		this.duaId = duaId;
		this.duaTitle = duaTitle;
		this.duaEnglish = duaEnglish;
		this.duaUrdu = duaUrdu;
		this.duaTransliteration = duaTransliteration;
		this.audioName = audioName;
		this.duaArabic = duaArabic;
		this.favourite = favourite;
		this.category = category;
		this.additionalInfo = additionalInfo;
		this.reference = reference;
		this.counter = counter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDuaId() {
		return duaId;
	}

	public void setDuaId(String duaId) {
		this.duaId = duaId;
	}

	public String getDuaTitle() {
		return duaTitle;
	}

	public void setDuaTitle(String duaTitle) {
		this.duaTitle = duaTitle;
	}

	public String getDuaEnglish() {
		return duaEnglish;
	}

	public void setDuaEnglish(String uaEnglish) {
		this.duaEnglish = uaEnglish;
	}

	public String getDuaUrdu() {
		return duaUrdu;
	}

	public void setDuaUrdu(String duaUrdu) {
		this.duaUrdu = duaUrdu;
	}

	public String getDuaTransliteration() {
		return duaTransliteration;
	}

	public void setDuaTransliteration(String duaTransliteration) {
		this.duaTransliteration = duaTransliteration;
	}

	public String getAudioName() {
		return audioName;
	}

	public void setAudioName(String audioName) {
		this.audioName = audioName;
	}

	public String getDuaArabic() {
		return duaArabic;
	}

	public void setDuaArabic(String duaArabic) {
		this.duaArabic = duaArabic;
	}

	public String getFavourite() {
		return favourite;
	}

	public void setFavourite(String favourite) {
		this.favourite = favourite;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

}
