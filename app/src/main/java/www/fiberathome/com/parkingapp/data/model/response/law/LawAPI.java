package www.fiberathome.com.parkingapp.data.model.response.law;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LawAPI {

    @GET("parking_law.php")
    Call<LawResponse> getParkingLaws();
}
