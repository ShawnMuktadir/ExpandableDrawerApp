package www.fiberathome.com.parkingapp.model.termsCondition;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import www.fiberathome.com.parkingapp.model.response.common.CommonResponse;

public class TermsConditionResponse extends CommonResponse {

    @SerializedName("termsCondition")
    @Expose
    private List<List<String>> termsCondition = null;

    public List<List<String>> getTermsCondition() {
        return termsCondition;
    }

    public void setTermsCondition(List<List<String>> termsCondition) {
        this.termsCondition = termsCondition;
    }

}
