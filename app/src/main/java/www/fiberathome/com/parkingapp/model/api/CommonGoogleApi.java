package www.fiberathome.com.parkingapp.model.api;

public class CommonGoogleApi {
    public static final String baseUrl = "https://googleapis.com";

    public static IGoogleApi getGoogleApi() {
        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class);
    }
}
