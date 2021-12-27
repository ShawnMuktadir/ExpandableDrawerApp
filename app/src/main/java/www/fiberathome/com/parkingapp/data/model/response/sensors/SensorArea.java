package www.fiberathome.com.parkingapp.data.model.response.sensors;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SensorArea implements Parcelable {

    private String parkingArea;
    private double distance;
    private String count;
    private String duration;
    private boolean isChecked;
    private String occupiedCount;
    private String psId;

    private double fetchDistance = 0.0;
    private String placeId;
    private double endLat;
    private double endLng;

    public SensorArea(String parkingArea, String placeId, double endLat, double endLng, String count,
                      String psId, double fetchDistance) {
        this.parkingArea = parkingArea;
        this.placeId = placeId;
        this.endLat = endLat;
        this.endLng = endLng;
        this.count = count;
        this.psId = psId;
        this.distance = fetchDistance;
    }

    protected SensorArea(Parcel in) {
        parkingArea = in.readString();
        distance = in.readDouble();
        count = in.readString();
        duration = in.readString();
        isChecked = in.readByte() != 0;
        occupiedCount = in.readString();
        psId = in.readString();
        fetchDistance = in.readDouble();
        placeId = in.readString();
        endLat = in.readDouble();
        endLng = in.readDouble();
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

    public String getOccupiedCount() {
        return occupiedCount;
    }

    public void setOccupiedCount(String occupiedCount) {
        this.occupiedCount = occupiedCount;
    }

    public String getPsId() {
        return psId;
    }

    public void setPsId(String psId) {
        this.psId = psId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parkingArea);
        dest.writeDouble(distance);
        dest.writeString(count);
        dest.writeString(duration);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeString(occupiedCount);
        dest.writeString(psId);
        dest.writeDouble(fetchDistance);
        dest.writeString(placeId);
        dest.writeDouble(endLat);
        dest.writeDouble(endLng);
    }
}
