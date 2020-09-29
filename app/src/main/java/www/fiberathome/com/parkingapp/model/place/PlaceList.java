package www.fiberathome.com.parkingapp.model.place;

import java.util.ArrayList;
import java.util.List;

public class PlaceList {

    public List<Place> places;
    public String next_page_token;

    public PlaceList(){
        places = new ArrayList<Place>();
    }

    public boolean canLoadMore(){
        return next_page_token != null;
    }
}
