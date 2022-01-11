package www.fiberathome.com.parkingapp.ui.auth.forgotPassword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.databinding.ActivityBaseBinding;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

@SuppressLint("NonConstantResourceId")
public class ForgotPasswordActivity extends BaseActivity {

    ActivityBaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Context context = this;
        setToolbar(context);

        // Initialize ForgetPasswordFragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ForgotPasswordFragment.newInstance(), R.id.frameLayout);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setToolbar(Context context) {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(context.getResources().getString(R.string.title_forget_password));
        binding.toolbar.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}