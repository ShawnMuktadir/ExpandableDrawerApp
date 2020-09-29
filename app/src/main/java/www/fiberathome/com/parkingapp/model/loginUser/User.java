package www.fiberathome.com.parkingapp.model.loginUser;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("fullname")
    private String fullName;

    @SerializedName("password")
    private String password;

    @SerializedName("mobile_no")
    private String mobileNo;

    @SerializedName("vehicle_no")
    private String vehicleNo;

    @SerializedName("profile_pic")
    private String profilePic;

    @SerializedName("old_password")
    private String old_password;

    @SerializedName("new_password")
    private String new_password;

    @SerializedName("confirm_password")
    private String confirm_password;

    public User(){

    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public User(int id, String fullName, String password, String mobileNo, String vehicleNo, String profilePic) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.mobileNo = mobileNo;
        this.vehicleNo = vehicleNo;
        this.profilePic = profilePic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}
