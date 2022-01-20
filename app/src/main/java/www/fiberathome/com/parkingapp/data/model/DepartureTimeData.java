package www.fiberathome.com.parkingapp.data.model;

public class DepartureTimeData {

    private String title;
    private double timeValue;

    public DepartureTimeData(String title, double timeValue) {
        this.title = title;
        this.timeValue = timeValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(double timeValue) {
        this.timeValue = timeValue;
    }
}

