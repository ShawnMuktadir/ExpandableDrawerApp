package www.fiberathome.com.parkingapp.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.UniversalSpinnerAdapter;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.BottomSheetDialogAddVehicleBinding;
import www.fiberathome.com.parkingapp.databinding.FragmentProfileBinding;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.profile.edit.EditProfileActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ProfileFragment extends BaseFragment implements IOnBackPressListener {

    private ProfileActivity context;
    FragmentProfileBinding binding;

    private String vehicleClass = "";
    private String vehicleDiv = "";
    private long classId, divId;
    BottomSheetDialogAddVehicleBinding addVehicleBinding;
    private List<www.fiberathome.com.parkingapp.model.Spinner> classDataList;
    private List<www.fiberathome.com.parkingapp.model.Spinner> classDivList;
    private String licencePlateInfo = " ";

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (ProfileActivity) getActivity();
        setListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        User user = Preferences.getInstance(context).getUser();
        setData(user);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    private void setListeners() {
        binding.btnUpdateInfo.setOnClickListener(v -> startActivity(new Intent(context, EditProfileActivity.class)));

        binding.ivAddVehicle.setOnClickListener(view -> {
            try {
                addVehicleBinding = BottomSheetDialogAddVehicleBinding.inflate(getLayoutInflater());
                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(addVehicleBinding.getRoot());
                dialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                dialog.show();
                populateNSubmitVehicleData(dialog, addVehicleBinding);
            } catch (Exception e) {
                // generic exception handling
                e.printStackTrace();
            }
        });
    }

    private void setData(User user) {
        if (getActivity() != null) {
            getActivity().setTitle(user.getFullName());
        }

        String name = user.getFullName();
        name = TextUtils.getInstance().capitalizeFirstLetter(name);
        binding.tvUserName.setText(name);

        binding.tvUserMobileNo.setText(TextUtils.getInstance().addCountryPrefixWithPlus(user.getMobileNo()));

        if (TextUtils.getInstance().isNumeric(Preferences.getInstance(context).getUser().getVehicleNo())) {
            binding.tvUserVehicleNoArmy.setVisibility(View.VISIBLE);
            binding.tvUserVehicleNo.setText(String.format("^%s", user.getVehicleNo().substring(0, 2)));
            binding.tvUserVehicleNoArmy.setText(user.getVehicleNo().substring(2, 6));
        } else {
            binding.tvUserVehicleNo.setText(user.getVehicleNo());
            binding.tvUserVehicleNoArmy.setVisibility(View.GONE);
        }
        if (user.getImage() != null && !user.getImage().equals("")) {
            try {
                if (!user.getImage().endsWith(".jpg")) {
                    String url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
                    Timber.e("Image URL -> %s", url);
                    Glide.with(context).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(binding.ivUserProfilePic);
                } else {
                    String url = AppConfig.IMAGES_URL + user.getImage();
                    Timber.e("Image URL -> %s", url);
                    Glide.with(context).load(url).placeholder(R.drawable.blank_profile).dontAnimate().into(binding.ivUserProfilePic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (user.getVehicleImage() != null && !user.getVehicleImage().equals("")) {
            try {
                if (!user.getVehicleImage().endsWith(".jpg")) {
                    String vehicleUrl = AppConfig.IMAGES_URL + user.getVehicleImage() + ".jpg";
                    Timber.e("Vehicle Image URL -> %s", vehicleUrl);
                    Glide.with(context).load(vehicleUrl).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(binding.ivVehicleProfilePlatePreview);
                } else {
                    String vehicleUrl = AppConfig.IMAGES_URL + user.getVehicleImage();
                    Timber.e("Vehicle Image URL -> %s", vehicleUrl);
                    Glide.with(context).load(vehicleUrl).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(binding.ivVehicleProfilePlatePreview);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateNSubmitVehicleData(BottomSheetDialog dialog, BottomSheetDialogAddVehicleBinding addVehicleBinding) {
        setVehicleClassCategory();
        setVehicleDivCategory();

        addVehicleBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioGeneral:
                    // do operations specific to this selection
                    addVehicleBinding.linearLayoutGeneralFormat.setVisibility(View.VISIBLE);
                    addVehicleBinding.linearLayoutMilitaryFormat.setVisibility(View.GONE);
                    break;
                case R.id.radioMilitary:
                    // do operations specific to this selection
                    addVehicleBinding.linearLayoutGeneralFormat.setVisibility(View.GONE);
                    addVehicleBinding.linearLayoutMilitaryFormat.setVisibility(View.VISIBLE);
                    break;
            }
        });

        addVehicleBinding.buttonSubmit.setOnClickListener(v -> {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                submitAddVehicleInfo(dialog);
            }
        });

        addVehicleBinding.buttonCancel.setOnClickListener(v -> dialog.dismiss());

        Objects.requireNonNull(addVehicleBinding.textInputLayoutCarNumber.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    addVehicleBinding.textInputLayoutCarNumber.setErrorEnabled(true);
                    addVehicleBinding.textInputLayoutCarNumber.setError(context.getResources().getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    addVehicleBinding.textInputLayoutCarNumber.setError(null);
                    addVehicleBinding.textInputLayoutCarNumber.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(addVehicleBinding.textInputLayoutVehicleMilitaryFirstTwoDigit.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    addVehicleBinding.textInputLayoutVehicleMilitaryFirstTwoDigit.setErrorEnabled(true);
                    addVehicleBinding.textInputLayoutVehicleMilitaryFirstTwoDigit.setError(context.getResources().getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    addVehicleBinding.textInputLayoutVehicleMilitaryFirstTwoDigit.setError(null);
                    addVehicleBinding.textInputLayoutVehicleMilitaryFirstTwoDigit.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(addVehicleBinding.textInputLayoutVehicleMilitaryLastFourDigit.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    addVehicleBinding.textInputLayoutVehicleMilitaryLastFourDigit.setErrorEnabled(true);
                    addVehicleBinding.textInputLayoutVehicleMilitaryLastFourDigit.setError(context.getResources().getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    addVehicleBinding.textInputLayoutVehicleMilitaryLastFourDigit.setError(null);
                    addVehicleBinding.textInputLayoutVehicleMilitaryLastFourDigit.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void selectSpinnerItemByValue(Spinner spinner, String value) {
        UniversalSpinnerAdapter adapter = (UniversalSpinnerAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).getValue().equalsIgnoreCase(value)) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    private void setVehicleClassCategory() {
        UniversalSpinnerAdapter vehicleClassAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                populateVehicleClassData());

        addVehicleBinding.classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                classId = id;
                vehicleClass = classDataList.get(position).getValue();
                Preferences.getInstance(context).saveVehicleClassData(vehicleClass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addVehicleBinding.classSpinner.setAdapter(vehicleClassAdapter);
    }

    private List<www.fiberathome.com.parkingapp.model.Spinner> populateVehicleClassData() {
        classDataList = new ArrayList<>();

        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(1, "Dhaka-Metro"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(2, "Chattogram-Metro"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(3, "Bagerhat"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(4, "Barguna"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(5, "Barishal"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(6, "Bandarban"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(7, "Bhola"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(8, "Bogura"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(9, "Brahmanbaria"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(10, "Chapainawabganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(11, "Chandpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(12, "Chattogram"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(13, "Cox's Bazar"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(14, "Cumilla"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(15, "Chuadanga"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(16, "Dhaka"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(17, "Dinajpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(18, "Feni"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(19, "Faridpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(20, "Gaibandha"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(21, "Gazipur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(22, "Gopalganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(23, "Habiganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(24, "Jhalokati"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(25, "Jashore"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(26, "Jhenaidah"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(27, "Jamalpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(28, "Joypurhat"));

        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(29, "Khulna"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(30, "Kurigram"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(31, "Kushtia"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(32, "Khagrachhari"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(33, "Kishoreganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(34, "Lakshmipur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(35, "Lalmonirhat"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(36, "Madaripur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(37, "Manikganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(38, "Magura"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(39, "Meherpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(40, "Moulvibazar"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(41, "Munshiganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(42, "Mymensingh"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(43, "Naogaon"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(44, "Natore"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(45, "Narail"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(46, "Netrokona"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(47, "Narayanganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(48, "Narsingdi"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(49, "Nilphamari"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(50, "Noakhali"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(51, "Pabna"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(52, "Panchagarh"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(53, "Patuakhali"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(54, "Pirojpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(55, "Rajshahi"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(56, "Rangamati"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(57, "Rajbari"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(58, "Rangpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(59, "Shariatpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(60, "Satkhira"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(61, "Sherpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(62, "Sirajganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(63, "Sunamganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(64, "Sylhet"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(65, "Tangail"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(66, "Thakurgaon"));

        return classDataList;
    }

    private void setVehicleDivCategory() {
        UniversalSpinnerAdapter vehicleDivAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                populateVehicleDivData());

        addVehicleBinding.divSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                divId = id;
                vehicleDiv = classDivList.get(position).getValue();
                Preferences.getInstance(context).saveVehicleDivData(vehicleDiv);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addVehicleBinding.divSpinner.setAdapter(vehicleDivAdapter);
    }

    private List<www.fiberathome.com.parkingapp.model.Spinner> populateVehicleDivData() {

        classDivList = new ArrayList<>();

        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(1, "A"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(2, "Ka"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(3, "Kha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(4, "Ga"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(5, "Gha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(6, "Uo"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(7, "Ca"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(8, "Cha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(9, "Ja"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(10, "Jha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(11, "Ta"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(12, "Tha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(13, "D"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(14, "Dha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(15, "Tha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(16, "Da"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(17, "Na"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(18, "Pa"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(19, "Pha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(20, "Ba"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(21, "Bha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(22, "Ma"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(23, "Ja"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(24, "Ra"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(25, "La"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(26, "Sha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(27, "Sa"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(28, "Ha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(29, "E"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(30, "O"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(31, "AU"));

        return classDivList;
    }

    private boolean checkFields() {
        boolean isVehicleRegValid = false;
        boolean isVehicleRegValidForFirstTwoDigit = false;
        boolean isVehicleRegValidForLastFourDigit = false;
        boolean isLicencePlateValid = false;

        if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
            isVehicleRegValid = Validator.checkValidity(addVehicleBinding.textInputLayoutCarNumber, addVehicleBinding.editTextCarNumber.getText().toString(), context.getResources().getString(R.string.err_msg_vehicle), "vehicleNumber");
        } else {
            isVehicleRegValidForFirstTwoDigit = Validator.checkValidity(addVehicleBinding.textInputLayoutVehicleMilitaryFirstTwoDigit, addVehicleBinding.editTextVehicleRegNumberMilitaryFirstTwoDigit.getText().toString(), context.getResources().getString(R.string.err_msg_vehicle), "vehicleMilitaryNumberForFirstTwo");
            isVehicleRegValidForLastFourDigit = Validator.checkValidity(addVehicleBinding.textInputLayoutVehicleMilitaryLastFourDigit, addVehicleBinding.editTextVehicleRegNumberMilitaryLastFourDigit.getText().toString(), context.getResources().getString(R.string.err_msg_vehicle), "vehicleMilitaryNumberForLastFour");
        }

        if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
            licencePlateInfo = addVehicleBinding.editTextVehicleRegNumberMilitaryFirstTwoDigit.getText().toString().trim() +
                    addVehicleBinding.editTextVehicleRegNumberMilitaryLastFourDigit.getText().toString().trim();
        }

        if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
            if (!vehicleClass.isEmpty() && !vehicleClass.equalsIgnoreCase("Select") && !vehicleDiv.isEmpty() && !vehicleDiv.equalsIgnoreCase("Select")) {
                isLicencePlateValid = true;
            }
        } else if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
            isLicencePlateValid = !licencePlateInfo.equalsIgnoreCase("000000");
        } else {
            Toast.makeText(context, "Please give valid vehicle number", Toast.LENGTH_SHORT).show();
        }

        if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
            return isVehicleRegValid && isLicencePlateValid;
        } else if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
            return isVehicleRegValidForFirstTwoDigit && isVehicleRegValidForLastFourDigit && isLicencePlateValid;
        } else {
            return false;
        }
    }

    private void submitAddVehicleInfo(BottomSheetDialog dialog) {
        if (checkFields()) {
            String vehicleNo = addVehicleBinding.editTextCarNumber.getText().toString().trim();
            String licencePlateInfo;

            if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
                licencePlateInfo = vehicleClass + " " + vehicleDiv + " " + vehicleNo;
                String temp = "" + vehicleNo.charAt(0) + vehicleNo.charAt(1);
                int vehicleNoInt = MathUtils.getInstance().convertToInt(temp);

                String tempForOther = "" + vehicleNo.charAt(4) + vehicleNo.charAt(5);
                int vehicleNoIntForOther = MathUtils.getInstance().convertToInt(tempForOther);

                if (vehicleNoInt < 11 || (vehicleDiv.equalsIgnoreCase("E") && vehicleNoIntForOther > 60) ||
                        (vehicleDiv.equalsIgnoreCase("Ma") && vehicleClass.equalsIgnoreCase("Munshiganj") && vehicleNoIntForOther > 50) ||
                        (vehicleDiv.equalsIgnoreCase("Ma") && vehicleClass.equalsIgnoreCase("Narayanganj") && vehicleNoInt < 51)) {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.invalid_vehicle_number));
                } else {
                    submitAddVehicle(Preferences.getInstance(context).getUser().getMobileNo(), licencePlateInfo);
                }
            } else if (addVehicleBinding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
                licencePlateInfo = context.getResources().getString(R.string.army_vehicle_arrow) + addVehicleBinding.editTextVehicleRegNumberMilitaryFirstTwoDigit.getText().toString().trim() +
                        addVehicleBinding.editTextVehicleRegNumberMilitaryLastFourDigit.getText().toString().trim();
                submitAddVehicle(Preferences.getInstance(context).getUser().getMobileNo(), licencePlateInfo);
            }
            dialog.dismiss();
        }
    }

    private void submitAddVehicle(String mobileNo, String licencePlateInfo) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> call = request.setUserVehicle(mobileNo, licencePlateInfo);
        call.enqueue(new Callback<BaseResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
    }
}
