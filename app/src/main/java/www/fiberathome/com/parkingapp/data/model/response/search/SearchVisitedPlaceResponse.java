package www.fiberathome.com.parkingapp.data.model.response.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

@SuppressWarnings({"unused", "RedundantSuppression"})
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
