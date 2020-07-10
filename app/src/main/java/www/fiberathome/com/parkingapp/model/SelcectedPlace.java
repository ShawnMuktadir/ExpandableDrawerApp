package www.fiberathome.com.parkingapp.model;

import java.io.Serializable;

public class SelcectedPlace implements Serializable {

    private String placeId;
    private String areaName;
    private String areaAddress;
    private double latitude;
    private double longitude;

    public SelcectedPlace(String areaName, double latitude, double longitude) {
        this.areaName = areaName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SelcectedPlace(String placeId, String areaName, String areaAddress, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAreaAddress() {
        return areaAddress;
    }

    public void setAreaAddress(String areaAddress) {
        this.areaAddress = areaAddress;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
