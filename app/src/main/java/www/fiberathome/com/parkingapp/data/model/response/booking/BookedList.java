package www.fiberathome.com.parkingapp.data.model.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BookedList {

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
    private String c_Status;
    @SerializedName("p_status")
    @Expose
    private String p_status;
    @SerializedName("area_id")
    @Expose
    private String areaId;
    @SerializedName("p_date")
    @Expose
    private String p_date;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("parking_area")
    @Expose
    private String parkingArea;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("ps_id")
    @Expose
    private String psId;

    @SerializedName("no_of_parking")
    @Expose
    private String noOfParking;

    public BookedList(String id, String userId, String timeStart, String timeEnd, String spotId, String currentBill,
                      String paymentStatus, String penalty, String bookingTime, String status, String cStatus, String pStatus,
                      String areaId, String pDate, String address, String parkingArea, String latitude, String longitude,
                      String psId) {
        this.id = id;
        this.userId = userId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.spotId = spotId;
        this.currentBill = currentBill;
        this.paymentStatus = paymentStatus;
        this.penalty = penalty;
        this.bookingTime = bookingTime;
        this.status = status;
        this.c_Status = cStatus;
        this.p_status = pStatus;
        this.areaId = areaId;
        this.p_date = pDate;
        this.address = address;
        this.parkingArea = parkingArea;
        this.latitude = latitude;
        this.longitude = longitude;
        this.psId = psId;
    }

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

    public String getC_Status() {
        return c_Status;
    }

    public void setC_Status(String c_Status) {
        this.c_Status = c_Status;
    }

    public String getP_status() {
        return p_status;
    }

    public void setP_status(String p_status) {
        this.p_status = p_status;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getP_date() {
        return p_date;
    }

    public void setP_date(String p_date) {
        this.p_date = p_date;
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

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }

    public String getNoOfParking() {
        return noOfParking;
    }

    public void setNoOfParking(String noOfParking) {
        this.noOfParking = noOfParking;
    }
}
