package www.fiberathome.com.parkingapp.model.response.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchVisitedPostResponse {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    @SerializedName("end_let")
    @Expose
    private String endLet;

    @SerializedName("end_long")
    @Expose
    private String endLong;

    @SerializedName("start_let")
    @Expose
    private String startLet;

    @SerializedName("start_long")
    @Expose
    private String startLong;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("date_visited")
    @Expose
    private String dateVisited;

    @SerializedName("token_id")
    @Expose
    private String tokenId;

    @SerializedName("address")
    @Expose
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEndLet() {
        return endLet;
    }

    public void setEndLet(String endLet) {
        this.endLet = endLet;
    }

    public String getEndLong() {
        return endLong;
    }

    public void setEndLong(String endLong) {
        this.endLong = endLong;
    }

    public String getStartLet() {
        return startLet;
    }

    public void setStartLet(String startLet) {
        this.startLet = startLet;
    }

    public String getStartLong() {
        return startLong;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getDateVisited() {
        return dateVisited;
    }

    public void setDateVisited(String dateVisited) {
        this.dateVisited = dateVisited;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
