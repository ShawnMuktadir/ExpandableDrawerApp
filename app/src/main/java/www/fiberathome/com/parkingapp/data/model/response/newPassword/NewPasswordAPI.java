package www.fiberathome.com.parkingapp.data.model.response.newPassword;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public interface NewPasswordAPI {
    @FormUrlEncoded
    @POST("change_password_otp.php")
    Call<BaseResponse> createNewPassword(
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );
}
