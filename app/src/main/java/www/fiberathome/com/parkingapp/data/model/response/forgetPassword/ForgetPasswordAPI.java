package www.fiberathome.com.parkingapp.data.model.response.forgetPassword;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public interface ForgetPasswordAPI {
    @FormUrlEncoded
    @POST("verify_user_otp.php")
    Call<BaseResponse> checkForgetPassword(@Field("mobile_no") String mobileNumber);
}
