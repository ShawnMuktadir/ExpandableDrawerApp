package www.fiberathome.com.parkingapp.ui.forgetPassword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

@SuppressLint("NonConstantResourceId")
public class ForgetPasswordActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Context context = this;

        unbinder = ButterKnife.bind(this);

        setToolbar(context);

        // Initialize ForgetPasswordFragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                ForgetPasswordFragment.newInstance(), R.id.frameLayout);
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
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    private void setToolbar(Context context) {
        setSupportActionBar(toolbar);
        toolbar.setTitle(context.getResources().getString(R.string.title_forget_password));
        toolbar.setVisibility(View.VISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}