package www.fiberathome.com.parkingapp.data.model.response.auth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;

public interface AuthAPI {
    @FormUrlEncoded
    @POST("verify_user.php")
    Call<LoginResponse> login(@Field("mobile_no") String mobileNo,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("request_sms.php")
    Call<BaseResponse> createUser(
            @Field("fullname") String name,
            @Field("password") String password,
            @Field("mobile_no") String mobileNo,
            @Field("vehicle_no") String vehicleNo,
            @Field("image") String image,
            @Field("image_name") String imageName,
            @Field("vehicle_image") String vehicleImage,
            @Field("vehicle_image_name") String vehicleImageName
    );

    @FormUrlEncoded
    @POST("change_password_otp.php")
    Call<BaseResponse> createNewPassword(
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("change_password.php")
    Call<BaseResponse> changePassword(
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("verify_user_otp.php")
    Call<BaseResponse> checkForgetPassword(@Field("mobile_no") String mobileNumber);

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<LoginResponse> verifyOtp(@Field("otp") String otp);
}
