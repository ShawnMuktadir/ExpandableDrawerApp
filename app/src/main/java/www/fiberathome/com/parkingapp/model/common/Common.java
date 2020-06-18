package www.fiberathome.com.parkingapp.model.common;

public class Common {

    private Boolean error;
    private String message;

    public Common(){

    }

    public Common(Boolean error, String message){
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
