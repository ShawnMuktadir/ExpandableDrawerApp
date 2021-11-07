package www.fiberathome.com.parkingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BookedPlace implements Parcelable {

    private double lat;
    private double lon;
    private String bookedUid;
    private String route;
    private String areaName;
    private String parkingSlotCount;
    private boolean isBooked = false;
    private boolean isPaid = false;
    private String placeId;
    private String reservation;
    private long departedDate;
    private long arriveDate;
    private float bill;


    public BookedPlace() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getBookedUid() {
        return bookedUid;
    }

    public void setBookedUid(String bookedUid) {
        this.bookedUid = bookedUid;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getParkingSlotCount() {
        return parkingSlotCount;
    }

    public void setParkingSlotCount(String parkingSlotCount) {
        this.parkingSlotCount = parkingSlotCount;
    }

    public boolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setDepartedDate(long departedDate) {
        this.departedDate = departedDate;
    }

    public long getDepartedDate() {
        return departedDate;
    }

    public void setArriveDate(long arriveDate) {
        this.arriveDate = arriveDate;
    }

    public long getArriveDate() {
        return arriveDate;
    }

    public String getReservation() {
        return reservation;
    }

    public void setReservation(String reservation) {
        this.reservation = reservation;
    }

    public float getBill() {
        return bill;
    }

    public void setBill(float bill) {
        this.bill = bill;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.bookedUid);
        dest.writeString(this.route);
        dest.writeString(this.areaName);
        dest.writeString(this.parkingSlotCount);
        dest.writeString(this.reservation);
        dest.writeByte(this.isBooked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPaid ? (byte) 1 : (byte) 0);
        dest.writeString(this.placeId);
        dest.writeDouble(this.departedDate);
        dest.writeDouble(this.arriveDate);
    }

    public void readFromParcel(Parcel source) {
        this.lat = source.readDouble();
        this.lon = source.readDouble();
        this.bookedUid = source.readString();
        this.route = source.readString();
        this.areaName = source.readString();
        this.parkingSlotCount = source.readString();
        this.isBooked = source.readByte() != 0;
        this.isPaid = source.readByte() != 0;
        this.placeId = source.readString();
        this.reservation = source.readString();
        this.departedDate = source.readLong();
        this.arriveDate = source.readLong();
    }

    protected BookedPlace(Parcel in) {
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.bookedUid = in.readString();
        this.route = in.readString();
        this.areaName = in.readString();
        this.parkingSlotCount = in.readString();
        this.isBooked = in.readByte() != 0;
        this.isPaid = in.readByte() != 0;
        this.placeId = in.readString();
        this.reservation = in.readString();
        this.departedDate = in.readLong();
        this.arriveDate = in.readLong();
    }

    public static final Creator<BookedPlace> CREATOR = new Creator<BookedPlace>() {
        @Override
        public BookedPlace createFromParcel(Parcel source) {
            return new BookedPlace(source);
        }

        @Override
        public BookedPlace[] newArray(int size) {
            return new BookedPlace[size];
        }
    };
}
