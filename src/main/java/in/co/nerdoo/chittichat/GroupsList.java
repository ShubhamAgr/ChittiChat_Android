package in.co.nerdoo.chittichat;

/**
 * Created by shubham on 16/10/16.
 */
public class GroupsList {
    private String GroupId;
    private String UserRole;
//    private String groupName;
//    private String groupProfilePicture;

    public String getGroupImageUrl() {
        return GroupImageUrl;
    }

    public void setGroupImageUrl(String groupImageUrl) {
        GroupImageUrl = groupImageUrl;
    }

    private String GroupImageUrl;
    public String getUserRole() {
        return UserRole;
    }

    public void setUserRole(String userRole) {
        UserRole = userRole;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }
}
