package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Reservation {

    @SerializedName("id")
    private int id;

    @SerializedName("mobile_no")
    private String mobileNo;

    @SerializedName("time_start")
    private String timeStart;

    @SerializedName("time_end")
    private String timeEnd;

    @SerializedName("spot_id")
    private String spotId;


    public Reservation(){

    }


    public Reservation( String mobileNo, String timeStart, String timeEnd, String spotId) {
        this.mobileNo = mobileNo;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.spotId = spotId;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() { return timeEnd; }

    public void setTimeEnd(String timeEnd) { this.timeEnd = timeEnd; }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }
}
