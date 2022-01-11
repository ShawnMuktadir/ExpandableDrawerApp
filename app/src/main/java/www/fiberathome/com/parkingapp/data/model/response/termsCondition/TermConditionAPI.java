package www.fiberathome.com.parkingapp.data.model.response.termsCondition;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TermConditionAPI {
    @GET("terms_condition.php")
    Call<TermsConditionResponse> getTermCondition();
}
