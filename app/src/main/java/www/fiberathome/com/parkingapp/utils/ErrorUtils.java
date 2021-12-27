package www.fiberathome.com.parkingapp.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;

public class ErrorUtils {
    public static ErrorResponse parseError(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> converter =
                APIClient.getRetrofit()
                        .responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse errorResponse;

        try {
            errorResponse = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ErrorResponse();
        }

        return errorResponse;
    }
}
