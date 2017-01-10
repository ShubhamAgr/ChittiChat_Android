package in.co.nerdoo.chittichat;

/**
 * Created by shubham on 16/10/16.
 */
public class GroupsList {
    private String _id;
    private String group_name;
    private String group_category;
    private String group_about;
    private String group_profilePicture;
    private String role;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_category() {
        return group_category;
    }

    public void setGroup_category(String group_category) {
        this.group_category = group_category;
    }

    public String getGroup_about() {
        return group_about;
    }

    public void setGroup_about(String group_about) {
        this.group_about = group_about;
    }

    public String getGroup_profilePicture() {
        return group_profilePicture;
    }

    public void setGroup_profilePicture(String group_profilePicture) {
        this.group_profilePicture = group_profilePicture;
    }
}
