package www.fiberathome.com.parkingapp.data.model.response.booking;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ReservationAPI {
    @FormUrlEncoded
    @POST("reservation_fnc.php")
    Call<ReservationResponse> storeReservation(
            @Field("mobile_no") String mobileNo,
            @Field("time_start") String startTime,
            @Field("time_end") String endTime,
            @Field("spot_id") String spotId,
            @Field("stage") String stage,
            @Field("vehicle_no") String vehicleNo);

    @FormUrlEncoded
    @POST("bookings.php")
    Call<BookedResponse> getBookedPlace(@Field("user_id") String mobileNo);

    @FormUrlEncoded
    @POST("booking_cancel_new.php")
    Call<ReservationCancelResponse> cancelReservation(@Field("mobile_no") String mobileNo,
                                                      @Field("spot_id") String bookedUid,
                                                      @Field("tbl_id") String tbl_id);
}
