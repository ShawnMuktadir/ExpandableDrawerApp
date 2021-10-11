package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SensorAreaStatusResponse extends BaseResponse {

    @SerializedName("sensors")
    @Expose
    private List<List<String>> sensorAreaStatusArrayList = null;

    public List<List<String>> getSensorAreaStatusArrayList() {
        return sensorAreaStatusArrayList;
    }

    public void setSensorAreaStatusArrayList(List<List<String>> sensorAreaStatusArrayList) {
        this.sensorAreaStatusArrayList = sensorAreaStatusArrayList;
    }
}
