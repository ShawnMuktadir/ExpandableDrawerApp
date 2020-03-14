package www.fiberathome.com.parkingapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import www.fiberathome.com.parkingapp.model.Sensor;

public class ParkingResponse extends CommonResponse{

    @SerializedName("sensors")
    @Expose
    private ArrayList<Sensor> sensors = null;

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }
}
