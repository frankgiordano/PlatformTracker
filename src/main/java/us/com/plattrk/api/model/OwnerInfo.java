package us.com.plattrk.api.model;

import java.util.Comparator;

public class OwnerInfo {
	private String userName;
	private String displayName;

	public OwnerInfo(String userName, String displayName) {

		this.displayName = displayName;
		this.userName = userName;
	}
	
	public OwnerInfo(String displayName) {
		this.displayName = displayName;
	}


	public String getUserName() {
		return userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	public static Comparator<OwnerInfo> OwnerComparator = new Comparator<OwnerInfo>() {

		public int compare(OwnerInfo owner1, OwnerInfo owner2) {
			return owner1.getDisplayName().toUpperCase().compareTo(owner2.getDisplayName().toUpperCase());
		}

	};

}
