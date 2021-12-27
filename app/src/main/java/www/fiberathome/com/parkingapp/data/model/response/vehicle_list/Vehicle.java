package www.fiberathome.com.parkingapp.data.model.response.vehicle_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("vehicle_no")
    @Expose
    private String vehicleNo;
    @SerializedName("priority")
    @Expose
    private String priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
