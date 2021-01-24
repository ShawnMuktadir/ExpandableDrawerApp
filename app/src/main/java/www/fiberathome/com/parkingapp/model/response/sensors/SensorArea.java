package www.fiberathome.com.parkingapp.model.response.sensors;

import android.os.Parcel;
import android.os.Parcelable;

public class SensorArea implements Parcelable {

    private String parkingArea;
    private double lat;
    private double lng;
    private double distance;
    private String count;
    private String duration;
    private boolean isChecked;

    public SensorArea() {
    }

    public SensorArea(String parkingArea, double lat, double lng, double distance, String count) {
        this.parkingArea = parkingArea;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.count = count;
    }

    protected SensorArea(Parcel in) {
        parkingArea = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        distance = in.readDouble();
        count = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<SensorArea> CREATOR = new Creator<SensorArea>() {
        @Override
        public SensorArea createFromParcel(Parcel in) {
            return new SensorArea(in);
        }

        @Override
        public SensorArea[] newArray(int size) {
            return new SensorArea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parkingArea);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeDouble(distance);
        dest.writeString(count);
        dest.writeByte((byte) (isChecked ? 1 : 0));
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
}
