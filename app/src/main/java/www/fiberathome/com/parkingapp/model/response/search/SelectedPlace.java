package www.fiberathome.com.parkingapp.model.response.search;

import java.io.Serializable;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SelectedPlace implements Serializable {

    private String placeId;
    private String areaName;
    private String areaAddress;
    private final double latitude;
    private final double longitude;

    public SelectedPlace(String areaName, double latitude, double longitude) {
        this.areaName = areaName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SelectedPlace(String placeId, String areaName, String areaAddress, double latitude, double longitude) {
        this.placeId = placeId;
        this.areaName = areaName;
        this.areaAddress = areaAddress;
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
