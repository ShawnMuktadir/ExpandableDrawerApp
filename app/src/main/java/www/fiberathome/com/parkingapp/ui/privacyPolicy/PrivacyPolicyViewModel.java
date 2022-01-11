package www.fiberathome.com.parkingapp.ui.privacyPolicy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.termsCondition.TermConditionRepository;
import www.fiberathome.com.parkingapp.data.model.response.termsCondition.TermsConditionResponse;

public class PrivacyPolicyViewModel extends ViewModel {
    private MutableLiveData<TermsConditionResponse> termsConditionResponseMutableLiveData;

    public void getTermCondition() {
        termsConditionResponseMutableLiveData = TermConditionRepository.getInstance().getTermCondition();
    }

    public LiveData<TermsConditionResponse> getTermConditionMutableData() {
        return termsConditionResponseMutableLiveData;
    }
}