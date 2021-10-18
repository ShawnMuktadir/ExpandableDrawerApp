package www.fiberathome.com.parkingapp.service.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class BookingSensorsRoom implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "uid")
    String uid;

    @ColumnInfo(name = "latitude")
    double latitude;

    @ColumnInfo(name = "longitude")
    double longitude;

    @ColumnInfo(name = "parking_area")
    String parkingArea;

    @ColumnInfo(name = "no_of_parking")
    String noOfParking;

    @ColumnInfo(name = "address")
    String address;

    @ColumnInfo(name = "status")
    String status;

    @ColumnInfo(name = "reserve_status")
    String reserveStatus;

    @Ignore
    public BookingSensorsRoom() {
    }

    public BookingSensorsRoom(int id, String uid, double latitude, double longitude, String parkingArea, String noOfParking, String address, String status, String reserveStatus) {
        this.id = id;
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingArea = parkingArea;
        this.noOfParking = noOfParking;
        this.address = address;
        this.status = status;
        this.reserveStatus = reserveStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(String parkingArea) {
        this.parkingArea = parkingArea;
    }

    public String getNoOfParking() {
        return noOfParking;
    }

    public void setNoOfParking(String noOfParking) {
        this.noOfParking = noOfParking;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(String reserveStatus) {
        this.reserveStatus = reserveStatus;
    }
}
