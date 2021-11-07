package www.fiberathome.com.parkingapp.model;

public class Spinner {

    private int id;
    private double timeValue;
    private String value;

    public Spinner() {

    }

    public Spinner(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public Spinner(double timeValue, String value) {
        this.timeValue = timeValue;
        this.value = value;
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

    public double getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(long timeValue) {
        this.timeValue = timeValue;
    }
}
