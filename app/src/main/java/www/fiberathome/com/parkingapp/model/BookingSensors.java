package www.fiberathome.com.parkingapp.model;

import java.util.Comparator;

public class BookingSensors {

    private String parkingArea;
    private double lat;
    private double lng;
    private double distance;
    private String count;
    private String duration;
    private boolean isChecked;
    private boolean selected;
    public int type;
    public String text;
    public int data;

    public static final int TEXT_INFO_TYPE = 0;
    public static final int INFO_TYPE = 1;

    public BookingSensors() {
    }

    public BookingSensors(String parkingArea, double lat, double lng, double distance, String count, String duration, String text, int type, int data) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
        this.duration = duration;
        this.text = text;
        this.type = type;
        this.data = data;
    }

    public BookingSensors(String parkingArea, double lat, double lng, double distance, String count, String duration, int type, int data) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
        this.duration = duration;
        this.type = type;
        this.data = data;
    }

    public BookingSensors(String parkingArea, double lat, double lng, double distance, String count) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
//        this.duration = duration;
    }

    public BookingSensors(String parkingArea, double lat, double lng, double distance, String count, String duration) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
        this.duration = duration;
    }

    public int compareTo(BookingSensors element) {
        int res = 0;
        if (this.distance < element.getDistance()) {
            res =- 1;
        }
        if (this.distance > element.getDistance()) {
            res = 1;
        }
        return res;
    }

    public static final Comparator<BookingSensors> BY_NAME_ASCENDING_ORDER = (bookingSensors, t1) -> {
//            return Double.compare(t1.getDistance(), bookingSensors.getDistance());
        return bookingSensors.parkingArea.compareTo(t1.parkingArea);
    };

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
