package www.fiberathome.com.parkingapp.data.model.response.verifyPhone;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;

public interface VerifyPhoneAPI {
    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<LoginResponse> verifyOtp(@Field("otp") String otp);
}
