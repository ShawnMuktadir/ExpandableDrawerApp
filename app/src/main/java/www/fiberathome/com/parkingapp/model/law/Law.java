package www.fiberathome.com.parkingapp.model.law;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Law implements Parcelable {
    @SerializedName("C")
    @Expose
    private String c;
    @SerializedName("B")
    @Expose
    private String b;

    protected Law(Parcel in) {
        c = in.readString();
        b = in.readString();
    }

    public static final Creator<Law> CREATOR = new Creator<Law>() {
        @Override
        public Law createFromParcel(Parcel in) {
            return new Law(in);
        }

        @Override
        public Law[] newArray(int size) {
            return new Law[size];
        }
    };

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(c);
        dest.writeString(b);
    }
}
