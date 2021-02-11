package www.fiberathome.com.parkingapp.model.response.booking;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

public class BookedResponse extends BaseResponse {

    @SerializedName("bookings")
    @Expose
    private ArrayList<BookedList> bookedLists = null;

    public ArrayList<BookedList> getBookedLists() {
        return bookedLists;
    }

    public void setBookedLists(ArrayList<BookedList> bookedLists) {
        this.bookedLists = bookedLists;
    }
}
