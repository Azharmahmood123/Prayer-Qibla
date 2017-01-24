package quran.model;

public class downloadDataModel 
{
	private int surahPosition;
	private long refrenceId;
	private String tempName;
	private String finalName;
	private int reciter;
	
	public downloadDataModel(int surahPosition, long refrenceId, String tempName, String finalName, int reciter)
	{
		this.surahPosition = surahPosition;
		this.refrenceId = refrenceId;
		this.tempName = tempName;
		this.finalName = finalName;
		this.reciter = reciter;
	}

	public int getSurahPosition() 
	{
		return surahPosition;
	}
	
	public void setSurahPosition(int surahPosition)
	{
		this.surahPosition = surahPosition;
	}
	
	public long getRefrenceId() 
	{
		return refrenceId;
	}
	
	public void setRefrenceId(long refrenceId) 
	{
		this.refrenceId = refrenceId;
	}
	
	public String getTempName() 
	{
		return tempName;
	}
	
	public void setTempName(String tempName) 
	{
		this.tempName = tempName;
	}
	
	public String getFinalName() 
	{
		return finalName;
	}
	
	public void setFinalName(String finalName) 
	{
		this.finalName = finalName;
	}

	public int getReciter() 
	{
		return reciter;
	}

	public void setReciter(int reciter) 
	{
		this.reciter = reciter;
	}
	
	
}
