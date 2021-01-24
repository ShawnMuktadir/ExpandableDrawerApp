package www.fiberathome.com.parkingapp.model.response.common;

public class CommonResponse {

    private Boolean error;

    private String message;

    public CommonResponse(){

    }

    public CommonResponse(Boolean error, String message){
        super();
        this.error = error;
        this.message = message;
    }

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
}
