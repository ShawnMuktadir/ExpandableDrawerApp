package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

public class ReservationCancelResponse extends BaseResponse {

    @SerializedName("uid")
    private String uid;

    @SerializedName("mobile_no")
    private String mobileNo;

    @SerializedName("tbl_id")
    private String tbl_id;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getTbl_id() {
        return tbl_id;
    }

    public void setTbl_id(String tbl_id) {
        this.tbl_id = tbl_id;
    }
}