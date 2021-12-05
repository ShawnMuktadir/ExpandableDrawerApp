package www.fiberathome.com.parkingapp.ui.verifyPhone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.databinding.ActivityBaseBinding;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

@SuppressLint("NonConstantResourceId")
public class VerifyPhoneActivity extends BaseActivity {

    ActivityBaseBinding binding;
    protected Context context;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

        setTitle(context.getResources().getString(R.string.verify_otp));

        //Initialize VerifyPhoneFragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                VerifyPhoneFragment.newInstance(), R.id.frameLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivityWithFinishAffinity(LoginActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivityWithFinishAffinity(LoginActivity.class);
    }
}

