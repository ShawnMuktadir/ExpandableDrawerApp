package www.fiberathome.com.parkingapp.data.model.response.law;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LawResponse extends BaseResponse {

    @SerializedName("parkingLaw")
    @Expose
    private List<List<String>> parkingLaw = null;

    public List<List<String>> getParkingLaw() {
        return parkingLaw;
    }

    public void setParkingLaw(List<List<String>> parkingLaw) {
        this.parkingLaw = parkingLaw;
    }
}
