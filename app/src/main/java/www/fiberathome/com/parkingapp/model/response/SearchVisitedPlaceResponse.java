package www.fiberathome.com.parkingapp.model.response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchVisitedPlaceResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("visitor_data")
    @Expose
    private List<List<String>> visitorData = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<List<String>> getVisitorData() {
        return visitorData;
    }

    public void setVisitorData(List<List<String>> visitorData) {
        this.visitorData = visitorData;
    }

}
