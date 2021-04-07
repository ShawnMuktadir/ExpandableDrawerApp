package www.fiberathome.com.parkingapp.model.response.searchHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Parameters {

    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("end_let")
    @Expose
    private String endLet;

    @SerializedName("end_long")
    @Expose
    private String endLong;

    @SerializedName("start_let")
    @Expose
    private String startLet;

    @SerializedName("start_long")
    @Expose
    private String startLong;

    @SerializedName("address")
    @Expose
    private String address;

    public Parameters(String mobileNumber, String placeId, String endLet, String endLong, String startLet, String startLong, String address) {
        this.mobileNumber = mobileNumber;
        this.placeId = placeId;
        this.endLet = endLet;
        this.endLong = endLong;
        this.startLet = startLet;
        this.startLong = startLong;
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getEndLet() {
        return endLet;
    }

    public void setEndLet(String endLet) {
        this.endLet = endLet;
    }

    public String getEndLong() {
        return endLong;
    }

    public void setEndLong(String endLong) {
        this.endLong = endLong;
    }

    public String getStartLet() {
        return startLet;
    }

    public void setStartLet(String startLet) {
        this.startLet = startLet;
    }

    public String getStartLong() {
        return startLong;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
