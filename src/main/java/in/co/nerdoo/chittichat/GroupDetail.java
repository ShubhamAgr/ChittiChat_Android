package in.co.nerdoo.chittichat;

/**
 * Created by shubham on 18/11/16.
 */
public class GroupDetail {
    private String groupId;
    private String groupName;

    private String ProfilePictures;
    private  String group_about;
    private  String isOpen;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProfilePictures() {
        return ProfilePictures;
    }

    public void setProfilePictures(String profilePictures) {
        ProfilePictures = profilePictures;
    }

    public String getGroup_about() {
        return group_about;
    }

    public void setGroup_about(String group_about) {
        this.group_about = group_about;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }
}
