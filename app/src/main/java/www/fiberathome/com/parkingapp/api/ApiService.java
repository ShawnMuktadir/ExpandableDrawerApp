package www.fiberathome.com.parkingapp.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.model.common.Common;

public interface ApiService {

    @FormUrlEncoded
    @POST("change_password.php")
    Call<Common> updatePassword(
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("request_sms.php")
    Call<Common> createUser(
            @Field("fullname") String name,
            @Field("password") String password,
            @Field("mobile_no") String mobileNo,
            @Field("vehicle_no") String vehicleNo,
            @Field("image") String image
    );

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<Common> verifyOtp(@Field("otp") String otp);
}
