package www.fiberathome.com.parkingapp.model.response.sensors;

import android.os.Parcel;
import android.os.Parcelable;

public class SensorArea {

    private String parkingArea;

    private double distance;
    private String count;
    private String duration;
    private boolean isChecked;

    private String placeId = null;
    private double endLat = 0.0;
    private double endLng = 0.0;
    private double fetchDistance = 0.0;

    public SensorArea(String parkingArea, String placeId, double endLat, double endLng, String count, double fetchDistance) {
        this.parkingArea = parkingArea;
        this.placeId = placeId;
        this.endLat = endLat;
        this.endLng = endLng;
        this.count = count;
        this.fetchDistance = fetchDistance;
    }

    public String getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(String parkingArea) {
        this.parkingArea = parkingArea;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int compareTo(SensorArea element) {
        int res = 0;
        if (this.distance < element.getDistance()) {
            res = -1;
        }
        if (this.distance > element.getDistance()) {
            res = 1;
        }
        return res;
    }
}
