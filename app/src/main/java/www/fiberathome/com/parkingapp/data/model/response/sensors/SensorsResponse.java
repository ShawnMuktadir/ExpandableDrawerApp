package www.fiberathome.com.parkingapp.data.model.response.sensors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SensorsResponse extends BaseResponse {

    @SerializedName("sensors")
    @Expose
    private List<Sensor> sensors = null;

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}