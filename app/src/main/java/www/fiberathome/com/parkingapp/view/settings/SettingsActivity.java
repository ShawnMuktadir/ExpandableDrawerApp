package www.fiberathome.com.parkingapp.view.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.view.main.MainActivity;

import static www.fiberathome.com.parkingapp.utils.LocaleManager.LANGUAGE_BANGLA;
import static www.fiberathome.com.parkingapp.utils.LocaleManager.LANGUAGE_ENGLISH;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.textViewBan)
    TextView textViewBan;
    @BindView(R.id.textViewEng)
    TextView textViewEng;

    private Unbinder unbinder;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        unbinder = ButterKnife.bind(this);
        context = this;

        //noinspection ConstantConditions
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setListeners();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    private void setListeners() {
        textViewBan.setOnClickListener(v -> {
            Timber.e("textViewBan clicked");
            ApplicationUtils.showAlertDialog(context.getString(R.string.change_language), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {
                setNewLocale(LANGUAGE_BANGLA, false);
            }, (dialog, which) -> {
                dialog.dismiss();
            });
        });

        textViewEng.setOnClickListener(v -> {
            Timber.e("textViewEng clicked");
            ApplicationUtils.showAlertDialog(context.getString(R.string.change_language), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {
                setNewLocale(LANGUAGE_ENGLISH, false);
            }, (dialog, which) -> {
                dialog.dismiss();
            });
        });
    }

    private boolean setNewLocale(String language, boolean restartProcess) {
        ParkingApp.localeManager.setNewLocale(context, language);

        Intent i = new Intent(context, MainActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        if (restartProcess) {
            System.exit(0);
        } else {
            Toast.makeText(context, "Activity restarted", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
