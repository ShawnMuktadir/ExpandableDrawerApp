package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeSlotsResponse {


    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;


    @SerializedName("sensors")
    @Expose
    private List<List<String>> sensors = null;

    public List<List<String>> getSensors() {
        return sensors;
    }

    public void setSensors(List<List<String>> sensors) {
        this.sensors = sensors;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}