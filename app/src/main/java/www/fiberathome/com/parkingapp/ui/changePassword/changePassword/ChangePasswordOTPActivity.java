package www.fiberathome.com.parkingapp.ui.changePassword.changePassword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.databinding.ActivityBaseBinding;
import www.fiberathome.com.parkingapp.ui.login.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
public class ChangePasswordOTPActivity extends BaseActivity {

    private Context context;

    ActivityBaseBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;
        setToolbar();

        // Initialize ChangePasswordFragmentForOTP
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ChangePasswordOTPFragment.newInstance(), R.id.frameLayout);
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
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        DialogUtils.getInstance().alertDialog(context,
                (Activity) context,
                context.getResources().getString(R.string.are_you_sure_you_want_to_exit),
                context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        finishAffinity();
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                    }

                    @Override
                    public void onNegativeClick() {
                        //null for this
                    }
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setToolbar() {
        binding.toolbar.setVisibility(View.VISIBLE);
        binding.view.setVisibility(View.VISIBLE);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(context.getResources().getString(R.string.verify_otp));
        binding.toolbar.setTitleTextColor(context.getResources().getColor(R.color.black));
        if (binding.toolbar.getNavigationIcon() != null) {
            binding.toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
