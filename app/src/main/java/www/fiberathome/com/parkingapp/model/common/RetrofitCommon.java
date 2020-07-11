package www.fiberathome.com.parkingapp.model.common;

public class RetrofitCommon {

    private Boolean error;
    private String message;

    public RetrofitCommon(){

    }

    public RetrofitCommon(Boolean error, String message){
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
