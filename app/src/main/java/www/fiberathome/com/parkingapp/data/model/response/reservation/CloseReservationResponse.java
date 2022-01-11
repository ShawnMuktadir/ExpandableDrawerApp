package www.fiberathome.com.parkingapp.data.model.response.reservation;

import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public class CloseReservationResponse extends BaseResponse {

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