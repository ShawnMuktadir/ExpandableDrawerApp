package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.SerializedName;

public class CloseReservationResponse{

	@SerializedName("uid")
	private String uid;

	@SerializedName("mobile_no")
	private String mobileNo;

	@SerializedName("error")
	private boolean error;

	@SerializedName("message")
	private String message;

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

	public void setError(boolean error){
		this.error = error;
	}

	public boolean isError(){
		return error;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return 
			"CloseReservationResponse{" + 
			"uid = '" + uid + '\'' + 
			",mobile_no = '" + mobileNo + '\'' + 
			",error = '" + error + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}