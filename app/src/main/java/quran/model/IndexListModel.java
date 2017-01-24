package quran.model;

public class IndexListModel {
	private int surahNo;
	private int totalVerses;
	private String engSurahName;
	private String arabicSurahName;
	private String placeOfRevelation;
	private int itemPosition;



	private String paraIndex="";



	public IndexListModel(int surahNo, String engName, String arabicName, String revealedPlace, int totalVerses, int itemPosition) {
		this.surahNo = surahNo;
		this.engSurahName = engName;
		this.arabicSurahName = arabicName;
		this.placeOfRevelation = revealedPlace;
		this.totalVerses = totalVerses;
		this.itemPosition = itemPosition;
	}

	public int getSurahSize() {
		return totalVerses;
	}

	public void setSurahSize(int surahSize) {
		this.totalVerses = surahSize;
	}

	public String getEngSurahName() {
		return engSurahName;
	}

	public void setEngSurahName(String engSurahName) {
		this.engSurahName = engSurahName;
	}

	public String getArabicSurahName() {
		return arabicSurahName;
	}

	public void setArabicSurahName(String arabicSurahName) {
		this.arabicSurahName = arabicSurahName;
	}

	public String getPlaceOfRevelation() {
		return placeOfRevelation;
	}

	public void setPlaceOfRevelation(String placeOfRevelation) {
		this.placeOfRevelation = placeOfRevelation;
	}

	public int getSurahNo() {
		return surahNo;
	}

	public void setSurahNo(int surahNo) {
		this.surahNo = surahNo;
	}

	public int getItemPosition() {
		return itemPosition;
	}

	public void setItemPosition(int itemPosition) {
		this.itemPosition = itemPosition;
	}
	public String getParaIndex() {
		return paraIndex;
	}

	public void setParaIndex(String paraIndex) {
		this.paraIndex = paraIndex;
	}
}
