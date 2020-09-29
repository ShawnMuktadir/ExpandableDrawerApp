package www.fiberathome.com.parkingapp.model.termsCondition;

import android.os.Parcel;
import android.os.Parcelable;

public class TermsCondition implements Parcelable {

    private String termsConditionId;
    private String termsConditionBody;
    private String termsConditionRemarks;
    private String termsConditionDate;
    private String termsConditionDomain;
    private String termsConditionSerialNo;
    private String termsConditionUser;

    public TermsCondition() {
    }

    public TermsCondition(String termsConditionId, String termsConditionBody,
                          String termsConditionRemarks, String termsConditionDate,
                          String termsConditionDomain, String termsConditionSerialNo, String termsConditionUser) {
        this.termsConditionId = termsConditionId;
        this.termsConditionBody = termsConditionBody;
        this.termsConditionRemarks = termsConditionRemarks;
        this.termsConditionDate = termsConditionDate;
        this.termsConditionDomain = termsConditionDomain;
        this.termsConditionSerialNo = termsConditionSerialNo;
        this.termsConditionUser = termsConditionUser;
    }

    protected TermsCondition(Parcel in) {
        termsConditionId = in.readString();
        termsConditionBody = in.readString();
        termsConditionRemarks = in.readString();
        termsConditionDate = in.readString();
        termsConditionDomain = in.readString();
        termsConditionSerialNo = in.readString();
        termsConditionUser = in.readString();
    }

    public static final Creator<TermsCondition> CREATOR = new Creator<TermsCondition>() {
        @Override
        public TermsCondition createFromParcel(Parcel in) {
            return new TermsCondition(in);
        }

        @Override
        public TermsCondition[] newArray(int size) {
            return new TermsCondition[size];
        }
    };

    public String getTermsConditionId() {
        return termsConditionId;
    }

    public void setTermsConditionId(String termsConditionId) {
        this.termsConditionId = termsConditionId;
    }

    public String getTermsConditionSerialNo() {
        return termsConditionSerialNo;
    }

    public void setTermsConditionSerialNo(String termsConditionSerialNo) {
        this.termsConditionSerialNo = termsConditionSerialNo;
    }

    public String getTermsConditionBody() {
        return termsConditionBody;
    }

    public void setTermsConditionBody(String termsConditionBody) {
        this.termsConditionBody = termsConditionBody;
    }

    public String getTermsConditionRemarks() {
        return termsConditionRemarks;
    }

    public void setTermsConditionRemarks(String termsConditionRemarks) {
        this.termsConditionRemarks = termsConditionRemarks;
    }

    public String getTermsConditionDate() {
        return termsConditionDate;
    }

    public void setTermsConditionDate(String termsConditionDate) {
        this.termsConditionDate = termsConditionDate;
    }

    public String getTermsConditionDomain() {
        return termsConditionDomain;
    }

    public void setTermsConditionDomain(String termsConditionDomain) {
        this.termsConditionDomain = termsConditionDomain;
    }

    public String getTermsConditionUser() {
        return termsConditionUser;
    }

    public void setTermsConditionUser(String termsConditionUser) {
        this.termsConditionUser = termsConditionUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(termsConditionId);
        dest.writeString(termsConditionBody);
        dest.writeString(termsConditionRemarks);
        dest.writeString(termsConditionDate);
        dest.writeString(termsConditionDomain);
        dest.writeString(termsConditionSerialNo);
        dest.writeString(termsConditionUser);
    }
}
