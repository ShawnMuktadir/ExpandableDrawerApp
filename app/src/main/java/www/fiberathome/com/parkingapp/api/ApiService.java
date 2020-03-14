package www.fiberathome.com.parkingapp.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.utils.MSG;

public interface ApiService {

    @FormUrlEncoded
    @POST("change_password.php")
    Call<MSG> updatePassword(
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
        );


}
