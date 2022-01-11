package www.fiberathome.com.parkingapp.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.search.SearchRepository;
import www.fiberathome.com.parkingapp.data.model.response.search.SearchVisitedPlaceResponse;

public class SearchViewModel extends ViewModel {
    private MutableLiveData<BaseResponse> storeVisitedPlaceData;
    private MutableLiveData<SearchVisitedPlaceResponse> visitedPlaceResponseMutableLiveData;

    public void storeVisitedPlaceInit(String mobileNo, String placeId, String endLatitude, String endLongitude,
                                      String startLatitude, String startLongitude, String areaAddress) {
        storeVisitedPlaceData = SearchRepository.getInstance().storeVisitedPlace(mobileNo, placeId, endLatitude, endLongitude,
                startLatitude, startLongitude, areaAddress);
    }

    public LiveData<BaseResponse> getStoreVisitedMutableData() {
        return storeVisitedPlaceData;
    }

    public void getSearchHistoryInit(String mobileNo) {
        visitedPlaceResponseMutableLiveData = SearchRepository.getInstance().getSearchHistory(mobileNo);
    }

    public LiveData<SearchVisitedPlaceResponse> getSearchHistoryMutableData() {
        return visitedPlaceResponseMutableLiveData;
    }


}
