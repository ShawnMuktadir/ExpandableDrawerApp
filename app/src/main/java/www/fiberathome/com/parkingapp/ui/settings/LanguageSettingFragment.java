package www.fiberathome.com.parkingapp.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.Language;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.settings.adapter.LanguageAdapter;
import www.fiberathome.com.parkingapp.ui.splash.SplashActivity;

import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_EN;

@SuppressLint("NonConstantResourceId")
public class LanguageSettingFragment extends Fragment {

    @BindView(R.id.recyclerView_settings)
    RecyclerView recyclerViewLanguages;

    private Unbinder unbinder;

    private LanguageSettingActivity context;

    public LanguageSettingFragment() {
        // Required empty public constructor
    }

    public static LanguageSettingFragment newInstance() {
        LanguageSettingFragment fragment = new LanguageSettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = (LanguageSettingActivity) getActivity();

        initView(view);
    }

    @Override
    public void onDestroyView() {

        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }

    private void initView(View view) {
        unbinder = ButterKnife.bind(this, view);

        String[] names = new String[]{context.getResources().getString(R.string.english), context.getResources().getString(R.string.bangla)};
        String[] isoCodes = new String[]{LANGUAGE_EN, LANGUAGE_BN};

        LanguageAdapter languageAdapter = new LanguageAdapter(
                populateLanguageItem(names, isoCodes, new ArrayList<>()), (language) -> {

            if (Preferences.getInstance(context).getAppLanguage() != null) {
                Preferences.getInstance(context).setAppLanguage(language.getIsoCode());
            } else {
                Preferences.getInstance(context).setAppLanguage(LANGUAGE_EN);
            }
            context.startActivityWithFinishAffinity(SplashActivity.class);
        });

        recyclerViewLanguages.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewLanguages.setItemAnimator(new DefaultItemAnimator());
        recyclerViewLanguages.setAdapter(languageAdapter);
    }

    private List<Language> populateLanguageItem(String[] names, String[] isoCodes, List<Language> languages) {
        for (int i = 0; i < names.length; i++) {
            languages.add(new Language(
                    names[i], isoCodes[i], isoCodes[i].equalsIgnoreCase(Preferences.getInstance(context).getAppLanguage())
            ));
        }

        return languages;
    }
}
