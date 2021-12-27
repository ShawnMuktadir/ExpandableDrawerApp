package www.fiberathome.com.parkingapp.data.model.response.termsCondition;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrivacyPolicyResponse implements Parcelable {

    @SerializedName("termsCondition")
    private List<String> termsCondition;

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    public void setTermsCondition(List<String> termsCondition) {
        this.termsCondition = termsCondition;
    }

    public List<String> getTermsCondition() {
        return termsCondition;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.termsCondition);
        dest.writeByte(this.error ? (byte) 1 : (byte) 0);
        dest.writeString(this.message);
    }

    public void readFromParcel(Parcel source) {
        this.termsCondition = source.createStringArrayList();
        this.error = source.readByte() != 0;
        this.message = source.readString();
    }

    public PrivacyPolicyResponse() {
    }

    protected PrivacyPolicyResponse(Parcel in) {
        this.termsCondition = in.createStringArrayList();
        this.error = in.readByte() != 0;
        this.message = in.readString();
    }

    public static final Parcelable.Creator<PrivacyPolicyResponse> CREATOR = new Parcelable.Creator<PrivacyPolicyResponse>() {
        @Override
        public PrivacyPolicyResponse createFromParcel(Parcel source) {
            return new PrivacyPolicyResponse(source);
        }

        @Override
        public PrivacyPolicyResponse[] newArray(int size) {
            return new PrivacyPolicyResponse[size];
        }
    };
}