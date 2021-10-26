package www.fiberathome.com.parkingapp.model.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookedResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.model.response.booking.CloseReservationResponse;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationResponse;
import www.fiberathome.com.parkingapp.model.response.booking.SensorAreaStatusResponse;
import www.fiberathome.com.parkingapp.model.response.booking.TimeSlotResponse;
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitedPlaceResponse;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorsResponse;
import www.fiberathome.com.parkingapp.model.response.termsCondition.TermsConditionResponse;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface ApiService {

    @FormUrlEncoded
    @POST("change_password.php")
    Call<BaseResponse> updatePassword(
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

    @FormUrlEncoded
    @POST("change_password_otp.php")
    Call<BaseResponse> setPasswordForForgetPassword(
            @Field("new_password") String new_password,
            @Field("confirm_password") String confirm_password,
            @Field("mobile_no") String mobile_no
    );

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
    @POST("edit_U_info.php")
    Call<LoginResponse> editProfile(
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
    @POST("verify_user.php")
    Call<LoginResponse> loginUser(
            @Field("mobile_no") String mobileNo,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<LoginResponse> verifyOtp(@Field("otp") String otp);

    @FormUrlEncoded
    @POST("verify_user_otp.php")
    Call<BaseResponse> checkForgetPassword(@Field("mobile_no") String mobileNumber);

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
    @POST("reservation_fnc.php")
    Call<ReservationResponse> storeReservation(
            @Field("mobile_no") String mobileNo,
            @Field("time_start") String startTime,
            @Field("time_end") String endTime,
            @Field("spot_id") String spotId,
            @Field("stage") String stage);

    @FormUrlEncoded
    @POST("visitor_place_tracker_get.php")
    Call<SearchVisitedPlaceResponse> getSearchHistory(@Field("mobile_number") String mobileNo);

    @GET("sensors.php")
    Call<SensorsResponse> getSensors();

    @GET("sensor_area.php")
    Call<ParkingSlotResponse> getParkingSlots();

    @GET("terms_condition.php")
    Call<TermsConditionResponse> getTermCondition();

    @GET("sensor_area_status.php")
    Call<SensorAreaStatusResponse> getSensorAreaStatus();

    @FormUrlEncoded
    @POST("bookings.php")
    Call<BookedResponse> getBookedPlace(@Field("user_id") String mobileNo);

    @FormUrlEncoded
    @POST("booking_closed_new.php")
    Call<CloseReservationResponse> endReservation(@Field("mobile_no") String mobileNo,
                                                  @Field("spot_id") String bookedUid,
                                                  @Field("tbl_id") String tbl_id);

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

    @GET("time_slot.php")
    Call<TimeSlotResponse> getTimeSlot();
}
