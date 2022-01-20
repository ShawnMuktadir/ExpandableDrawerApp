package www.fiberathome.com.parkingapp.data.model.response.reservation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public class TimeSlotResponse extends BaseResponse {

    @SerializedName("sensors")
    @Expose
    private List<List<String>> timeSlots = null;

    public List<List<String>> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<List<String>> timeSlots) {
        this.timeSlots = timeSlots;
    }
}