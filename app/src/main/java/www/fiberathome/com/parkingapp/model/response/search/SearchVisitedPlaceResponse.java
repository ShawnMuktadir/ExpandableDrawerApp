package www.fiberathome.com.parkingapp.model.response.search;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

public class SearchVisitedPlaceResponse extends BaseResponse {

    @SerializedName("visitor_data")
    @Expose
    private List<List<String>> visitorData = null;

    public List<List<String>> getVisitorData() {
        return visitorData;
    }

    public void setVisitorData(List<List<String>> visitorData) {
        this.visitorData = visitorData;
    }

}
