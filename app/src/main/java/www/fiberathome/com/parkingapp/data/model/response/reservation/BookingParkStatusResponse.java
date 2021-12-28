package www.fiberathome.com.parkingapp.data.model.response.reservation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BookingParkStatusResponse extends BaseResponse {

    @SerializedName("sensors")
    @Expose
    private Sensors sensors;

    public Sensors getSensors() {
        return sensors;
    }

    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
    }

    public static class Sensors {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("time_start")
        @Expose
        private String timeStart;
        @SerializedName("time_end")
        @Expose
        private String timeEnd;
        @SerializedName("spot_id")
        @Expose
        private String spotId;
        @SerializedName("current_bill")
        @Expose
        private String currentBill;
        @SerializedName("payment_status")
        @Expose
        private String paymentStatus;
        @SerializedName("penalty")
        @Expose
        private String penalty;
        @SerializedName("booking_time")
        @Expose
        private String bookingTime;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("c_status")
        @Expose
        private String cStatus;
        @SerializedName("p_status")
        @Expose
        private String pStatus;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("area_id")
        @Expose
        private String area_id;
        @SerializedName("p_date")
        @Expose
        private String p_date;
        @SerializedName("parking_area")
        @Expose
        private String parkingArea;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTimeStart() {
            return timeStart;
        }

        public void setTimeStart(String timeStart) {
            this.timeStart = timeStart;
        }

        public String getTimeEnd() {
            return timeEnd;
        }

        public void setTimeEnd(String timeEnd) {
            this.timeEnd = timeEnd;
        }

        public String getSpotId() {
            return spotId;
        }

        public void setSpotId(String spotId) {
            this.spotId = spotId;
        }

        public String getCurrentBill() {
            return currentBill;
        }

        public void setCurrentBill(String currentBill) {
            this.currentBill = currentBill;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getPenalty() {
            return penalty;
        }

        public void setPenalty(String penalty) {
            this.penalty = penalty;
        }

        public String getBookingTime() {
            return bookingTime;
        }

        public void setBookingTime(String bookingTime) {
            this.bookingTime = bookingTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getcStatus() {
            return cStatus;
        }

        public void setcStatus(String cStatus) {
            this.cStatus = cStatus;
        }

        public String getpStatus() {
            return pStatus;
        }

        public void setpStatus(String pStatus) {
            this.pStatus = pStatus;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getParkingArea() {
            return parkingArea;
        }

        public void setParkingArea(String parkingArea) {
            this.parkingArea = parkingArea;
        }

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getP_date() {
            return p_date;
        }

        public void setP_date(String p_date) {
            this.p_date = p_date;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}
