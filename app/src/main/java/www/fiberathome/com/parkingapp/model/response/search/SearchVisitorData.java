package www.fiberathome.com.parkingapp.model.response.search;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SearchVisitorData implements Parcelable {

    private String visitedArea;
    private String placeId;
    private double endLat;
    private double endLng;
    private double startLat;
    private double startLng;

    public SearchVisitorData() {
    }

    public SearchVisitorData(String visitedArea, String placeId, double endLat, double endLng, double startLat, double startLng) {
        this.visitedArea = visitedArea;
        this.placeId = placeId;
        this.endLat = endLat;
        this.endLng = endLng;
        this.startLat = startLat;
        this.startLng = startLng;
    }

    public SearchVisitorData(String visitedArea, String placeId, double endLat, double endLng) {
        this.visitedArea = visitedArea;
        this.placeId = placeId;
        this.endLat = endLat;
        this.endLng = endLng;
    }

    public String getVisitedArea() {
        return visitedArea;
    }

    public void setVisitedArea(String visitedArea) {
        this.visitedArea = visitedArea;
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

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof SearchVisitorData) {
            SearchVisitorData temp = (SearchVisitorData) obj;
            return this.visitedArea.equals(temp.visitedArea) && this.placeId.equals(temp.placeId);
        }
        return false;

    }

    @Override
    public int hashCode() {
        return (this.visitedArea.hashCode() + this.placeId.hashCode());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.visitedArea);
        dest.writeString(this.placeId);
        dest.writeDouble(this.endLat);
        dest.writeDouble(this.endLng);
        dest.writeDouble(this.startLat);
        dest.writeDouble(this.startLng);
    }

    public void readFromParcel(Parcel source) {
        this.visitedArea = source.readString();
        this.placeId = source.readString();
        this.endLat = source.readDouble();
        this.endLng = source.readDouble();
        this.startLat = source.readDouble();
        this.startLng = source.readDouble();
    }

    protected SearchVisitorData(Parcel in) {
        this.visitedArea = in.readString();
        this.placeId = in.readString();
        this.endLat = in.readDouble();
        this.endLng = in.readDouble();
        this.startLat = in.readDouble();
        this.startLng = in.readDouble();
    }

    public static final Parcelable.Creator<SearchVisitorData> CREATOR = new Parcelable.Creator<SearchVisitorData>() {
        @Override
        public SearchVisitorData createFromParcel(Parcel source) {
            return new SearchVisitorData(source);
        }

        @Override
        public SearchVisitorData[] newArray(int size) {
            return new SearchVisitorData[size];
        }
    };
}