package www.fiberathome.com.parkingapp.model;

public class Spinner {

    private int id;
    private String value;

    public Spinner(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public Spinner() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
