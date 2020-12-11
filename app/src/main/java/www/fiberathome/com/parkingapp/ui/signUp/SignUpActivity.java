package www.fiberathome.com.parkingapp.ui.signUp;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;

import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

public class SignUpActivity extends BaseActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        context = this;

        // Initialize splash fragment
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    //SignUpActivity.super.onBackPressed();
                    finish();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                }).create();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
            //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
        });
        dialog.show();
    }
}
