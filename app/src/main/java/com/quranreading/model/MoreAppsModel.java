package com.quranreading.model;

public class MoreAppsModel {
	
	private String appName;
	private String appLink;
	private String appIcon;
	private int position;
	
	
	public MoreAppsModel() {
		// TODO Auto-generated constructor stub
	}
	
	public MoreAppsModel(String appName, String appLink, String appIcon, int position) {
		super();
		this.appName = appName;
		this.appLink = appLink;
		this.appIcon = appIcon;
		this.position = position;
	}

	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppLink() {
		return appLink;
	}
	public void setAppLink(String appLink) {
		this.appLink = appLink;
	}
	public String getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}

}
