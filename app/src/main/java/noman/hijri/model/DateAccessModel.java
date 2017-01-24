package noman.hijri.model;

public class DateAccessModel
{
	public String date = "";
	public int monthNo;
	public String monthName = "";
	public String year = "";
	public String status = "";
	public int eventIndex;
	
	public DateAccessModel(String date, int monthNo, String monthName, String year, String status,
						   int eventIndex)
	{
		this.date = date;
		this.monthNo = monthNo;
		this.monthName = monthName;
		this.year = year;
		this.status = status;
		this.eventIndex = eventIndex;
	}
}
