package www.fiberathome.com.parkingapp.data.model.response.login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginAPI {
    @FormUrlEncoded
    @POST("verify_user.php")
    //Call<LoginResponse> login(@Body LoginRequest request);
    Call<LoginResponse> login(@Field("mobile_no") String mobileNo,
                              @Field("password") String password);
}
