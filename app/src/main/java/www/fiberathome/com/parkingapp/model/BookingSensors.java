package www.fiberathome.com.parkingapp.model;

import java.util.Comparator;

public class BookingSensors {

    private String parkingArea;
    private double lat;
    private double lng;
    private String distance;
    private String count;
    private String duration;
    private boolean isChecked;
    private boolean selected;

    public BookingSensors() {
    }

    public BookingSensors(String parkingArea, double lat, double lng, String distance, String count) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
//        this.duration = duration;
    }

    public BookingSensors(String parkingArea, double lat, double lng, String distance, String count, String duration) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
        this.duration = duration;
    }

    public static final Comparator<BookingSensors> BY_DISTANCE_ASCENDING_ORDER = new Comparator<BookingSensors>() {
        @Override
        public int compare(BookingSensors bookingSensors, BookingSensors t1) {
//            return Double.compare(t1.getDistance(), bookingSensors.getDistance());
            return bookingSensors.parkingArea.compareTo(t1.parkingArea);
        }
    };

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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}