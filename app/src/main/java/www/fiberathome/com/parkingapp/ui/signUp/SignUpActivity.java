package www.fiberathome.com.parkingapp.ui.signUp;

import android.os.Bundle;
import android.view.View;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.databinding.ActivityBaseBinding;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class SignUpActivity extends BaseActivity {

    ActivityBaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Initialize SignUp fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                SignUpFragment.newInstance(), R.id.frameLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
