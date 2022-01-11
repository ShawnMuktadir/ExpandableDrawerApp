package www.fiberathome.com.parkingapp.data.model.response.vehicle_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public class UserVehicleListResponse extends BaseResponse {

    @SerializedName("vehicle")
    @Expose
    private List<Vehicle> vehicle = null;

    public List<Vehicle> getVehicle() {
        return vehicle;
    }

    public void setVehicle(List<Vehicle> vehicle) {
        this.vehicle = vehicle;
    }
}
