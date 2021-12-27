package www.fiberathome.com.parkingapp.data.model.response.search;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SelectedPlace implements Parcelable {

    private String placeId;
    private String areaName;
    private String areaAddress;
    private double latitude;
    private double longitude;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.placeId);
        dest.writeString(this.areaName);
        dest.writeString(this.areaAddress);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public void readFromParcel(Parcel source) {
        this.placeId = source.readString();
        this.areaName = source.readString();
        this.areaAddress = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
    }

    protected SelectedPlace(Parcel in) {
        this.placeId = in.readString();
        this.areaName = in.readString();
        this.areaAddress = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<SelectedPlace> CREATOR = new Parcelable.Creator<SelectedPlace>() {
        @Override
        public SelectedPlace createFromParcel(Parcel source) {
            return new SelectedPlace(source);
        }

        @Override
        public SelectedPlace[] newArray(int size) {
            return new SelectedPlace[size];
        }
    };
}
