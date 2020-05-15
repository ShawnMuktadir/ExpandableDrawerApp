package www.fiberathome.com.parkingapp.model;

import java.io.Serializable;

public class SelcectedPlace implements Serializable {
    private String areaName;
    private double latitude;
    private double longitude;

    public SelcectedPlace(String areaName, double latitude, double longitude) {
        this.areaName = areaName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SelcectedPlace(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
