package www.fiberathome.com.parkingapp.data.model.response.registration;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public interface RegistrationAPI {
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
}
