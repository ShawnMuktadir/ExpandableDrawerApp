package www.fiberathome.com.parkingapp.model.response.search;

public class SearchVisitedPostRequest {

    private String tokenId;

    private String mobileNo;

    public SearchVisitedPostRequest(String tokenId, String mobileNo) {
        this.tokenId = tokenId;
        this.mobileNo = mobileNo;
    }
}
