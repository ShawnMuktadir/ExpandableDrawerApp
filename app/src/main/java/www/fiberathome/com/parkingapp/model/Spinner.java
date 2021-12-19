package www.fiberathome.com.parkingapp.model;

public class Spinner {

    private int id;
    private double timeValue;
    private String value;
    /*private String vehicleNo;
    private String priority;*/

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

    /*public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }*/
}
