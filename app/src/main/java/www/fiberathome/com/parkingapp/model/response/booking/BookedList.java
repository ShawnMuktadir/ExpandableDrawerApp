package www.fiberathome.com.parkingapp.model.response.booking;

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

    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("parking_area")
    @Expose
    private String parkingArea;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("c_status")
    @Expose
    private String c_status;



    @SerializedName("p_status")
    @Expose
    private String p_status;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("sensor_no")
    @Expose
    private String sensorNo;

    @SerializedName("area_no")
    @Expose
    private String areaNo;

    @SerializedName("area_id")
    @Expose
    private String areaId;

    @SerializedName("no_of_parking")
    @Expose
    private String noOfParking;

    public BookedList(String address, String timeStart, String timeEnd, String currentBill, String penalty) {
        this.address = address;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.currentBill = currentBill;
        this.penalty = penalty;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(String parkingArea) {
        this.parkingArea = parkingArea;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSensorNo() {
        return sensorNo;
    }

    public void setSensorNo(String sensorNo) {
        this.sensorNo = sensorNo;
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getNoOfParking() {
        return noOfParking;
    }

    public void setNoOfParking(String noOfParking) {
        this.noOfParking = noOfParking;
    }
    public String getC_status() {
        return c_status;
    }

    public void setC_status(String c_status) {
        this.c_status = c_status;
    }
    public String getP_status() {
        return p_status;
    }

    public void setP_status(String p_status) {
        this.p_status = p_status;
    }
}
