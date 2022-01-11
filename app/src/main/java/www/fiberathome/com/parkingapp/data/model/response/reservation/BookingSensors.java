package www.fiberathome.com.parkingapp.data.model.response.reservation;

@SuppressWarnings({"unused", "RedundantSuppression"})
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
    public String uid;
    public String occupiedCount;
    public String totalCount;
    private String parkingPlaceId;
    private String psId;

    public static final int SELECTED_INFO_TYPE = 0;
    public static final int INFO_TYPE = 1;

    public BookingSensors() {
    }

    public BookingSensors(String parkingArea, double lat, double lng, double distance, String count,
                          String duration, String text, int type, int data, String parkingPlaceId,
                          String occupiedCount, String psId) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
        this.duration = duration;
        this.text = text;
        this.type = type;
        this.data = data;
        this.parkingPlaceId = parkingPlaceId;
        this.occupiedCount = occupiedCount;
        this.psId = psId;
    }

    public BookingSensors(String parkingArea, double lat, double lng, double distance, String count,
                          String duration, int type, int data, String parkingPlaceId, String occupiedCount, String psId) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
        this.duration = duration;
        this.type = type;
        this.data = data;
        this.parkingPlaceId = parkingPlaceId;
        this.occupiedCount = occupiedCount;
        this.psId = psId;
    }

    public int compareTo(BookingSensors element) {
        int res = 0;
        if (this.distance < element.getDistance()) {
            res = -1;
        }
        if (this.distance > element.getDistance()) {
            res = 1;
        }
        return res;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOccupiedCount() {
        return occupiedCount;
    }

    public void setOccupiedCount(String occupiedCount) {
        this.occupiedCount = occupiedCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getParkingPlaceId() {
        return parkingPlaceId;
    }

    public void setParkingPlaceId(String parkingPlaceId) {
        this.parkingPlaceId = parkingPlaceId;
    }

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
    }
}
