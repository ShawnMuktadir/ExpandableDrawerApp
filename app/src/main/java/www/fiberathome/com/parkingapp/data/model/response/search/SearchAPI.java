package www.fiberathome.com.parkingapp.data.model.response.search;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public interface SearchAPI {
    @FormUrlEncoded
    @POST("visitor_place_history.php")
    Call<BaseResponse> storeSearchHistory(
            @Field("mobile_number") String mobileNo,
            @Field("place_id") String placeId,
            @Field("end_let") String endLatitude,
            @Field("end_long") String endLongitude,
            @Field("start_let") String startLatitude,
            @Field("start_long") String startLongitude,
            @Field("address") String areaAddress
    );

    @FormUrlEncoded
    @POST("visitor_place_tracker_get.php")
    Call<SearchVisitedPlaceResponse> getSearchHistory(@Field("mobile_number") String mobileNo);
}
