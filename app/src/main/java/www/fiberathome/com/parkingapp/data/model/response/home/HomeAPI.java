package www.fiberathome.com.parkingapp.data.model.response.home;

import retrofit2.Call;
import retrofit2.http.GET;
import www.fiberathome.com.parkingapp.data.model.response.booking.SensorAreaStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.data.model.response.sensors.SensorsResponse;

public interface HomeAPI {
    @GET("sensors.php")
    Call<SensorsResponse> getSensors();

    @GET("sensor_area.php")
    Call<ParkingSlotResponse> getParkingSlots();

    @GET("sensor_area_status.php")
    Call<SensorAreaStatusResponse> getSensorAreaStatus();
}
