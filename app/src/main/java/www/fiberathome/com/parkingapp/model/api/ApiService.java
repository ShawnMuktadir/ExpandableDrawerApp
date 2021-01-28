package www.fiberathome.com.parkingapp.model.api;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import www.fiberathome.com.parkingapp.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.model.searchHistory.Parameters;
import www.fiberathome.com.parkingapp.model.searchHistory.SearchHistoryCommon;
import www.fiberathome.com.parkingapp.model.response.common.CommonResponse;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitedPlaceResponse;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitedPostResponse;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorsResponse;

public interface ApiService {

    @FormUrlEncoded
    @POST("change_password.php")
    Call<CommonResponse> updatePassword(
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("change_password_otp.php")
    Call<CommonResponse> forgetPassword(
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("request_sms.php")
    Call<CommonResponse> createUser(
            @Field("fullname") String name,
            @Field("password") String password,
            @Field("mobile_no") String mobileNo,
            @Field("vehicle_no") String vehicleNo,
            @Field("image") String image
    );

    @FormUrlEncoded
    @POST("visitor_place_history.php")
    Call<CommonResponse> storeSearchHistory(
            @Field("mobile_number") String mobileNo,
            @Field("place_id") String placeId,
            @Field("end_let") String endLatitude,
            @Field("end_long") String endLongitude,
            @Field("start_let") String startLatitude,
            @Field("start_long") String startLongitude,
            @Field("address") String areaAddress
    );

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<CommonResponse> verifyOtp(@Field("otp") String otp);

    @GET("visitor_place_tracker_get.php")
    Call<SearchVisitedPlaceResponse> getVisitorData();

    @FormUrlEncoded
    @POST("visitor_place_history.php")
    Call<SearchHistoryCommon> storeSearchHistory(@Field ("parameters") ArrayList<Parameters> parametersArrayList);

    @Multipart
    @POST("recent_visit_place")
    Call<SearchVisitedPostResponse> storeSearchHistory(@Part("token_id") RequestBody tokenId, @Part("mobile_no") RequestBody mobileNo);

    @POST("recent_visit_place")
    Call<ResponseBody> postSearchHistory(@Body RequestBody body);

    @FormUrlEncoded
    @POST("visitor_place_history.php")
    Call<ResponseBody> getSearchHistory(@Part("mobile_number") String mobileNo);

    @GET("sensors.php")
    Call<SensorsResponse> getSensors();

    @GET("sensor_area.php")
    Call<ParkingSlotResponse> getParkingSlots();
}
