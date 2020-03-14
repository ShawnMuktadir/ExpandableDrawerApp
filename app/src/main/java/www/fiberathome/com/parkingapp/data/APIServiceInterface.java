package www.fiberathome.com.parkingapp.data;

import retrofit2.Call;
import retrofit2.http.GET;
import www.fiberathome.com.parkingapp.model.response.ParkingResponse;

public interface APIServiceInterface {

    /*----------------------------------------------------------Parking-------------------------------------------------------------*/

    @GET("sensors.php")
    Call<ParkingResponse> getParkingSensors();
}
