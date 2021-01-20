package www.fiberathome.com.parkingapp.model.sensors;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SensorsResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sensors")
    @Expose
    private List<Sensor> sensors = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public class Sensor {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("uid")
        @Expose
        private String uid;
        @SerializedName("s_status")
        @Expose
        private String sStatus;
        @SerializedName("last_updated")
        @Expose
        private String lastUpdated;
        @SerializedName("event_time")
        @Expose
        private String eventTime;
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
        @SerializedName("reserve_status")
        @Expose
        private Integer reserveStatus;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSStatus() {
            return sStatus;
        }

        public void setSStatus(String sStatus) {
            this.sStatus = sStatus;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public String getEventTime() {
            return eventTime;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
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

        public Integer getReserveStatus() {
            return reserveStatus;
        }

        public void setReserveStatus(Integer reserveStatus) {
            this.reserveStatus = reserveStatus;
        }

    }
}