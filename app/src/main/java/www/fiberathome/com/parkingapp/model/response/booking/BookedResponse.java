package www.fiberathome.com.parkingapp.model.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;

@SuppressWarnings("unused")
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
