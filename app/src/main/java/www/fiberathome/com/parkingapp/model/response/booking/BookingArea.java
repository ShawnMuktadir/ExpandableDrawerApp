package www.fiberathome.com.parkingapp.model.response.booking;

public class BookingArea {

    private String spotName;
    private String timeStart;
    private String timeEnd;

    public BookingArea() {
    }

    public BookingArea(String spotName, String timeStart, String timeEnd) {
        this.spotName = spotName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
}
