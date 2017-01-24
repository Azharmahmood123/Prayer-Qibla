package com.quranreading.model;

public class MenuDrawerModel 
{
	private boolean showViewHeading;
	private boolean showViewIcon;
	private boolean showViewLine;
	private String rowText;
	private int position;
	
	public MenuDrawerModel(boolean showViewHeading, boolean showViewIcon, boolean showViewLine, String rowText, int position)
	{
		this.showViewHeading = showViewHeading;
		this.showViewIcon = showViewIcon;
		this.showViewLine = showViewLine;
		this.rowText = rowText;
		this.position = position;
	}
	
	public boolean isViewHeading() 
	{
		return showViewHeading;
	}
	
	public void setViewHeading(boolean viewHeading) 
	{
		this.showViewHeading = viewHeading;
	}
	
	public boolean isViewIcon()
	{
		return showViewIcon;
	}
	
	public void setViewIcon(boolean viewIcon) 
	{
		this.showViewIcon = viewIcon;
	}
	
	public boolean isViewLine() 
	{
		return showViewLine;
	}
	
	public void setViewLine(boolean viewLine) 
	{
		this.showViewLine = viewLine;
	}

	public String getRowText() 
	{
		return rowText;
	}

	public void setRowText(String rowText) 
	{
		this.rowText = rowText;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	
}
