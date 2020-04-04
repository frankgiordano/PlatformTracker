package us.com.plattrk.api.model;

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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
