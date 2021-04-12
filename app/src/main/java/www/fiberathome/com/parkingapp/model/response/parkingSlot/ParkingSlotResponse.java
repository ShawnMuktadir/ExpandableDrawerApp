package www.fiberathome.com.parkingapp.model.response.parkingSlot;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

@SuppressWarnings("unused")
public class ParkingSlotResponse extends BaseResponse {

    @SerializedName("sensors")
    @Expose
    private List<List<String>> sensors = null;

    public List<List<String>> getSensors() {
        return sensors;
    }

    public void setSensors(List<List<String>> sensors) {
        this.sensors = sensors;
    }

}
