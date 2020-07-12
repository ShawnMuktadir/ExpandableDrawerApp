package www.fiberathome.com.parkingapp.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.model.common.RetrofitCommon;
import www.fiberathome.com.parkingapp.model.response.SearchVisitedPlaceResponse;

public interface ApiService {

    @FormUrlEncoded
    @POST("change_password.php")
    Call<RetrofitCommon> updatePassword(
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("change_password_otp.php")
    Call<RetrofitCommon> forgetPassword(
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("request_sms.php")
    Call<RetrofitCommon> createUser(
            @Field("fullname") String name,
            @Field("password") String password,
            @Field("mobile_no") String mobileNo,
            @Field("vehicle_no") String vehicleNo,
            @Field("image") String image
    );

    @FormUrlEncoded
    @POST("visitor_place_history.php")
    Call<RetrofitCommon> storeSearchHistory(
            @Field("mobile_number") String mobileNo,
            @Field("place_id") String placeId,
            @Field("end_let") String endLatitude,
            @Field("end_long") String endLongitude,
            @Field("start_let") String startLatitude,
            @Field("start_long") String startLongitude,
//            @Field("date_visited") String dateVisited,
            @Field("address") String areaAddress
    );

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<RetrofitCommon> verifyOtp(@Field("otp") String otp);

    @GET("visitor_place_tracker_get.php")
    Call<SearchVisitedPlaceResponse> getVisitorData();
}
