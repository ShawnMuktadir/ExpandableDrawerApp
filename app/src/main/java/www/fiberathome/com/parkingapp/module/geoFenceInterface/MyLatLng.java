package www.fiberathome.com.parkingapp.module.geoFenceInterface;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class MyLatLng  {
    private double latitude;
    private double longitude;

    public MyLatLng() {
    }

    public MyLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}
