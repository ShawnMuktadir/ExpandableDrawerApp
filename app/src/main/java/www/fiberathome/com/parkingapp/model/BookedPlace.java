package www.fiberathome.com.parkingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BookedPlace implements Parcelable {

  private double lat;
  private double lon;
  private String bookedUid;
  private  String route;

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
  }

  public void readFromParcel(Parcel source) {
    this.lat = source.readDouble();
    this.lon = source.readDouble();
    this.bookedUid = source.readString();
    this.route = source.readString();
  }

  public BookedPlace() {
  }

  protected BookedPlace(Parcel in) {
    this.lat = in.readDouble();
    this.lon = in.readDouble();
    this.bookedUid = in.readString();
    this.route = in.readString();
  }

  public static final Parcelable.Creator<BookedPlace> CREATOR = new Parcelable.Creator<BookedPlace>() {
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
