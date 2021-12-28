package www.fiberathome.com.parkingapp.data.model.response.reservation;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.data.model.response.vehicle_list.UserVehicleListResponse;

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

    @FormUrlEncoded
    @POST("booking_park_status.php")
    Call<BookingParkStatusResponse> getBookingParkStatus(@Field("mobile_no") String mobileNo);

    @FormUrlEncoded
    @POST("booking_park.php")
    Call<ReservationCancelResponse> setBookingPark(@Field("mobile_no") String mobileNo,
                                                   @Field("spot_id") String bookedUid);

    @FormUrlEncoded
    @POST("booking_closed_new.php")
    Call<CloseReservationResponse> endReservation(@Field("mobile_no") String mobileNo,
                                                  @Field("spot_id") String bookedUid,
                                                  @Field("tbl_id") String tbl_id);

    @GET("time_slot.php")
    Call<TimeSlotResponse> getTimeSlot();

    @FormUrlEncoded
    @POST("user_vehicle.php")
    Call<UserVehicleListResponse> getUserVehicleList(@Field("user_id") String mobileNo);
}
