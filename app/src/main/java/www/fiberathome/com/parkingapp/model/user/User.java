package www.fiberathome.com.parkingapp.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class User {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("fullname")
    @Expose
    private String fullName;

    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;

    @SerializedName("vehicle_no")
    @Expose
    private String vehicleNo;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("status")
    @Expose
    private Integer status;

    public User() {
    }

    public User(Integer id, String fullName, String mobileNo, String vehicleNo, String image, String createdAt, Integer status) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNo = mobileNo;
        this.vehicleNo = vehicleNo;
        this.image = image;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
