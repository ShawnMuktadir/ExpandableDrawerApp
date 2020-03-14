package www.fiberathome.com.parkingapp.model;

public class SensorArea   {

    private String parkingArea;
    private double lat;
    private double lng;
    private String count;
    private boolean isChecked;

    public SensorArea() {
    }

    public SensorArea(String parkingArea, String count, boolean isChecked) {
        this.parkingArea = parkingArea;
        this.count = count;
        this.isChecked = isChecked;
    }

    public String getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(String parkingArea) {
        this.parkingArea = parkingArea;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
