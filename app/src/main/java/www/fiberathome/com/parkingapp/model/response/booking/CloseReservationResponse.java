package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

public class CloseReservationResponse extends BaseResponse {

	@SerializedName("uid")
	private String uid;

	@SerializedName("mobile_no")
	private String mobileNo;

	public void setUid(String uid){
		this.uid = uid;
	}

	public String getUid(){
		return uid;
	}

	public void setMobileNo(String mobileNo){
		this.mobileNo = mobileNo;
	}

	public String getMobileNo(){
		return mobileNo;
	}
}