package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ReservationResponse extends BaseResponse {
    @SerializedName("reservation")
    @Expose
    private String reservation;
    @SerializedName("bill")
    @Expose
    private String bill;


    @SerializedName("uid")
    @Expose
    private String uid;

    public String getReservation() {
        return reservation;
    }

    public void setReservation(String reservation) {
        this.reservation = reservation;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
