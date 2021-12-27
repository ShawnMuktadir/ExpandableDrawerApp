package www.fiberathome.com.parkingapp.data.model.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

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
    @SerializedName("ps_id")
    @Expose
    private String psId;

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

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }
}
