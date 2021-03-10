package www.fiberathome.com.parkingapp.ui.changePassword.newPassword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

@SuppressLint("NonConstantResourceId")
public class ChangeNewPasswordActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view)
    View view;

    private Context context;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        context = this;

        unbinder = ButterKnife.bind(this);

        setToolbar();

        // Initialize ForgetPasswordFragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ChangeNewPasswordFragment.newInstance(), R.id.frameLayout);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    ChangeNewPasswordActivity.super.onBackPressed();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                }).create();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        });
        dialog.show();
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

    private void setToolbar() {
        mToolbar.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(context.getResources().getString(R.string.title_change_password));
        mToolbar.setTitleTextColor(context.getResources().getColor(R.color.black));
        if (mToolbar.getNavigationIcon() != null) {
            mToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
